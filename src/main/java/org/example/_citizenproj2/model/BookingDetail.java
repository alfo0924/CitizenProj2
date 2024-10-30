package org.example._citizenproj2.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_details")
@Data  // 已有，但確保存在
@Getter // 明確添加
@Setter // 明確添加
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetail {
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
    private tickettype ticketType = tickettype.ADULT;

    @Column(nullable = false)
    private BigDecimal ticketPrice;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum TicketType {
        ADULT,
        CHILD,
        SENIOR,
        STUDENT
    }





    @Getter
    public enum tickettype {
        ADULT(new BigDecimal("1.0"), "全票"),
        CHILD(new BigDecimal("0.5"), "兒童票"),
        SENIOR(new BigDecimal("0.7"), "敬老票"),
        STUDENT(new BigDecimal("0.8"), "學生票");

        private final BigDecimal priceMultiplier;
        private final String displayName;

        tickettype(BigDecimal priceMultiplier, String displayName) {
            this.priceMultiplier = priceMultiplier;
            this.displayName = displayName;
        }

    }

    // 修改計算票價的方法
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

    // 新增驗證方法
    public boolean isValidTicketType() {
        // 檢查特定票種的限制
        switch (ticketType) {
            case CHILD:
                return booking.getMember().isChildTicketEligible();
            case SENIOR:
                return booking.getMember().isSeniorTicketEligible();
            case STUDENT:
                return booking.getMember().isStudentTicketEligible();
            default:
                return true;
        }
    }

    // 新增取得票價描述的方法
    public String getTicketDescription() {
        StringBuilder description = new StringBuilder();
        description.append(ticketType.getDisplayName());

        if (seat.getSeatType() == Seat.SeatType.VIP) {
            description.append(" (VIP座位)");
        }

        description.append(String.format(" - NT$%s", ticketPrice));
        return description.toString();
    }

    // 新增預處理方法
    @PrePersist
    public void prePersist() {
        if (ticketType == null) {
            ticketType = tickettype.ADULT;
        }
        calculateTicketPrice();
    }

    // 新增座位驗證方法
    public boolean validateSeat() {
        if (!seat.isAvailable()) {
            return false;
        }

        if (isAlreadyBooked()) {
            return false;
        }

        // 檢查VIP座位限制
        if (seat.getSeatType() == Seat.SeatType.VIP &&
                !booking.getMember().isVipSeatEligible()) {
            return false;
        }

        return true;
    }

    private boolean isAlreadyBooked() {
        return booking.getShowing().getBookings().stream()
                .flatMap(b -> b.getBookingDetails().stream())
                .anyMatch(detail -> detail.getSeat().equals(seat) &&
                        !detail.getBooking().isCancelled());
    }
}