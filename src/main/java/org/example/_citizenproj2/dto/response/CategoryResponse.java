package org.example._citizenproj2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long categoryId;
    private String categoryName;
    private String description;
    private Boolean isActive;
    private Integer displayOrder;
    private String categoryType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 統計資訊
    private CategoryStats stats;

    // 電影清單
    private List<MovieSummary> movies;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryStats {
        private Integer totalMovies;
        private Integer activeMovies;
        private Integer upcomingMovies;
        private Double averageRating;
        private Integer totalBookings;
        private Double totalRevenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovieSummary {
        private Long movieId;
        private String movieName;
        private String originalName;
        private String posterUrl;
        private String movieStatus;
        private LocalDateTime releaseDate;
        private Double rating;
        private Integer bookingCount;
        private Boolean isShowing;
        private Long categoryId;
        private String categoryName;
        private String description;
        private Boolean isActive;
        private Integer displayOrder;
    }
}