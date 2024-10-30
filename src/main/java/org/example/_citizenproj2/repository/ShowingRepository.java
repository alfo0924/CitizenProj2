package org.example._citizenproj2.repository;

import org.example._citizenproj2.model.Showing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowingRepository extends JpaRepository<Showing, Long> {

    // 基本查詢
    List<Showing> findByMovieMovieId(Long movieId);

    List<Showing> findByVenueVenueId(Long venueId);

    List<Showing> findByShowDateAndShowingStatus(LocalDate showDate, Showing.ShowingStatus status);

    // 複合查詢
    @Query("SELECT s FROM Showing s WHERE s.movie.movieId = :movieId " +
            "AND s.showDate >= :date AND s.showingStatus = 'AVAILABLE'")
    List<Showing> findAvailableShowings(
            @Param("movieId") Long movieId,
            @Param("date") LocalDate date);

    // 座位更新
    @Modifying
    @Query("UPDATE Showing s SET s.availableSeats = :seats, " +
            "s.showingStatus = CASE " +
            "WHEN :seats <= 0 THEN 'FULL' " +
            "WHEN :seats <= (s.venue.totalCapacity * 0.2) THEN 'ALMOST_FULL' " +
            "ELSE 'AVAILABLE' END " +
            "WHERE s.showingId = :showingId")
    int updateAvailableSeats(@Param("showingId") Long showingId, @Param("seats") Integer seats);

    // 統計查詢
    @Query("SELECT COUNT(s) FROM Showing s WHERE s.movie.movieId = :movieId " +
            "AND s.showDate = :date")
    long countShowingsByMovieAndDate(
            @Param("movieId") Long movieId,
            @Param("date") LocalDate date);

    // 分頁查詢
    Page<Showing> findByShowDateBetween(
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable);

    // 複雜條件查詢
    @Query("SELECT s FROM Showing s WHERE " +
            "(:movieId IS NULL OR s.movie.movieId = :movieId) AND " +
            "(:venueId IS NULL OR s.venue.venueId = :venueId) AND " +
            "(:date IS NULL OR s.showDate = :date) AND " +
            "(:status IS NULL OR s.showingStatus = :status)")
    List<Showing> searchShowings(
            @Param("movieId") Long movieId,
            @Param("venueId") Long venueId,
            @Param("date") LocalDate date,
            @Param("status") Showing.ShowingStatus status);

    // 熱門場次查詢
    @Query(value = "SELECT s.* FROM showings s " +
            "JOIN bookings b ON s.showing_id = b.showing_id " +
            "WHERE s.show_date >= CURRENT_DATE " +
            "GROUP BY s.showing_id " +
            "ORDER BY COUNT(b.booking_id) DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Showing> findPopularShowings(@Param("limit") int limit);
}