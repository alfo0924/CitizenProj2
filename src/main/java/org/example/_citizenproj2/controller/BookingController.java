package org.example._citizenproj2.controller;

import lombok.RequiredArgsConstructor;
import org.example._citizenproj2.dto.request.BookingRequest;
import org.example._citizenproj2.dto.request.GroupBookingRequest;
import org.example._citizenproj2.dto.response.BookingResponse;
import org.example._citizenproj2.exception.SeatNotAvailableException;
import org.example._citizenproj2.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @GetMapping("/{bookingId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable String bookingId) {
        return ResponseEntity.ok(bookingService.getBookingById(bookingId));
    }

    @GetMapping("/member/{memberId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BookingResponse>> getMemberBookings(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok((List<BookingResponse>) bookingService.getMemberBookings(memberId, page, size));
    }

    @PutMapping("/{bookingId}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable String bookingId) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId));
    }

    @GetMapping("/showing/{showingId}/available-seats")
    public ResponseEntity<List<String>> getAvailableSeats(@PathVariable Long showingId) {
        return ResponseEntity.ok(bookingService.getAvailableSeats(showingId));
    }

    @PostMapping("/verify-seats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> verifySeatsAvailability(
            @RequestParam Long showingId,
            @RequestBody List<Long> seatIds) {
        return ResponseEntity.ok(bookingService.verifySeatsAvailability(showingId, seatIds));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getBookingStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(bookingService.getBookingStatistics(startDate, endDate));
    }

    @PostMapping("/group")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingResponse> createGroupBooking(
            @Valid @RequestBody GroupBookingRequest request) {
        return ResponseEntity.ok(bookingService.createGroupBooking(request));
    }

    @PutMapping("/{bookingId}/payment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingResponse> updatePaymentStatus(
            @PathVariable String bookingId,
            @RequestParam String paymentStatus) {
        return ResponseEntity.ok(bookingService.updatePaymentStatus(bookingId, paymentStatus));
    }

    @ExceptionHandler(SeatNotAvailableException.class)
    public ResponseEntity<String> handleSeatNotAvailableException(SeatNotAvailableException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}