package org.example._citizenproj2.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "citizen_cards")
@Data  // 已有，但確保存在
@Getter // 明確添加
@Setter // 明確添加
@NoArgsConstructor
@AllArgsConstructor
public class CitizenCard {
    @Id
    @Column(length = 20)
    private String cardNumber;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(nullable = false, length = 100)
    private String holderName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType cardType = CardType.REGULAR;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    private CardStatus cardStatus = CardStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String encryptedData;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum CardType {
        REGULAR, STUDENT, SENIOR, DISABILITY
    }

    public enum CardStatus {
        ACTIVE, INACTIVE, LOST, EXPIRED
    }

    // 業務方法
    public boolean isValid() {
        return cardStatus == CardStatus.ACTIVE &&
                LocalDate.now().isBefore(expiryDate);
    }

    public boolean needsRenewal() {
        return LocalDate.now().plusMonths(1).isAfter(expiryDate);
    }

    @PrePersist
    public void prePersist() {
        if (issueDate == null) {
            issueDate = LocalDate.now();
        }
        if (expiryDate == null) {
            expiryDate = issueDate.plusYears(5);
        }
    }
}