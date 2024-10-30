package org.example._citizenproj2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private String bookingId;
    private Long memberId;
    private Long showingId;
    private BigDecimal totalAmount;
    private String bookingStatus;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime bookingTime;

    // 訂票詳細資訊
    private ShowingDetails showingDetails;
    private List<SeatInfo> seatDetails;
    private PaymentDetails paymentDetails;
    private DiscountInfo discountInfo;

    @Data
    @Builder
    public static class ShowingDetails {
        private String movieName;
        private String venueName;
        private String theaterNumber;
        private LocalDateTime showTime;
        private String moviePosterUrl;
    }

    @Data
    @Builder
    public static class SeatInfo {
        private Long seatId;
        private String rowNumber;
        private Integer columnNumber;
        private String seatType;
        private String ticketType;
        private BigDecimal ticketPrice;
    }

    @Data
    @Builder
    public static class PaymentDetails {
        private BigDecimal originalAmount;
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
        private LocalDateTime paymentTime;
        private String transactionId;
    }

    @Data
    @Builder
    public static class DiscountInfo {
        private String discountName;
        private String discountType;
        private BigDecimal discountValue;
        private String discountCode;
    }

    // 用於團體訂票的額外資訊
    @Data
    @Builder
    public static class GroupBookingInfo {
        private Boolean isGroupBooking;
        private Integer totalMembers;
        private Integer confirmedMembers;
        private LocalDateTime expiryTime;
        private List<GroupMemberInfo> memberDetails;
    }

    @Data
    @Builder
    public static class GroupMemberInfo {
        private Long memberId;
        private String memberName;
        private String paymentStatus;
        private LocalDateTime joinTime;
    }
}