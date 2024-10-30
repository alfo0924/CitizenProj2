package org.example._citizenproj2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example._citizenproj2.model.Movie;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {
    private Long movieId;
    private String movieName;
    private String originalName;
    private String director;
    private String cast;
    private Integer duration;
    private String description;
    private Date releaseDate;
    private Date endDate;
    private String rating;
    private String language;
    private String subtitle;
    private String posterUrl;
    private String trailerUrl;
    private Long categoryId;
    private String categoryName;
    private Movie.MovieStatus movieStatus;

    // 擴展資訊
    private ShowingInfo currentShowings;
    private RatingInfo ratingInfo;
    private List<String> genres;

    @Data
    @Builder
    public static class ShowingInfo {
        private Integer totalShowings;
        private Integer availableShowings;
        private Date nextShowingTime;
        private Double lowestPrice;
        private Double highestPrice;
        private Long showingId;
        private LocalDateTime showTime;
        private String venueName;
        private Integer availableSeats;
        private BigDecimal basePrice;
        private String status;
    }

    @Data
    @Builder
    public static class RatingInfo {
        private Double averageRating;
        private Integer totalRatings;
        private Integer totalReviews;
    }
}