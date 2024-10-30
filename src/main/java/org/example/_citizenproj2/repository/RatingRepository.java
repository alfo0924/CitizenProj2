package org.example._citizenproj2.repository;

import org.example._citizenproj2.model.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    // 基本查詢
    Page<Rating> findByMovieMovieId(Long movieId, Pageable pageable);

    Page<Rating> findByMemberMemberId(Long memberId, Pageable pageable);

    // 評分統計
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.movie.movieId = :movieId")
    Double getAverageRating(@Param("movieId") Long movieId);

    // 評分分布
    @Query("SELECT r.rating as score, COUNT(r) as count " +
            "FROM Rating r " +
            "WHERE r.movie.movieId = :movieId " +
            "GROUP BY r.rating " +
            "ORDER BY r.rating DESC")
    List<Map<String, Object>> getRatingDistribution(@Param("movieId") Long movieId);

    // 最新評分
    @Query("SELECT r FROM Rating r " +
            "WHERE r.movie.movieId = :movieId " +
            "AND r.isVisible = true " +
            "ORDER BY r.createdAt DESC")
    Page<Rating> findLatestRatings(
            @Param("movieId") Long movieId,
            Pageable pageable);

    // 高分評價
    @Query("SELECT r FROM Rating r " +
            "WHERE r.movie.movieId = :movieId " +
            "AND r.rating >= :minRating " +
            "AND r.isVisible = true " +
            "ORDER BY r.rating DESC, r.createdAt DESC")
    Page<Rating> findHighRatings(
            @Param("movieId") Long movieId,
            @Param("minRating") Double minRating,
            Pageable pageable);

    // 評論搜索
    @Query("SELECT r FROM Rating r " +
            "WHERE r.movie.movieId = :movieId " +
            "AND r.comment LIKE %:keyword% " +
            "AND r.isVisible = true " +
            "ORDER BY r.createdAt DESC")
    Page<Rating> searchRatingsByKeyword(
            @Param("movieId") Long movieId,
            @Param("keyword") String keyword,
            Pageable pageable);

    // 會員評分統計
    @Query("SELECT new map(" +
            "r.rating as rating, " +
            "COUNT(r) as count, " +
            "AVG(r.rating) as average) " +
            "FROM Rating r " +
            "WHERE r.member.memberId = :memberId " +
            "GROUP BY r.rating")
    List<Map<String, Object>> getMemberRatingStatistics(
            @Param("memberId") Long memberId);

    // 驗證評分資格
    @Query("SELECT COUNT(r) > 0 FROM Rating r " +
            "WHERE r.movie.movieId = :movieId " +
            "AND r.member.memberId = :memberId")
    boolean hasUserRated(
            @Param("movieId") Long movieId,
            @Param("memberId") Long memberId);

    // 取得特定時間範圍的評分
    @Query("SELECT r FROM Rating r " +
            "WHERE r.movie.movieId = :movieId " +
            "AND r.createdAt BETWEEN :startDate AND :endDate " +
            "AND r.isVisible = true " +
            "ORDER BY r.createdAt DESC")
    Page<Rating> findRatingsByDateRange(
            @Param("movieId") Long movieId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 取得需要審核的評分
    @Query("SELECT r FROM Rating r " +
            "WHERE r.isVerified = false " +
            "ORDER BY r.createdAt ASC")
    Page<Rating> findUnverifiedRatings(Pageable pageable);

    // 統計每日評分數量
    @Query("SELECT new map(" +
            "FUNCTION('DATE', r.createdAt) as date, " +
            "COUNT(r) as count, " +
            "AVG(r.rating) as averageRating) " +
            "FROM Rating r " +
            "WHERE r.movie.movieId = :movieId " +
            "GROUP BY FUNCTION('DATE', r.createdAt) " +
            "ORDER BY FUNCTION('DATE', r.createdAt) DESC")
    List<Map<String, Object>> getDailyRatingStatistics(
            @Param("movieId") Long movieId);
}