package org.example._citizenproj2.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Data  // 已有，但確保存在
@Getter // 明確添加
@Setter // 明確添加
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    private String bookingId;  // 自定義格式的訂單編號

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "showing_id", nullable = false)
    private Showing showing;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus = BookingStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    private String paymentMethod;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<BookingDetail> bookingDetails;

    @CreationTimestamp
    private LocalDateTime bookingTime;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // 枚舉定義
    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }

    public enum PaymentStatus {
        UNPAID, PAID, REFUNDED
    }

    // 訂單編號生成方法
    public void generateBookingId() {
        if (this.bookingId == null) {
            this.bookingId = "BK" + System.currentTimeMillis();
        }
    }
    // 添加以下方法
    public int getTicketCount() {
        if (bookingDetails == null || bookingDetails.isEmpty()) {
            return 0;
        }
        return bookingDetails.size();
    }

    // 業務方法
    public boolean isPending() {
        return bookingStatus == BookingStatus.PENDING;
    }

    public boolean isConfirmed() {
        return bookingStatus == BookingStatus.CONFIRMED;
    }

    public boolean isCancelled() {
        return bookingStatus == BookingStatus.CANCELLED;
    }

    public boolean isCompleted() {
        return bookingStatus == BookingStatus.COMPLETED;
    }

    public boolean isPaid() {
        return paymentStatus == PaymentStatus.PAID;
    }

    public boolean isRefunded() {
        return paymentStatus == PaymentStatus.REFUNDED;
    }

    public boolean isRefundable() {
        return isPaid() && !isRefunded() &&
                (isCancelled() || showing.isCancelled());
    }

    public boolean isCancellable() {
        return (isPending() || isConfirmed()) &&
                !isCancelled() &&
                LocalDateTime.now().isBefore(showing.getShowTime());
    }

    // 計算總金額
    public void calculateTotalAmount() {
        if (bookingDetails == null || bookingDetails.isEmpty()) {
            this.totalAmount = BigDecimal.ZERO;
            return;
        }

        this.totalAmount = bookingDetails.stream()
                .map(detail -> showing.getBasePrice().multiply(detail.getTicketType().getPriceMultiplier()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 添加訂票明細
    public void addBookingDetail(BookingDetail detail) {
        if (bookingDetails == null) {
            bookingDetails = new ArrayList<>();
        }
        bookingDetails.add(detail);
        detail.setBooking(this);
        calculateTotalAmount();
    }

    // 移除訂票明細
    public void removeBookingDetail(BookingDetail detail) {
        if (bookingDetails != null) {
            bookingDetails.remove(detail);
            detail.setBooking(null);
            calculateTotalAmount();
        }
    }

    // 確認訂單
    public void confirm() {
        if (!isPending()) {
            throw new IllegalStateException("只有待確認的訂單可以確認");
        }
        this.bookingStatus = BookingStatus.CONFIRMED;
    }

    // 取消訂單
    public void cancel() {
        if (!isCancellable()) {
            throw new IllegalStateException("訂單無法取消");
        }
        this.bookingStatus = BookingStatus.CANCELLED;
    }

    // 完成訂單
    public void complete() {
        if (!isConfirmed() || !isPaid()) {
            throw new IllegalStateException("只有已確認且已付款的訂單可以完成");
        }
        this.bookingStatus = BookingStatus.COMPLETED;
    }

    // 更新付款狀態
    public void updatePaymentStatus(PaymentStatus newStatus) {
        this.paymentStatus = newStatus;
        if (newStatus == PaymentStatus.PAID && isPending()) {
            confirm();
        }
    }

    // 預處理方法
    @PrePersist
    public void prePersist() {
        generateBookingId();
        if (bookingStatus == null) {
            bookingStatus = BookingStatus.PENDING;
        }
        if (paymentStatus == null) {
            paymentStatus = PaymentStatus.UNPAID;
        }
        if (bookingDetails == null) {
            bookingDetails = new ArrayList<>();
        }
    }
    public boolean isValid() {
        return bookingStatus == BookingStatus.CONFIRMED ||
                bookingStatus == BookingStatus.PENDING;
    }
}