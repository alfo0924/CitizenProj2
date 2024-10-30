package org.example._citizenproj2.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movie_ratings")
@Data  // 已有，但確保存在
@Getter // 明確添加
@Setter // 明確添加
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratingId;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;


    @Column(nullable = false)
    private BigDecimal rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean isVisible = true;
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private Boolean isVerified = false;

    @Enumerated(EnumType.STRING)
    private RatingStatus status = RatingStatus.ACTIVE;


    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum RatingStatus {
        ACTIVE, HIDDEN, REPORTED, DELETED
    }

    // 驗證方法
    @PrePersist
    @PreUpdate
    public void validateRating() {
        if (rating.compareTo(BigDecimal.ZERO) < 0 || rating.compareTo(new BigDecimal("5")) > 0) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
    }

    // 業務方法
    public boolean canEdit() {
        return LocalDateTime.now().minusHours(24).isBefore(createdAt);
    }

    public boolean isReportable() {
        return status == RatingStatus.ACTIVE;
    }
}