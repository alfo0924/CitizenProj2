package org.example._citizenproj2.dto.request;

import jakarta.validation.constraints.*;
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
public class RatingRequest {

    @NotNull(message = "評分不能為空")
    @DecimalMin(value = "0.0", message = "評分不能小於0")
    @DecimalMax(value = "5.0", message = "評分不能大於5")
    private BigDecimal rating;

    @Size(max = 1000, message = "評論長度不能超過1000字")
    private String comment;

    // 可選欄位
    private Long movieId;
    private Long memberId;
    private Boolean isAnonymous;
    private String title;
    private Boolean containsSpoilers;

    // 驗證方法
    public void validateRating() {
        if (rating != null) {
            // 確保評分只有一位小數
            rating = rating.setScale(1, BigDecimal.ROUND_HALF_UP);
        }

        if (comment != null && comment.trim().isEmpty()) {
            comment = null;
        }

        if (containsSpoilers == null) {
            containsSpoilers = false;
        }

        if (isAnonymous == null) {
            isAnonymous = false;
        }
    }

    // 建立評分的業務規則
    @AssertTrue(message = "標題必須少於50字")
    private boolean isTitleValid() {
        if (title == null) {
            return true;
        }
        return title.length() <= 50;
    }

    @AssertTrue(message = "如果是匿名評論，不能包含個人資訊")
    private boolean isAnonymousValid() {
        if (Boolean.TRUE.equals(isAnonymous)) {
            return memberId == null;
        }
        return true;
    }

    // Builder 模式的默認值設置
    public static class RatingRequestBuilder {
        private Boolean isAnonymous = false;
        private Boolean containsSpoilers = false;

        public RatingRequestBuilder rating(BigDecimal rating) {
            this.rating = rating != null ? rating.setScale(1, BigDecimal.ROUND_HALF_UP) : null;
            return this;
        }
    }

    // 用於創建評分的靜態工廠方法
    public static RatingRequest createSimpleRating(BigDecimal rating) {
        return RatingRequest.builder()
                .rating(rating)
                .build();
    }

    public static RatingRequest createDetailedRating(BigDecimal rating, String comment, String title) {
        return RatingRequest.builder()
                .rating(rating)
                .comment(comment)
                .title(title)
                .build();
    }

    // 用於驗證評分的常量
    public static final BigDecimal MIN_RATING = BigDecimal.ZERO;
    public static final BigDecimal MAX_RATING = new BigDecimal("5.0");
    public static final int MAX_COMMENT_LENGTH = 1000;
    public static final int MAX_TITLE_LENGTH = 50;
}