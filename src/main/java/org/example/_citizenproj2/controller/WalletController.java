package org.example._citizenproj2.controller;


import org.example._citizenproj2.dto.request.TransferRequest;
import org.example._citizenproj2.exception.InsufficientBalanceException;
import org.example._citizenproj2.exception.WalletNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example._citizenproj2.dto.response.TransactionResponse;
import org.example._citizenproj2.dto.response.WalletResponse;
import org.example._citizenproj2.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/{memberId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable Long memberId) {
        return ResponseEntity.ok(walletService.getWalletByMemberId(memberId));
    }

    @PostMapping("/{walletId}/deposit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TransactionResponse> deposit(
            @PathVariable Long walletId,
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(walletService.deposit(walletId, request));
    }

    @PostMapping("/{walletId}/withdraw")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TransactionResponse> withdraw(
            @PathVariable Long walletId,
            @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(walletService.withdraw(walletId, request));
    }

    @GetMapping("/{walletId}/transactions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @PathVariable Long walletId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(walletService.getTransactions(walletId, page, size));
    }

    @GetMapping("/{walletId}/balance")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long walletId) {
        return ResponseEntity.ok(walletService.getBalance(walletId));
    }

    @PostMapping("/{walletId}/transfer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TransactionResponse> transfer(
            @PathVariable Long walletId,
            @Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(walletService.transfer(walletId, request));
    }

    @PutMapping("/{walletId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WalletResponse> updateWalletStatus(
            @PathVariable Long walletId,
            @RequestParam String status) {
        return ResponseEntity.ok(walletService.updateWalletStatus(walletId, status));
    }

    @GetMapping("/{walletId}/statement")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TransactionResponse>> getStatement(
            @PathVariable Long walletId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return ResponseEntity.ok(walletService.getStatement(walletId, startDate, endDate));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<String> handleWalletNotFoundException(WalletNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}