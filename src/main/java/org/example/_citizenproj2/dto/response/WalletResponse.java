package org.example._citizenproj2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example._citizenproj2.model.Wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
    private Long walletId;
    private Long memberId;
    private BigDecimal balance;
    private BigDecimal totalDeposit;
    private BigDecimal totalSpent;
    private String walletStatus;
    private LocalDateTime lastTransactionTime;

    // 擴展資訊
    private TransactionSummary transactionSummary;
    private SecurityInfo securityInfo;
    private LimitInfo limitInfo;
    private List<TransactionResponse> recentTransactions;

    @Data
    @Builder
    public static class TransactionSummary {
        private BigDecimal todaySpent;
        private BigDecimal todayReceived;
        private BigDecimal monthlySpent;
        private BigDecimal monthlyReceived;
        private Integer totalTransactions;
        private LocalDateTime firstTransactionDate;
    }

    @Data
    @Builder
    public static class SecurityInfo {
        private Boolean isLocked;
        private Boolean hasTwoFactor;
        private LocalDateTime lastPasswordChange;
        private String securityLevel;
        private List<String> authorizedDevices;
    }

    @Data
    @Builder
    public static class LimitInfo {
        private BigDecimal dailyTransactionLimit;
        private BigDecimal singleTransactionLimit;
        private BigDecimal remainingDailyLimit;
        private Boolean isLimitTemporarilyIncreased;
        private LocalDateTime limitResetTime;
    }

    // 靜態工廠方法
    public static WalletResponse fromEntity(Wallet wallet) {
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

    // 業務方法
    public boolean isActive() {
        return "ACTIVE".equals(walletStatus);
    }

    public boolean canTransact(BigDecimal amount) {
        if (!isActive()) {
            return false;
        }
        if (limitInfo != null) {
            return amount.compareTo(limitInfo.getRemainingDailyLimit()) <= 0 &&
                    amount.compareTo(limitInfo.getSingleTransactionLimit()) <= 0;
        }
        return true;
    }

    public boolean needsSecurityUpgrade() {
        if (securityInfo != null) {
            return !securityInfo.getHasTwoFactor() ||
                    (securityInfo.getLastPasswordChange() != null &&
                            securityInfo.getLastPasswordChange().plusMonths(3).isBefore(LocalDateTime.now()));
        }
        return false;
    }

    public String getActivityLevel() {
        if (transactionSummary == null || transactionSummary.getTotalTransactions() == null) {
            return "NEW";
        }
        int totalTransactions = transactionSummary.getTotalTransactions();
        if (totalTransactions > 100) return "VERY_ACTIVE";
        if (totalTransactions > 50) return "ACTIVE";
        if (totalTransactions > 10) return "REGULAR";
        return "OCCASIONAL";
    }
}