package org.example._citizenproj2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wallets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalDeposit = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletStatus walletStatus = WalletStatus.ACTIVE;

    private LocalDateTime lastTransactionTime;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void setMemberMemberId(Long memberId) {

    }

    public enum WalletStatus {
        ACTIVE, SUSPENDED, CLOSED
    }

    // 業務方法
    public boolean isActive() {
        return walletStatus == WalletStatus.ACTIVE;
    }

    public boolean hasEnoughBalance(BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("存款金額必須大於0");
        }
        this.balance = this.balance.add(amount);
        this.totalDeposit = this.totalDeposit.add(amount);
        this.lastTransactionTime = LocalDateTime.now();
    }

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("提款金額必須大於0");
        }
        if (!hasEnoughBalance(amount)) {
            throw new IllegalStateException("餘額不足");
        }
        this.balance = this.balance.subtract(amount);
        this.totalSpent = this.totalSpent.add(amount);
        this.lastTransactionTime = LocalDateTime.now();
    }

    public void suspend() {
        if (walletStatus != WalletStatus.ACTIVE) {
            throw new IllegalStateException("錢包狀態不正確");
        }
        this.walletStatus = WalletStatus.SUSPENDED;
    }

    public void activate() {
        if (walletStatus != WalletStatus.SUSPENDED) {
            throw new IllegalStateException("錢包狀態不正確");
        }
        this.walletStatus = WalletStatus.ACTIVE;
    }

    public void close() {
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("錢包還有餘額");
        }
        this.walletStatus = WalletStatus.CLOSED;
    }

    // 預處理方法
    @PrePersist
    public void prePersist() {
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
        if (totalDeposit == null) {
            totalDeposit = BigDecimal.ZERO;
        }
        if (totalSpent == null) {
            totalSpent = BigDecimal.ZERO;
        }
        if (walletStatus == null) {
            walletStatus = WalletStatus.ACTIVE;
        }
    }
}