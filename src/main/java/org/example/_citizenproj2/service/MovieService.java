package org.example._citizenproj2.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example._citizenproj2.dto.request.MovieRequest;
import org.example._citizenproj2.dto.request.RatingRequest;
import org.example._citizenproj2.dto.response.CategoryResponse;
import org.example._citizenproj2.dto.response.MovieResponse;
import org.example._citizenproj2.dto.response.RatingResponse;
import org.example._citizenproj2.exception.MovieNotFoundException;
import org.example._citizenproj2.model.Movie;
import org.example._citizenproj2.model.MovieCategory;
import org.example._citizenproj2.repository.MovieCategoryRepository;
import org.example._citizenproj2.repository.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.example._citizenproj2.model.Rating;
import org.example._citizenproj2.repository.RatingRepository;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieCategoryRepository categoryRepository;
    private final RatingRepository ratingRepository;
    @Transactional
    public MovieResponse createMovie(MovieRequest request) {
        MovieCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("無效的電影類別ID"));

        Movie movie = new Movie();
        movie.setMovieName(request.getMovieName());
        movie.setOriginalName(request.getOriginalName());
        movie.setDirector(request.getDirector());
        movie.setCast(request.getCast());
        movie.setDuration(request.getDuration());
        movie.setDescription(request.getDescription());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setEndDate(request.getEndDate());
        movie.setRating(request.getRating());
        movie.setLanguage(request.getLanguage());
        movie.setSubtitle(request.getSubtitle());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setTrailerUrl(request.getTrailerUrl());
        movie.setCategory(category);
        movie.setMovieStatus(Movie.MovieStatus.COMING);

        Movie savedMovie = movieRepository.save(movie);
        return convertToResponse(savedMovie);
    }

    public MovieResponse getMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("找不到ID為 " + id + " 的電影"));
        return convertToResponse(movie);
    }

    @Transactional
    public MovieResponse updateMovie(Long id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("找不到ID為 " + id + " 的電影"));

        MovieCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("無效的電影類別ID"));

        movie.setMovieName(request.getMovieName());
        movie.setOriginalName(request.getOriginalName());
        movie.setDirector(request.getDirector());
        movie.setCast(request.getCast());
        movie.setDuration(request.getDuration());
        movie.setDescription(request.getDescription());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setEndDate(request.getEndDate());
        movie.setRating(request.getRating());
        movie.setLanguage(request.getLanguage());
        movie.setSubtitle(request.getSubtitle());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setTrailerUrl(request.getTrailerUrl());
        movie.setCategory(category);

        Movie updatedMovie = movieRepository.save(movie);
        return convertToResponse(updatedMovie);
    }

    @Transactional
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new MovieNotFoundException("找不到ID為 " + id + " 的電影");
        }
        movieRepository.deleteById(id);
    }

    public Page<MovieResponse> getCurrentlyShowingMovies(Pageable pageable) {
        return movieRepository.findCurrentlyShowingMovies(pageable)
                .map(this::convertToResponse);
    }

    public Page<MovieResponse> getUpcomingMovies(Pageable pageable) {
        return movieRepository.findUpcomingMovies(pageable)
                .map(this::convertToResponse);
    }

    public Page<MovieResponse> searchMovies(String name, String director,
                                            Movie.MovieStatus status, Long categoryId,
                                            Pageable pageable) {
        return movieRepository.searchMovies(name, director, status, categoryId, pageable)
                .map(this::convertToResponse);
    }

    @Transactional
    public MovieResponse updateMovieStatus(Long id, Movie.MovieStatus status) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("找不到ID為 " + id + " 的電影"));
        movie.setMovieStatus(status);
        return convertToResponse(movieRepository.save(movie));
    }

    public Page<MovieResponse> getMoviesByCategory(Long categoryId, Pageable pageable) {
        return movieRepository.findMoviesByCategory(categoryId, pageable)
                .map(this::convertToResponse);
    }

    public Page<MovieResponse> getPopularMovies(Pageable pageable) {
        return movieRepository.findPopularMovies(pageable)
                .map(this::convertToResponse);
    }

    public Page<MovieResponse> getHighlyRatedMovies(double minRating, Pageable pageable) {
        return movieRepository.findHighlyRatedMovies(minRating, pageable)
                .map(this::convertToResponse);
    }

    public Page<MovieResponse> searchByKeyword(String keyword, Pageable pageable) {
        return movieRepository.searchByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    public List<Map<String, Object>> getMovieStatusStatistics() {
        return movieRepository.getMovieStatusStatistics();
    }

    public List<Map<String, Object>> getMovieCategoryStatistics() {
        return movieRepository.getMovieCategoryStatistics();
    }

    private MovieResponse convertToResponse(Movie movie) {
        return MovieResponse.builder()
                .movieId(movie.getMovieId())
                .movieName(movie.getMovieName())
                .originalName(movie.getOriginalName())
                .director(movie.getDirector())
                .cast(movie.getCast())
                .duration(movie.getDuration())
                .description(movie.getDescription())
                .releaseDate(movie.getReleaseDate())
                .endDate(movie.getEndDate())
                .rating(movie.getRating())
                .language(movie.getLanguage())
                .subtitle(movie.getSubtitle())
                .posterUrl(movie.getPosterUrl())
                .trailerUrl(movie.getTrailerUrl())
                .categoryId(movie.getCategory().getCategoryId())
                .categoryName(movie.getCategory().getCategoryName())
                .movieStatus(movie.getMovieStatus())
                .build();
    }
    // 在 MovieService 類中添加以下方法

    public List<MovieResponse.ShowingInfo> getMovieShowings(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("找不到ID為 " + movieId + " 的電影"));

        return movie.getShowings().stream()
                .map(showing -> MovieResponse.ShowingInfo.builder()
                        .showingId(showing.getShowingId())
                        .showTime(showing.getShowTime())
                        .venueName(showing.getVenue().getVenueName())
                        .availableSeats(showing.getAvailableSeats())
                        .basePrice(showing.getBasePrice())
                        .status(showing.getShowingStatus().toString())
                        .build())
                .collect(Collectors.toList());
    }

    public Page<CategoryResponse> getMovieCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(category -> CategoryResponse.builder()
                        .categoryId(category.getCategoryId())
                        .categoryName(category.getCategoryName())
                        .description(category.getDescription())
                        .isActive(category.getIsActive())
                        .displayOrder(category.getDisplayOrder())
                        .build());
    }

    @Transactional
    public RatingResponse addMovieRating(Long movieId, @Valid RatingRequest request) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("找不到ID為 " + movieId + " 的電影"));

        Rating rating = new Rating();
        rating.setMovie(movie);
        rating.setRating(request.getRating());
        rating.setComment(request.getComment());
        rating.setCreatedAt(LocalDateTime.now());

        Rating savedRating = ratingRepository.save(rating);

        return convertToRatingResponse(savedRating);
    }

    public Page<RatingResponse> getMovieRatings(Long movieId, Pageable pageable) {
        return ratingRepository.findByMovieMovieId(movieId, pageable)
                .map(this::convertToRatingResponse);
    }

    public Map<String, Object> getMovieStatistics(String startDate, String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("statusStats", getMovieStatusStatistics());
        statistics.put("categoryStats", getMovieCategoryStatistics());
        statistics.put("totalMovies", movieRepository.count());
        statistics.put("showingMovies", movieRepository.countByMovieStatus(Movie.MovieStatus.SHOWING));
        statistics.put("upcomingMovies", movieRepository.countByMovieStatus(Movie.MovieStatus.COMING));

        return statistics;
    }

    // 添加輔助方法
    private RatingResponse convertToRatingResponse(Rating rating) {
        return RatingResponse.builder()
                .ratingId(rating.getRatingId())
                .movieId(rating.getMovie().getMovieId())
                .rating(rating.getRating())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .build();
    }
}