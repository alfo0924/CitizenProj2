package org.example._citizenproj2.repository;

import org.example._citizenproj2.model.Seat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    // 基本查詢
    List<Seat> findByVenueVenueId(Long venueId);

    List<Seat> findBySeatType(Seat.SeatType seatType);

    Page<Seat> findByVenueVenueId(Long venueId, Pageable pageable);

    // 可用座位查詢
    @Query("SELECT s FROM Seat s WHERE s.venue.venueId = :venueId " +
            "AND s.status = 'ACTIVE'")
    List<Seat> findAvailableSeats(@Param("venueId") Long venueId);

    // 座位狀態查詢
    @Query("SELECT s FROM Seat s WHERE s.venue.venueId = :venueId " +
            "AND s.seatId NOT IN :bookedSeatIds " +
            "AND s.status = 'ACTIVE'")
    List<Seat> findAvailableSeatsByVenue(
            @Param("venueId") Long venueId,
            @Param("bookedSeatIds") List<Long> bookedSeatIds);

    // 座位號碼查詢
    @Query("SELECT CONCAT(s.rowNumber, s.columnNumber) FROM Seat s " +
            "WHERE s.venue.venueId = :venueId " +
            "AND s.status = 'ACTIVE' " +
            "AND s.seatId NOT IN :bookedSeatIds " +
            "ORDER BY s.rowNumber, s.columnNumber")
    List<String> findAvailableSeatNumbers(
            @Param("venueId") Long venueId,
            @Param("bookedSeatIds") List<Long> bookedSeatIds);

    // 特定類型座位查詢
    @Query("SELECT s FROM Seat s WHERE s.venue.venueId = :venueId " +
            "AND s.seatType = :seatType " +
            "AND s.status = 'ACTIVE' " +
            "AND s.seatId NOT IN :bookedSeatIds")
    List<Seat> findAvailableSeatsByType(
            @Param("venueId") Long venueId,
            @Param("seatType") Seat.SeatType seatType,
            @Param("bookedSeatIds") List<Long> bookedSeatIds);

    // 座位容量統計
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.venue.venueId = :venueId " +
            "AND s.status = 'ACTIVE'")
    Long countAvailableSeats(@Param("venueId") Long venueId);

    // 檢查座位是否可用
    @Query("SELECT COUNT(s) = :totalSeats FROM Seat s " +
            "WHERE s.venue.venueId = :venueId " +
            "AND s.seatId IN :seatIds " +
            "AND s.status = 'ACTIVE' " +
            "AND s.seatId NOT IN " +
            "(SELECT bd.seat.seatId FROM BookingDetail bd " +
            "WHERE bd.booking.showing.venue.venueId = :venueId " +
            "AND bd.booking.bookingStatus != 'CANCELLED')")
    boolean areSeatsAvailable(
            @Param("venueId") Long venueId,
            @Param("seatIds") List<Long> seatIds,
            @Param("totalSeats") int totalSeats);

    // 取得連續座位
    @Query("SELECT s FROM Seat s WHERE s.venue.venueId = :venueId " +
            "AND s.status = 'ACTIVE' " +
            "AND s.seatId NOT IN :bookedSeatIds " +
            "AND s.rowNumber = :rowNumber " +
            "ORDER BY s.columnNumber")
    List<Seat> findConsecutiveSeats(
            @Param("venueId") Long venueId,
            @Param("rowNumber") String rowNumber,
            @Param("bookedSeatIds") List<Long> bookedSeatIds);

    // 取得VIP座位
    @Query("SELECT s FROM Seat s WHERE s.venue.venueId = :venueId " +
            "AND s.seatType = 'VIP' " +
            "AND s.status = 'ACTIVE' " +
            "AND s.seatId NOT IN :bookedSeatIds")
    List<Seat> findAvailableVipSeats(
            @Param("venueId") Long venueId,
            @Param("bookedSeatIds") List<Long> bookedSeatIds);

    // 取得特定區域的座位
    @Query("SELECT s FROM Seat s WHERE s.venue.venueId = :venueId " +
            "AND s.rowNumber BETWEEN :startRow AND :endRow " +
            "AND s.status = 'ACTIVE' " +
            "AND s.seatId NOT IN :bookedSeatIds " +
            "ORDER BY s.rowNumber, s.columnNumber")
    List<Seat> findAvailableSeatsInArea(
            @Param("venueId") Long venueId,
            @Param("startRow") String startRow,
            @Param("endRow") String endRow,
            @Param("bookedSeatIds") List<Long> bookedSeatIds);

    // 取得座位地圖
    @Query("SELECT new map(" +
            "s.rowNumber as row, " +
            "s.columnNumber as column, " +
            "s.seatType as type, " +
            "s.status as status, " +
            "CASE WHEN s.seatId IN :bookedSeatIds THEN true ELSE false END as isBooked) " +
            "FROM Seat s " +
            "WHERE s.venue.venueId = :venueId " +
            "ORDER BY s.rowNumber, s.columnNumber")
    List<java.util.Map<String, Object>> getSeatMap(
            @Param("venueId") Long venueId,
            @Param("bookedSeatIds") List<Long> bookedSeatIds);
}