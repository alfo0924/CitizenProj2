package org.example._citizenproj2.service;

import lombok.RequiredArgsConstructor;
import org.example._citizenproj2.dto.request.BookingRequest;
import org.example._citizenproj2.dto.request.GroupBookingRequest;
import org.example._citizenproj2.dto.response.BookingResponse;
import org.example._citizenproj2.exception.BookingException;
import org.example._citizenproj2.exception.SeatNotAvailableException;
import org.example._citizenproj2.model.*;
import org.example._citizenproj2.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
        List<Seat> seats = validateAndGetSeats(request.getSeatIds(), showing.getVenue().getVenueId());

        // 計算總金額
        BigDecimal totalAmount = calculateTotalAmount(showing, request.getTicketTypes());

        // 檢查錢包餘額
        if (!walletService.hasEnoughBalance(member.getMemberId(), totalAmount)) {
            throw new BookingException("餘額不足");
        }

        // 創建訂單
        Booking booking = new Booking();
        booking.setBookingId(generateBookingId());
        booking.setMember(member);
        booking.setShowing(showing);
        booking.setTotalAmount(totalAmount);
        booking.setBookingStatus(Booking.BookingStatus.PENDING);
        booking.setPaymentStatus(Booking.PaymentStatus.UNPAID);
        booking.setBookingTime(LocalDateTime.now());

        // 保存訂單
        booking = bookingRepository.save(booking);

        // 創建訂單明細
        createBookingDetails(booking, seats, request.getTicketTypes());

        // 更新座位狀態
        updateShowingAvailableSeats(showing);

        // 扣除錢包餘額
        walletService.processPayment(member.getMemberId(), totalAmount, booking.getBookingId());
        booking.setPaymentStatus(Booking.PaymentStatus.PAID);
        booking = bookingRepository.save(booking);

        return convertToBookingResponse(booking);
    }

    @Transactional
    public BookingResponse createGroupBooking(GroupBookingRequest request) {
        Showing showing = showingRepository.findById(request.getShowingId())
                .orElseThrow(() -> new BookingException("場次不存在"));

        if (request.getMinMembers() > request.getMaxMembers()) {
            throw new BookingException("最小人數不能大於最大人數");
        }

        // 創建團體訂單邏輯
        // ... 實作團體訂單相關邏輯

        return null; // 需要實作完整的團體訂票邏輯
    }

    public BookingResponse getBookingById(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingException("訂單不存在"));
        return convertToBookingResponse(booking);
    }

    public Page<BookingResponse> getMemberBookings(Long memberId, int page, int size) {
        return bookingRepository.findByMemberMemberId(memberId, PageRequest.of(page, size))
                .map(this::convertToBookingResponse);
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
        updateShowingAvailableSeats(booking.getShowing());

        // 退款處理
        if (booking.getPaymentStatus() == Booking.PaymentStatus.PAID) {
            walletService.processRefund(
                    booking.getMember().getMemberId(),
                    booking.getTotalAmount(),
                    booking.getBookingId()
            );
        }

        return convertToBookingResponse(booking);
    }

    public List<String> getAvailableSeats(Long showingId) {
        Showing showing = showingRepository.findById(showingId)
                .orElseThrow(() -> new BookingException("場次不存在"));
        List<Long> bookedSeatIds = bookingRepository.findBookedSeatIds(showingId);
        // 修改這裡，直接使用 Repository 的方法返回座位號碼列表
        return seatRepository.findAvailableSeatNumbers(
                showing.getVenue().getVenueId(),
                bookedSeatIds
        );
    }

    public boolean verifySeatsAvailability(Long showingId, List<Long> seatIds) {
        List<Long> bookedSeatIds = bookingRepository.findBookedSeatIds(showingId);
        return !bookedSeatIds.containsAll(seatIds);
    }

    @Transactional
    public BookingResponse updatePaymentStatus(String bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingException("訂單不存在"));

        booking.setPaymentStatus(Booking.PaymentStatus.valueOf(status.toUpperCase()));
        return convertToBookingResponse(bookingRepository.save(booking));
    }

    public Map<String, Object> getBookingStatistics(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime start = LocalDate.parse(startDate, formatter).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate, formatter).atTime(23, 59, 59);

        // 獲取統計數據列表
        List<Map<String, Object>> statisticsList = bookingRepository.getBookingStatistics(start, end);

        // 將列表中的第一個元素作為結果返回，如果列表為空則返回空的Map
        return statisticsList.isEmpty() ?
                new HashMap<>() :
                statisticsList.get(0);
    }

    private List<Seat> validateAndGetSeats(List<Long> seatIds, Long venueId) {
        List<Seat> seats = seatRepository.findAllById(seatIds);

        if (seats.size() != seatIds.size()) {
            throw new SeatNotAvailableException("部分座位不存在");
        }

        seats.forEach(seat -> {
            if (!seat.getVenue().getVenueId().equals(venueId)) {
                throw new SeatNotAvailableException("座位不屬於該影廳");
            }
            if (seat.getStatus() != Seat.Status.ACTIVE) {
                throw new SeatNotAvailableException("座位狀態不可用");
            }
        });

        return seats;
    }

    private String generateBookingId() {
        return "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BigDecimal calculateTotalAmount(Showing showing, List<String> ticketTypes) {
        return showing.getBasePrice().multiply(BigDecimal.valueOf(ticketTypes.size()));
    }

    private void createBookingDetails(Booking booking, List<Seat> seats, List<String> ticketTypes) {
        for (int i = 0; i < seats.size(); i++) {
            BookingDetail detail = new BookingDetail();
            detail.setBooking(booking);
            detail.setSeat(seats.get(i));
            detail.setTicketType(BookingDetail.tickettype.valueOf(ticketTypes.get(i)));
            // 需要實作 BookingDetailRepository 來保存明細
        }
    }

    private void updateShowingAvailableSeats(Showing showing) {
        int bookedSeats = bookingRepository.countConfirmedBookings(showing.getShowingId()).intValue();
        showing.setAvailableSeats(showing.getVenue().getTotalCapacity() - bookedSeats);
        showingRepository.save(showing);
    }

    private BookingResponse convertToBookingResponse(Booking booking) {
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