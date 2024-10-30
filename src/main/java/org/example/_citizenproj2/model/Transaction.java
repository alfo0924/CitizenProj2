package org.example._citizenproj2.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data  // 已有，但確保存在
@Getter // 明確添加
@Setter // 明確添加
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @Column(length = 50)
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    private String paymentMethod;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String referenceId;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Column(nullable = false)
    private LocalDateTime transactionTime;

    @Column(length = 8)
    private String verificationCode;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, PAYMENT, REFUND, TRANSFER_IN, TRANSFER_OUT, ADJUSTMENT
    }

    public enum Status {
        PENDING, COMPLETED, FAILED, CANCELLED
    }

    public enum VerificationStatus {
        PENDING, VERIFIED, FAILED
    }

    // 業務方法
    public boolean isSuccess() {
        return status == Status.COMPLETED;
    }

    public boolean canCancel() {
        return status == Status.PENDING;
    }

    public boolean needsVerification() {
        return amount.compareTo(new BigDecimal("10000")) > 0;
    }
}