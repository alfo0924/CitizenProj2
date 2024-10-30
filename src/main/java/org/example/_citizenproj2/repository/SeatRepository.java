package org.example._citizenproj2.repository;

import org.example._citizenproj2.model.Seat;
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

    // 可用座位查詢
    @Query("SELECT s FROM Seat s WHERE s.venue.venueId = :venueId " +
            "AND s.status = 'ACTIVE'")
    List<Seat> findAvailableSeats(@Param("venueId") Long venueId);

    // 座位狀態查詢
    @Query("SELECT s FROM Seat s WHERE s.venue.venueId = :venueId " +
            "AND s.seatId NOT IN :bookedSeatIds")
    List<Seat> findUnbookedSeats(
            @Param("venueId") Long venueId,
            @Param("bookedSeatIds") List<Long> bookedSeatIds);

    // 座位號碼查詢
    @Query("SELECT s.rowNumber || s.columnNumber FROM Seat s " +
            "WHERE s.venue.venueId = :venueId " +
            "AND s.status = 'ACTIVE' " +
            "AND s.seatId NOT IN :bookedSeatIds")
    List<String> findAvailableSeatNumbers(
            @Param("venueId") Long venueId,
            @Param("bookedSeatIds") List<Long> bookedSeatIds);

    // 特定類型座位查詢
    @Query("SELECT s FROM Seat s WHERE s.venue.venueId = :venueId " +
            "AND s.seatType = :seatType " +
            "AND s.status = 'ACTIVE'")
    List<Seat> findAvailableSeatsByType(
            @Param("venueId") Long venueId,
            @Param("seatType") Seat.SeatType seatType);

    // 座位容量統計
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.venue.venueId = :venueId " +
            "AND s.status = 'ACTIVE'")
    int countAvailableSeats(@Param("venueId") Long venueId);
}