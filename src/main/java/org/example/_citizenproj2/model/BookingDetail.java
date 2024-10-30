package org.example._citizenproj2.model;

import ch.qos.logback.classic.Level;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetail {
    public static Level tickettype;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingDetailId;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketType ticketType = TicketType.ADULT;

    @Column(nullable = false)
    private BigDecimal ticketPrice;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum TicketType {
        ADULT(new BigDecimal("1.0"), "全票"),
        CHILD(new BigDecimal("0.5"), "兒童票"),
        SENIOR(new BigDecimal("0.7"), "敬老票"),
        STUDENT(new BigDecimal("0.8"), "學生票");

        private final BigDecimal priceMultiplier;
        private final String displayName;

        TicketType(BigDecimal priceMultiplier, String displayName) {
            this.priceMultiplier = priceMultiplier;
            this.displayName = displayName;
        }

        public BigDecimal getPriceMultiplier() {
            return priceMultiplier;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // 計算票價的方法
    public void calculateTicketPrice() {
        // 基本票價
        BigDecimal basePrice = booking.getShowing().getBasePrice();

        // 使用票種倍數計算
        this.ticketPrice = basePrice.multiply(ticketType.getPriceMultiplier());

        // 如果是VIP座位，增加價格
        if (seat.getSeatType() == Seat.SeatType.VIP) {
            this.ticketPrice = this.ticketPrice.multiply(new BigDecimal("1.2"));
        }
    }

    // 驗證座位是否可用
    public boolean validateSeat() {
        return seat.isAvailable() && !isAlreadyBooked();
    }

    // 驗證票種是否適用
    public boolean validateTicketType() {
        if (booking == null || booking.getMember() == null) {
            return false;
        }

        Member member = booking.getMember();
        return switch (ticketType) {
            case CHILD -> member.isChildTicketEligible();
            case SENIOR -> member.isSeniorTicketEligible();
            case STUDENT -> member.isStudentTicketEligible();
            default -> true;
        };
    }

    private boolean isAlreadyBooked() {
        if (booking == null || booking.getShowing() == null) {
            return false;
        }

        return booking.getShowing().getBookings().stream()
                .filter(b -> b.getBookingStatus() != Booking.BookingStatus.CANCELLED)
                .flatMap(b -> b.getBookingDetails().stream())
                .anyMatch(detail -> detail.getSeat().equals(this.seat));
    }

    // 預處理方法
    @PrePersist
    public void prePersist() {
        if (ticketType == null) {
            ticketType = TicketType.ADULT;
        }
        calculateTicketPrice();
    }

    // 取得票價描述
    public String getTicketDescription() {
        StringBuilder description = new StringBuilder();
        description.append(ticketType.getDisplayName());

        if (seat != null && seat.getSeatType() == Seat.SeatType.VIP) {
            description.append(" (VIP座位)");
        }

        if (ticketPrice != null) {
            description.append(String.format(" - NT$%s", ticketPrice));
        }

        return description.toString();
    }
}