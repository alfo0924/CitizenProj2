package org.example._citizenproj2.repository;

import org.example._citizenproj2.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // 基本查詢方法
    List<Movie> findByMovieStatus(Movie.MovieStatus status);

    List<Movie> findByReleaseDateAfter(LocalDate date);

    Optional<Movie> findByMovieName(String movieName);

    // 複合查詢
    @Query("SELECT m FROM Movie m WHERE m.movieStatus = 'SHOWING' AND m.releaseDate <= CURRENT_DATE")
    List<Movie> findCurrentlyShowingMovies();

    // 自定義查詢
    @Query("SELECT m FROM Movie m WHERE m.category.categoryId = :categoryId")
    List<Movie> findMoviesByCategory(@Param("categoryId") Long categoryId);

    // 更新操作
    @Modifying
    @Query("UPDATE Movie m SET m.movieStatus = :status WHERE m.movieId = :id")
    int updateMovieStatus(@Param("id") Long id, @Param("status") Movie.MovieStatus status);

    // 統計查詢
    @Query("SELECT COUNT(m) FROM Movie m WHERE m.releaseDate BETWEEN :startDate AND :endDate")
    long countMoviesInPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 複雜搜尋
    @Query("SELECT m FROM Movie m WHERE " +
            "(:name IS NULL OR m.movieName LIKE %:name%) AND " +
            "(:director IS NULL OR m.director LIKE %:director%) AND " +
            "(:status IS NULL OR m.movieStatus = :status) AND " +
            "(:categoryId IS NULL OR m.category.categoryId = :categoryId)")
    List<Movie> searchMovies(
            @Param("name") String name,
            @Param("director") String director,
            @Param("status") Movie.MovieStatus status,
            @Param("categoryId") Long categoryId);

    // 即將上映電影
    @Query("SELECT m FROM Movie m WHERE m.releaseDate > CURRENT_DATE ORDER BY m.releaseDate ASC")
    List<Movie> findUpcomingMovies();

    // 熱門電影查詢
    @Query(value = "SELECT m.* FROM city_movies m " +
            "JOIN showings s ON m.movie_id = s.movie_id " +
            "GROUP BY m.movie_id " +
            "ORDER BY COUNT(s.showing_id) DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Movie> findMostScheduledMovies(@Param("limit") int limit);

    // 根據評分查詢
    @Query(value = "SELECT m.* FROM city_movies m " +
            "LEFT JOIN movie_reviews r ON m.movie_id = r.movie_id " +
            "GROUP BY m.movie_id " +
            "HAVING AVG(r.rating) >= :minRating", nativeQuery = true)
    List<Movie> findHighlyRatedMovies(@Param("minRating") double minRating);

    // 日期範圍查詢
    List<Movie> findByReleaseDateBetweenOrderByReleaseDateDesc(
            LocalDate startDate,
            LocalDate endDate);

    // 類別和狀態組合查詢
    List<Movie> findByCategoryCategoryIdAndMovieStatus(
            Long categoryId,
            Movie.MovieStatus status);
}