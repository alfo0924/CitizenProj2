package org.example._citizenproj2.repository;

import org.example._citizenproj2.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<Movie> findByMovieStatus(Movie.MovieStatus status, Pageable pageable);

    Page<Movie> findByReleaseDateAfter(LocalDate date, Pageable pageable);

    Optional<Movie> findByMovieName(String movieName);

    boolean existsByMovieName(String movieName);

    // 狀態計數
    long countByMovieStatus(Movie.MovieStatus status);

    // 複合查詢
    @Query("SELECT m FROM Movie m WHERE m.movieStatus = 'SHOWING' " +
            "AND m.releaseDate <= CURRENT_DATE")
    Page<Movie> findCurrentlyShowingMovies(Pageable pageable);

    // 自定義查詢
    @Query("SELECT m FROM Movie m WHERE m.category.categoryId = :categoryId")
    Page<Movie> findMoviesByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

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
    Page<Movie> searchMovies(
            @Param("name") String name,
            @Param("director") String director,
            @Param("status") Movie.MovieStatus status,
            @Param("categoryId") Long categoryId,
            Pageable pageable);

    // 即將上映電影
    @Query("SELECT m FROM Movie m WHERE m.releaseDate > CURRENT_DATE " +
            "ORDER BY m.releaseDate ASC")
    Page<Movie> findUpcomingMovies(Pageable pageable);

    // 熱門電影查詢
    @Query(value = "SELECT m.* FROM movies m " +
            "JOIN showings s ON m.movie_id = s.movie_id " +
            "WHERE m.movie_status = 'SHOWING' " +
            "GROUP BY m.movie_id " +
            "ORDER BY COUNT(s.showing_id) DESC",
            nativeQuery = true)
    Page<Movie> findPopularMovies(Pageable pageable);

    // 根據評分查詢
    @Query(value = "SELECT m.* FROM movies m " +
            "LEFT JOIN movie_ratings r ON m.movie_id = r.movie_id " +
            "GROUP BY m.movie_id " +
            "HAVING AVG(r.rating) >= :minRating " +
            "ORDER BY AVG(r.rating) DESC",
            nativeQuery = true)
    Page<Movie> findHighlyRatedMovies(@Param("minRating") double minRating, Pageable pageable);

    // 日期範圍查詢
    Page<Movie> findByReleaseDateBetweenOrderByReleaseDateDesc(
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable);

    // 類別和狀態組合查詢
    Page<Movie> findByCategoryCategoryIdAndMovieStatus(
            Long categoryId,
            Movie.MovieStatus status,
            Pageable pageable);

    // 關鍵字搜索
    @Query("SELECT m FROM Movie m WHERE " +
            "LOWER(m.movieName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.originalName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.director) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.cast) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Movie> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 統計分析
    @Query("SELECT new map(" +
            "m.movieStatus as status, " +
            "COUNT(m) as count) " +
            "FROM Movie m GROUP BY m.movieStatus")
    List<java.util.Map<String, Object>> getMovieStatusStatistics();

    @Query("SELECT new map(" +
            "m.category.categoryName as category, " +
            "COUNT(m) as count) " +
            "FROM Movie m GROUP BY m.category.categoryName")
    List<java.util.Map<String, Object>> getMovieCategoryStatistics();

    // 自定義排序查詢
    @Query("SELECT m FROM Movie m WHERE " +
            "m.movieStatus = :status " +
            "ORDER BY CASE " +
            "WHEN :sortBy = 'rating' THEN m.rating " +
            "WHEN :sortBy = 'releaseDate' THEN m.releaseDate " +
            "ELSE m.movieId END")
    Page<Movie> findMoviesWithCustomSort(
            @Param("status") Movie.MovieStatus status,
            @Param("sortBy") String sortBy,
            Pageable pageable);

    // 評論相關查詢
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.comments " +
            "WHERE m.movieId = :movieId")
    Optional<Movie> findMovieWithComments(@Param("movieId") Long movieId);

    @Query("SELECT COUNT(c) FROM Movie m JOIN m.comments c " +
            "WHERE m.movieId = :movieId")
    long getCommentCount(@Param("movieId") Long movieId);

    // 修改評論相關查詢為評分相關查詢
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.ratings " +
            "WHERE m.movieId = :movieId")
    Optional<Movie> findMovieWithRatings(@Param("movieId") Long movieId);

    @Query("SELECT COUNT(r) FROM Movie m JOIN m.ratings r " +
            "WHERE m.movieId = :movieId")
    long getRatingCount(@Param("movieId") Long movieId);

    // 添加評分統計相關查詢
    @Query("SELECT AVG(r.rating) FROM Movie m JOIN m.ratings r " +
            "WHERE m.movieId = :movieId")
    Double getAverageRating(@Param("movieId") Long movieId);

    @Query("SELECT new map(" +
            "r.rating as score, " +
            "COUNT(r) as count) " +
            "FROM Movie m JOIN m.ratings r " +
            "WHERE m.movieId = :movieId " +
            "GROUP BY r.rating " +
            "ORDER BY r.rating DESC")
    List<java.util.Map<String, Object>> getRatingDistribution(@Param("movieId") Long movieId);

}