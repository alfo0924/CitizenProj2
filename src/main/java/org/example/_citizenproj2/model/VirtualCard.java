package org.example._citizenproj2.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "virtual_cards")
@Data  // 已有，但確保存在
@Getter // 明確添加
@Setter // 明確添加
@NoArgsConstructor
@AllArgsConstructor
public class VirtualCard {
    @Id
    @Column(length = 36)
    private String virtualCardId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 20)
    private String boundPhoneNumber;

    @Enumerated(EnumType.STRING)
    private CardStatus cardStatus = CardStatus.ACTIVE;

    @Column(precision = 10, scale = 2)
    private BigDecimal dailyLimit = new BigDecimal("5000.00");

    private LocalDateTime lastUsedTime;

    @Column(nullable = false)
    private LocalDateTime activationDate;

    private LocalDateTime expiryDate;

    @Column(nullable = false, length = 6)
    private String securityCode;

    @Column(columnDefinition = "TEXT")
    private String deviceInfo;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum CardStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }

    // 業務方法
    public boolean isValid() {
        return cardStatus == CardStatus.ACTIVE &&
                (expiryDate == null || LocalDateTime.now().isBefore(expiryDate));
    }

    public boolean canMakeTransaction(BigDecimal amount) {
        return isValid() && amount.compareTo(dailyLimit) <= 0;
    }

    @PrePersist
    public void prePersist() {
        if (activationDate == null) {
            activationDate = LocalDateTime.now();
        }
        if (expiryDate == null) {
            expiryDate = activationDate.plusYears(5);
        }
    }

    // 自定義驗證方法
    public void validateSecurityCode(String inputCode) {
        if (!securityCode.equals(inputCode)) {
            throw new IllegalArgumentException("Invalid security code");
        }
    }

    public void updateLastUsedTime() {
        this.lastUsedTime = LocalDateTime.now();
    }
}