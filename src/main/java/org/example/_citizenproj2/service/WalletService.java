package org.example._citizenproj2.service;

import lombok.RequiredArgsConstructor;
import org.example._citizenproj2.dto.request.TransactionRequest;
import org.example._citizenproj2.dto.response.TransactionResponse;
import org.example._citizenproj2.dto.response.WalletResponse;
import org.example._citizenproj2.exception.InsufficientBalanceException;
import org.example._citizenproj2.exception.WalletException;
import org.example._citizenproj2.model.Transaction;
import org.example._citizenproj2.model.Wallet;
import org.example._citizenproj2.repository.TransactionRepository;
import org.example._citizenproj2.repository.WalletRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public WalletResponse getWalletByMemberId(Long memberId) {
        Wallet wallet = walletRepository.findByMemberMemberId(memberId)
                .orElseThrow(() -> new WalletException("錢包不存在"));
        return convertToWalletResponse(wallet);
    }

    @Transactional
    public TransactionResponse deposit(Long walletId, TransactionRequest request) {
        Wallet wallet = getWalletById(walletId);
        validateWalletStatus(wallet);

        Transaction transaction = createTransaction(
                wallet,
                request.getAmount(),
                Transaction.TransactionType.DEPOSIT,
                request.getDescription()
        );

        updateWalletBalance(wallet, request.getAmount());

        return convertToTransactionResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse withdraw(Long walletId, TransactionRequest request) {
        Wallet wallet = getWalletById(walletId);
        validateWalletStatus(wallet);
        validateBalance(wallet, request.getAmount());

        Transaction transaction = createTransaction(
                wallet,
                request.getAmount().negate(),
                Transaction.TransactionType.WITHDRAWAL,
                request.getDescription()
        );

        updateWalletBalance(wallet, request.getAmount().negate());

        return convertToTransactionResponse(transactionRepository.save(transaction));
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(Long walletId, int page, int size) {
        return transactionRepository.findByWalletWalletId(walletId, PageRequest.of(page, size))
                .stream()
                .map(this::convertToTransactionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long walletId) {
        return walletRepository.findById(walletId)
                .map(Wallet::getBalance)
                .orElseThrow(() -> new WalletException("錢包不存在"));
    }

    @Transactional
    public boolean hasEnoughBalance(Long memberId, BigDecimal amount) {
        return walletRepository.findByMemberMemberId(memberId)
                .map(wallet -> wallet.getBalance().compareTo(amount) >= 0)
                .orElse(false);
    }

    @Transactional
    public TransactionResponse processPayment(Long memberId, BigDecimal amount, String referenceId) {
        Wallet wallet = walletRepository.findByMemberMemberId(memberId)
                .orElseThrow(() -> new WalletException("錢包不存在"));

        validateWalletStatus(wallet);
        validateBalance(wallet, amount);

        Transaction transaction = createTransaction(
                wallet,
                amount.negate(),
                Transaction.TransactionType.PAYMENT,
                "訂票付款"
        );
        transaction.setReferenceId(referenceId);

        updateWalletBalance(wallet, amount.negate());

        return convertToTransactionResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse processRefund(Long memberId, BigDecimal amount, String referenceId) {
        Wallet wallet = walletRepository.findByMemberMemberId(memberId)
                .orElseThrow(() -> new WalletException("錢包不存在"));

        Transaction transaction = createTransaction(
                wallet,
                amount,
                Transaction.TransactionType.REFUND,
                "訂票退款"
        );
        transaction.setReferenceId(referenceId);

        updateWalletBalance(wallet, amount);

        return convertToTransactionResponse(transactionRepository.save(transaction));
    }

    private Wallet getWalletById(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletException("錢包不存在"));
    }

    private void validateWalletStatus(Wallet wallet) {
        if (wallet.getWalletStatus() != Wallet.WalletStatus.ACTIVE) {
            throw new WalletException("錢包狀態不可用");
        }
    }

    private void validateBalance(Wallet wallet, BigDecimal amount) {
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("餘額不足");
        }
    }

    private Transaction createTransaction(
            Wallet wallet,
            BigDecimal amount,
            Transaction.TransactionType type,
            String description) {

        Transaction transaction = new Transaction();
        transaction.setTransactionId(generateTransactionId());
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setBalance(wallet.getBalance().add(amount));
        transaction.setTransactionType(type);
        transaction.setDescription(description);
        transaction.setStatus(Transaction.Status.COMPLETED);
        transaction.setTransactionTime(LocalDateTime.now());

        return transaction;
    }

    private void updateWalletBalance(Wallet wallet, BigDecimal amount) {
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setLastTransactionTime(LocalDateTime.now());
        walletRepository.save(wallet);
    }

    private String generateTransactionId() {
        return "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private WalletResponse convertToWalletResponse(Wallet wallet) {
        return WalletResponse.builder()
                .walletId(wallet.getWalletId())
                .memberId(wallet.getMember().getMemberId())
                .balance(wallet.getBalance())
                .walletStatus(wallet.getWalletStatus().toString())
                .lastTransactionTime(wallet.getLastTransactionTime())
                .build();
    }

    private TransactionResponse convertToTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .walletId(transaction.getWallet().getWalletId())
                .amount(transaction.getAmount())
                .balance(transaction.getBalance())
                .transactionType(transaction.getTransactionType().toString())
                .status(transaction.getStatus().toString())
                .transactionTime(transaction.getTransactionTime())
                .description(transaction.getDescription())
                .referenceId(transaction.getReferenceId())
                .build();
    }
}