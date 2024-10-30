package org.example._citizenproj2.repository;

import org.example._citizenproj2.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    // 基本查詢
    Page<Booking> findByMemberMemberId(Long memberId, Pageable pageable);

    Page<Booking> findByShowingShowingId(Long showingId, Pageable pageable);

    Optional<Booking> findByBookingIdAndMemberMemberId(String bookingId, Long memberId);

    // 狀態查詢
    Page<Booking> findByBookingStatus(Booking.BookingStatus status, Pageable pageable);

    Page<Booking> findByPaymentStatus(Booking.PaymentStatus status, Pageable pageable);

    // 複合查詢
    @Query("SELECT b FROM Booking b WHERE b.member.memberId = :memberId " +
            "AND b.bookingTime BETWEEN :startTime AND :endTime")
    Page<Booking> findMemberBookingsInPeriod(
            @Param("memberId") Long memberId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    // 更新操作
    @Modifying
    @Query("UPDATE Booking b SET b.bookingStatus = :status WHERE b.bookingId = :id")
    int updateBookingStatus(@Param("id") String id, @Param("status") Booking.BookingStatus status);

    @Modifying
    @Query("UPDATE Booking b SET b.paymentStatus = :status WHERE b.bookingId = :id")
    int updatePaymentStatus(@Param("id") String id, @Param("status") Booking.PaymentStatus status);

    // 統計查詢
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.showing.showingId = :showingId " +
            "AND b.bookingStatus = 'CONFIRMED'")
    Long countConfirmedBookings(@Param("showingId") Long showingId);

    // 座位查詢
    @Query("SELECT bd.seat.seatId FROM BookingDetail bd " +
            "WHERE bd.booking.showing.showingId = :showingId " +
            "AND bd.booking.bookingStatus != 'CANCELLED'")
    List<Long> findBookedSeatIds(@Param("showingId") Long showingId);

    // 統計分析
    @Query("SELECT new map(" +
            "b.showing.movie.movieName as movieName, " +
            "COUNT(b) as bookingCount, " +
            "SUM(b.totalAmount) as totalRevenue) " +
            "FROM Booking b " +
            "WHERE b.bookingTime BETWEEN :startTime AND :endTime " +
            "AND b.bookingStatus = 'COMPLETED' " +
            "GROUP BY b.showing.movie.movieId, b.showing.movie.movieName")
    List<Map<String, Object>> getBookingStatistics(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    // 取消過期訂單
    @Modifying
    @Query("UPDATE Booking b SET b.bookingStatus = 'CANCELLED' " +
            "WHERE b.bookingStatus = 'PENDING' " +
            "AND b.bookingTime < :expiryTime")
    int cancelExpiredBookings(@Param("expiryTime") LocalDateTime expiryTime);

    // 查詢熱門場次
    @Query("SELECT new map(" +
            "b.showing.showingId as showingId, " +
            "b.showing.movie.movieName as movieName, " +
            "COUNT(b) as bookingCount) " +
            "FROM Booking b " +
            "WHERE b.bookingStatus = 'CONFIRMED' " +
            "GROUP BY b.showing.showingId, b.showing.movie.movieName " +
            "ORDER BY COUNT(b) DESC")
    Page<Map<String, Object>> findPopularShowings(Pageable pageable);

    // 進階搜索
    @Query("SELECT b FROM Booking b WHERE " +
            "(:memberId IS NULL OR b.member.memberId = :memberId) AND " +
            "(:showingId IS NULL OR b.showing.showingId = :showingId) AND " +
            "(:status IS NULL OR b.bookingStatus = :status) AND " +
            "(:startTime IS NULL OR b.bookingTime >= :startTime) AND " +
            "(:endTime IS NULL OR b.bookingTime <= :endTime)")
    Page<Booking> searchBookings(
            @Param("memberId") Long memberId,
            @Param("showingId") Long showingId,
            @Param("status") Booking.BookingStatus status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    // 會員訂票統計
    @Query("SELECT new map(" +
            "b.member.memberId as memberId, " +
            "COUNT(b) as totalBookings, " +
            "SUM(b.totalAmount) as totalSpent) " +
            "FROM Booking b " +
            "WHERE b.bookingStatus = 'COMPLETED' " +
            "GROUP BY b.member.memberId")
    Page<Map<String, Object>> getMemberBookingStatistics(Pageable pageable);

    // 檢查重複訂票
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.member.memberId = :memberId " +
            "AND b.showing.showingId = :showingId " +
            "AND b.bookingStatus != 'CANCELLED'")
    boolean hasExistingBooking(
            @Param("memberId") Long memberId,
            @Param("showingId") Long showingId);

    // 每日訂票統計
    @Query("SELECT new map(" +
            "FUNCTION('DATE', b.bookingTime) as bookingDate, " +
            "COUNT(b) as totalBookings, " +
            "SUM(b.totalAmount) as totalRevenue) " +
            "FROM Booking b " +
            "WHERE b.bookingTime BETWEEN :startTime AND :endTime " +
            "GROUP BY FUNCTION('DATE', b.bookingTime)")
    List<Map<String, Object>> getDailyBookingStatistics(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}