package org.example._citizenproj2.repository;

import org.example._citizenproj2.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    // 基本查詢
    List<Booking> findByMemberMemberId(Long memberId);

    List<Booking> findByShowingShowingId(Long showingId);

    Optional<Booking> findByBookingIdAndMemberMemberId(String bookingId, Long memberId);

    // 狀態查詢
    List<Booking> findByBookingStatus(Booking.BookingStatus status);

    List<Booking> findByPaymentStatus(Booking.PaymentStatus status);

    // 複合查詢
    @Query("SELECT b FROM Booking b WHERE b.member.memberId = :memberId " +
            "AND b.bookingTime BETWEEN :startTime AND :endTime")
    List<Booking> findMemberBookingsInPeriod(
            @Param("memberId") Long memberId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

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
    long countConfirmedBookings(@Param("showingId") Long showingId);

    // 座位查詢
    @Query("SELECT bd.seat.seatId FROM BookingDetail bd " +
            "WHERE bd.booking.showing.showingId = :showingId " +
            "AND bd.booking.bookingStatus != 'CANCELLED'")
    List<Long> findBookedSeatIds(@Param("showingId") Long showingId);

    // 複雜統計
    @Query("SELECT new map(" +
            "b.showing.movie.movieName as movieName, " +
            "COUNT(b) as bookingCount, " +
            "SUM(b.totalAmount) as totalRevenue) " +
            "FROM Booking b " +
            "WHERE b.bookingStatus = 'COMPLETED' " +
            "GROUP BY b.showing.movie.movieId")
    List<Map<String, Object>> getMovieBookingStatistics();

    // 取消過期訂單
    @Modifying
    @Query("UPDATE Booking b SET b.bookingStatus = 'CANCELLED' " +
            "WHERE b.bookingStatus = 'PENDING' " +
            "AND b.bookingTime < :expiryTime")
    int cancelExpiredBookings(@Param("expiryTime") LocalDateTime expiryTime);

    // 查詢熱門場次
    @Query("SELECT b.showing.showingId, COUNT(b) as bookingCount " +
            "FROM Booking b " +
            "WHERE b.bookingStatus = 'CONFIRMED' " +
            "GROUP BY b.showing.showingId " +
            "ORDER BY bookingCount DESC")
    List<Object[]> findPopularShowings(Pageable pageable);
}