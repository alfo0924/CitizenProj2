package org.example._citizenproj2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {
    private Long ratingId;
    private Long movieId;
    private Long memberId;
    private BigDecimal rating;
    private String comment;
    private Boolean isVerified;
    private Boolean isVisible;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 電影資訊
    private MovieInfo movieInfo;

    // 會員資訊
    private MemberInfo memberInfo;

    // 審核資訊
    private ModeratorInfo moderatorInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovieInfo {
        private String movieName;
        private String originalName;
        private String posterUrl;
        private String movieStatus;
        private Double averageRating;
        private Integer totalRatings;
        private LocalDateTime releaseDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {
        private String memberName;
        private String memberAvatar;
        private Integer totalReviews;
        private Double averageRating;
        private Boolean isTrustedReviewer;
        private LocalDateTime memberSince;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModeratorInfo {
        private String moderatorId;
        private String moderatorName;
        private LocalDateTime verifiedAt;
        private String verificationNote;
        private String moderationStatus;
        private LocalDateTime lastModifiedAt;
    }

    // 業務方法
    public boolean isEditable() {
        return createdAt.plusHours(24).isAfter(LocalDateTime.now());
    }

    public boolean isReportable() {
        return isVisible && "ACTIVE".equals(status);
    }

    public boolean needsModeration() {
        return !isVerified && (comment != null && comment.length() > 200);
    }

    public boolean isTrustedReview() {
        return isVerified && memberInfo != null && memberInfo.getIsTrustedReviewer();
    }

    // 評分等級判斷
    public String getRatingLevel() {
        if (rating == null) return "NONE";
        double value = rating.doubleValue();
        if (value >= 4.5) return "EXCELLENT";
        if (value >= 4.0) return "VERY_GOOD";
        if (value >= 3.0) return "GOOD";
        if (value >= 2.0) return "FAIR";
        return "POOR";
    }

    // 格式化評分顯示
    public String getFormattedRating() {
        return rating != null ? String.format("%.1f", rating) : "N/A";
    }

    // 取得評論摘要
    public String getCommentSummary(int maxLength) {
        if (comment == null || comment.isEmpty()) {
            return "No comment";
        }
        return comment.length() > maxLength ?
                comment.substring(0, maxLength) + "..." :
                comment;
    }
}