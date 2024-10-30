package org.example._citizenproj2.service;

import lombok.RequiredArgsConstructor;
import org.example._citizenproj2.dto.request.BookingRequest;
import org.example._citizenproj2.dto.response.BookingResponse;
import org.example._citizenproj2.exception.BookingException;
import org.example._citizenproj2.exception.SeatNotAvailableException;
import org.example._citizenproj2.model.*;
import org.example._citizenproj2.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ShowingRepository showingRepository;
    private final SeatRepository seatRepository;
    private final MemberRepository memberRepository;
    private final WalletService walletService;

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        // 驗證場次
        Showing showing = showingRepository.findById(request.getShowingId())
                .orElseThrow(() -> new BookingException("場次不存在"));

        // 驗證會員
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new BookingException("會員不存在"));

        // 驗證座位
        List<Seat> seats = validateAndGetSeats(request.getSeatIds(), showing.getVenueId());

        // 計算總金額
        BigDecimal totalAmount = calculateTotalAmount(showing, request.getTicketTypes());

        // 檢查錢包餘額
        if (!walletService.hasEnoughBalance(member.getMemberId(), totalAmount)) {
            throw new BookingException("餘額不足");
        }

        // 創建訂單
        Booking booking = new Booking();
        booking.setMember(member);
        booking.setShowing(showing);
        booking.setTotalAmount(totalAmount);
        booking.setBookingStatus(Booking.BookingStatus.PENDING);

        // 保存訂單
        booking = bookingRepository.save(booking);

        // 創建訂單明細
        createBookingDetails(booking, seats, request.getTicketTypes());

        // 更新座位狀態
        updateShowingAvailableSeats(showing.getShowingId());

        // 扣除錢包餘額
        walletService.processPayment(member.getMemberId(), totalAmount, booking.getBookingId());

        return convertToBookingResponse(booking);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingException("訂單不存在"));
        return convertToBookingResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMemberBookings(Long memberId, int page, int size) {
        return bookingRepository.findByMemberMemberId(memberId, PageRequest.of(page, size))
                .stream()
                .map(this::convertToBookingResponse)
                .toList();
    }

    @Transactional
    public BookingResponse cancelBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingException("訂單不存在"));

        if (booking.getBookingStatus() != Booking.BookingStatus.PENDING &&
                booking.getBookingStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new BookingException("訂單狀態不允許取消");
        }

        booking.setBookingStatus(Booking.BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);

        // 退回座位
        updateShowingAvailableSeats(booking.getShowing().getShowingId());

        // 退款處理
        if (booking.getPaymentStatus() == Booking.PaymentStatus.PAID) {
            walletService.processRefund(booking.getMember().getMemberId(),
                    booking.getTotalAmount(),
                    booking.getBookingId());
        }

        return convertToBookingResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<String> getAvailableSeats(Long showingId) {
        List<Long> bookedSeatIds = bookingRepository.findBookedSeatIds(showingId);
        return seatRepository.findAvailableSeats(showingId, bookedSeatIds);
    }

    @Transactional(readOnly = true)
    public boolean verifySeatsAvailability(Long showingId, List<Long> seatIds) {
        List<Long> bookedSeatIds = bookingRepository.findBookedSeatIds(showingId);
        return !bookedSeatIds.containsAll(seatIds);
    }

    private List<Seat> validateAndGetSeats(List<Long> seatIds, Long venueId) {
        List<Seat> seats = seatRepository.findAllById(seatIds);

        if (seats.size() != seatIds.size()) {
            throw new SeatNotAvailableException("部分座位不存在");
        }

        seats.forEach(seat -> {
            if (!seat.getVenueId().equals(venueId)) {
                throw new SeatNotAvailableException("座位不屬於該影廳");
            }
            if (seat.getStatus() != Seat.Status.ACTIVE) {
                throw new SeatNotAvailableException("座位狀態不可用");
            }
        });

        return seats;
    }

    private BigDecimal calculateTotalAmount(Showing showing, List<String> ticketTypes) {
        // 實作票價計算邏輯
        return showing.getBasePrice().multiply(BigDecimal.valueOf(ticketTypes.size()));
    }

    private void createBookingDetails(Booking booking, List<Seat> seats, List<String> ticketTypes) {
        // 實作訂單明細創建邏輯
    }

    private void updateShowingAvailableSeats(Long showingId) {
        // 實作座位數更新邏輯
    }

    private BookingResponse convertToBookingResponse(Booking booking) {
        // 實作轉換邏輯
        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .memberId(booking.getMember().getMemberId())
                .showingId(booking.getShowing().getShowingId())
                .totalAmount(booking.getTotalAmount())
                .bookingStatus(booking.getBookingStatus().toString())
                .paymentStatus(booking.getPaymentStatus().toString())
                .bookingTime(booking.getBookingTime())
                .build();
    }
}