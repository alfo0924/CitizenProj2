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
    private TicketType ticketType = TicketType.ADULT;

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

    // 計算票價的方法
    public void calculateTicketPrice() {
        // 基本票價
        BigDecimal basePrice = booking.getShowing().getBasePrice();

        // 根據票種計算折扣
        switch (ticketType) {
            case CHILD:
                this.ticketPrice = basePrice.multiply(new BigDecimal("0.5"));
                break;
            case SENIOR:
                this.ticketPrice = basePrice.multiply(new BigDecimal("0.7"));
                break;
            case STUDENT:
                this.ticketPrice = basePrice.multiply(new BigDecimal("0.8"));
                break;
            default:
                this.ticketPrice = basePrice;
        }

        // 如果是VIP座位，增加價格
        if (seat.getSeatType() == Seat.SeatType.VIP) {
            this.ticketPrice = this.ticketPrice.multiply(new BigDecimal("1.2"));
        }
    }

    // 驗證座位是否可用
    public boolean validateSeat() {
        return seat.isAvailable() &&
                !isAlreadyBooked();
    }

    private boolean isAlreadyBooked() {
        // 實作檢查座位是否已被預訂的邏輯
        return false; // 需要實際實作
    }
}