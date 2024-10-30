package org.example._citizenproj2.service;

import lombok.RequiredArgsConstructor;
import org.example._citizenproj2.dto.request.TransactionRequest;
import org.example._citizenproj2.dto.request.TransferRequest;
import org.example._citizenproj2.dto.response.TransactionResponse;
import org.example._citizenproj2.dto.response.WalletResponse;
import org.example._citizenproj2.exception.InsufficientBalanceException;
import org.example._citizenproj2.exception.WalletException;
import org.example._citizenproj2.model.Transaction;
import org.example._citizenproj2.model.Wallet;
import org.example._citizenproj2.repository.TransactionRepository;
import org.example._citizenproj2.repository.WalletRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void createWallet(Long memberId) {
        if (walletRepository.existsByMemberMemberId(memberId)) {
            throw new WalletException("錢包已存在");
        }

        Wallet wallet = new Wallet();
        wallet.setMemberMemberId(memberId);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setTotalDeposit(BigDecimal.ZERO);
        wallet.setTotalSpent(BigDecimal.ZERO);
        wallet.setWalletStatus(Wallet.WalletStatus.ACTIVE);

        convertToWalletResponse(walletRepository.save(wallet));
    }

    public WalletResponse getWalletByMemberId(Long memberId) {
        Wallet wallet = (Wallet) walletRepository.findByMemberMemberId(memberId)
                .orElseThrow(() -> new WalletException("錢包不存在"));
        return convertToWalletResponse(wallet);
    }

    @Transactional
    public TransactionResponse deposit(Long walletId, TransactionRequest request) {
        Wallet wallet = getWalletById(walletId);
        validateWalletStatus(wallet);
        validateDepositAmount(request.getAmount());

        Transaction transaction = createTransaction(
                wallet,
                request.getAmount(),
                Transaction.TransactionType.DEPOSIT,
                request.getDescription()
        );

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        wallet.setTotalDeposit(wallet.getTotalDeposit().add(request.getAmount()));
        wallet.setLastTransactionTime(LocalDateTime.now());
        walletRepository.save(wallet);

        return convertToTransactionResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse withdraw(Long walletId, TransactionRequest request) {
        Wallet wallet = getWalletById(walletId);
        validateWalletStatus(wallet);
        validateWithdrawalAmount(wallet, request.getAmount());

        Transaction transaction = createTransaction(
                wallet,
                request.getAmount().negate(),
                Transaction.TransactionType.WITHDRAWAL,
                request.getDescription()
        );

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        wallet.setTotalSpent(wallet.getTotalSpent().add(request.getAmount()));
        wallet.setLastTransactionTime(LocalDateTime.now());
        walletRepository.save(wallet);

        return convertToTransactionResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public void processPayment(Long memberId, BigDecimal amount, String referenceId) {
        Wallet wallet = (Wallet) walletRepository.findByMemberMemberId(memberId)
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

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setTotalSpent(wallet.getTotalSpent().add(amount));
        wallet.setLastTransactionTime(LocalDateTime.now());
        walletRepository.save(wallet);

        convertToTransactionResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse processRefund(Long memberId, BigDecimal amount, String referenceId) {
        Wallet wallet = (Wallet) walletRepository.findByMemberMemberId(memberId)
                .orElseThrow(() -> new WalletException("錢包不存在"));

        Transaction transaction = createTransaction(
                wallet,
                amount,
                Transaction.TransactionType.REFUND,
                "訂票退款"
        );
        transaction.setReferenceId(referenceId);

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setLastTransactionTime(LocalDateTime.now());
        walletRepository.save(wallet);

        return convertToTransactionResponse(transactionRepository.save(transaction));
    }

    public Page<TransactionResponse> getTransactionHistory(Long walletId, Pageable pageable) {
        return transactionRepository.findByWalletWalletId(walletId, pageable)
                .map(this::convertToTransactionResponse);
    }

    public Map<String, Object> getWalletStatistics(Long walletId) {
        return (Map<String, Object>) transactionRepository.getTransactionStatistics(walletId);
    }

    public boolean hasEnoughBalance(Long memberId, BigDecimal amount) {
        return walletRepository.findByMember_MemberId(memberId)
                .filter(wallet -> wallet.getWalletStatus() == Wallet.WalletStatus.ACTIVE)
                .map(wallet -> wallet.getBalance().compareTo(amount) >= 0)
                .orElse(false);
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
            throw new InsufficientBalanceException(
                    String.format("餘額不足。當前餘額: %s, 需要金額: %s",
                            wallet.getBalance().toString(),
                            amount.toString()),
                    wallet.getBalance(),
                    amount
            );
        }
    }
    private void validateDepositAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new WalletException("存款金額必須大於0");
        }
        if (amount.compareTo(new BigDecimal("100000")) > 0) {
            throw new WalletException("單筆存款不能超過100000");
        }
    }

    private void validateWithdrawalAmount(Wallet wallet, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new WalletException("提款金額必須大於0");
        }
        if (amount.compareTo(new BigDecimal("50000")) > 0) {
            throw new WalletException("單筆提款不能超過50000");
        }
        validateBalance(wallet, amount);
    }

    private Transaction createTransaction(
            Wallet wallet,
            BigDecimal amount,
            Transaction.TransactionType type,
            String description) {

        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN" + UUID.randomUUID().toString().substring(0, 8));
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setBalance(wallet.getBalance().add(amount));
        transaction.setTransactionType(type);
        transaction.setDescription(description);
        transaction.setStatus(Transaction.Status.COMPLETED);
        transaction.setTransactionTime(LocalDateTime.now());

        return transaction;
    }

    private WalletResponse convertToWalletResponse(Wallet wallet) {
        return WalletResponse.builder()
                .walletId(wallet.getWalletId())
                .memberId(wallet.getMember().getMemberId())
                .balance(wallet.getBalance())
                .totalDeposit(wallet.getTotalDeposit())
                .totalSpent(wallet.getTotalSpent())
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
    public BigDecimal getBalance(Long walletId) {
        Wallet wallet = getWalletById(walletId);
        return wallet.getBalance();
    }

    @Transactional
    public TransactionResponse transfer(Long walletId, TransferRequest request) {
        Wallet senderWallet = getWalletById(walletId);
        Wallet receiverWallet = (Wallet) walletRepository.findByMemberMemberId(request.getReceiverMemberId())
                .orElseThrow(() -> new WalletException("收款方錢包不存在"));

        validateWalletStatus(senderWallet);
        validateWalletStatus(receiverWallet);
        validateBalance(senderWallet, request.getAmount());

        // 建立轉出交易
        Transaction senderTransaction = createTransaction(
                senderWallet,
                request.getAmount().negate(),
                Transaction.TransactionType.TRANSFER_OUT,
                "轉帳給 " + receiverWallet.getMember().getEmail()
        );

        // 建立轉入交易
        Transaction receiverTransaction = createTransaction(
                receiverWallet,
                request.getAmount(),
                Transaction.TransactionType.TRANSFER_IN,
                "來自 " + senderWallet.getMember().getEmail() + " 的轉帳"
        );

        // 更新雙方餘額
        senderWallet.setBalance(senderWallet.getBalance().subtract(request.getAmount()));
        receiverWallet.setBalance(receiverWallet.getBalance().add(request.getAmount()));

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);
        transactionRepository.save(receiverTransaction);

        return convertToTransactionResponse(transactionRepository.save(senderTransaction));
    }

    @Transactional
    public WalletResponse updateWalletStatus(Long walletId, String status) {
        Wallet wallet = getWalletById(walletId);
        wallet.setWalletStatus(Wallet.WalletStatus.valueOf(status.toUpperCase()));
        return convertToWalletResponse(walletRepository.save(wallet));
    }

    public List<TransactionResponse> getStatement(Long walletId, String startDate, String endDate) {
        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate).atTime(23, 59, 59);

        return transactionRepository.findByWalletWalletIdAndTransactionTimeBetween(
                        walletId, start, end)
                .stream()
                .map(this::convertToTransactionResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactions(Long walletId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("transactionTime").descending());
        return transactionRepository.findByWalletWalletId(walletId, pageRequest)
                .getContent()
                .stream()
                .map(this::convertToTransactionResponse)
                .collect(Collectors.toList());
    }
}