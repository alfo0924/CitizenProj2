package org.example._citizenproj2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example._citizenproj2.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private String transactionId;
    private Long walletId;
    private BigDecimal amount;
    private BigDecimal balance;
    private String transactionType;
    private String paymentMethod;
    private String description;
    private String referenceId;
    private String status;
    private LocalDateTime transactionTime;

    // 擴展資訊
    private SecurityInfo securityInfo;
    private VerificationInfo verificationInfo;
    private AuditInfo auditInfo;

    @Data
    @Builder
    public static class SecurityInfo {
        private String deviceInfo;
        private String ipAddress;
        private String location;
        private Boolean isVerified;
    }

    @Data
    @Builder
    public static class VerificationInfo {
        private String verificationStatus;
        private LocalDateTime verifiedAt;
        private String verifiedBy;
        private String verificationMethod;
    }

    @Data
    @Builder
    public static class AuditInfo {
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String processedBy;
        private String remarks;
    }

    // 靜態工廠方法
    public static TransactionResponse fromEntity(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .walletId(transaction.getWallet().getWalletId())
                .amount(transaction.getAmount())
                .balance(transaction.getBalance())
                .transactionType(transaction.getTransactionType().toString())
                .paymentMethod(transaction.getPaymentMethod())
                .description(transaction.getDescription())
                .referenceId(transaction.getReferenceId())
                .status(transaction.getStatus().toString())
                .transactionTime(transaction.getTransactionTime())
                .auditInfo(AuditInfo.builder()
                        .createdAt(transaction.getCreatedAt())
                        .updatedAt(transaction.getUpdatedAt())
                        .build())
                .build();
    }

    // 業務方法
    public boolean isSuccessful() {
        return "COMPLETED".equals(status);
    }

    public boolean isRefundable() {
        return isSuccessful() &&
                amount.compareTo(BigDecimal.ZERO) < 0 &&
                transactionTime.plusDays(30).isAfter(LocalDateTime.now());
    }

    public boolean needsVerification() {
        return amount.abs().compareTo(new BigDecimal("10000")) > 0;
    }
}