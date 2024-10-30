package org.example._citizenproj2.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "seats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(name = "seat_row", nullable = false)
    private String rowNumber;

    @Column(nullable = false)
    private Integer columnNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType = SeatType.REGULAR;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @OneToMany(mappedBy = "seat")
    @Builder.Default
    private List<BookingDetail> bookingDetails = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum SeatType {
        REGULAR("一般座位"),
        VIP("VIP座位"),
        COUPLE("雙人座"),
        DISABLED("無障礙座位");

        private final String displayName;

        SeatType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Status {
        ACTIVE("可用"),
        MAINTENANCE("維護中"),
        INACTIVE("停用");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // 業務方法
    public boolean isAvailable() {
        return status == Status.ACTIVE;
    }

    public boolean isVIP() {
        return seatType == SeatType.VIP;
    }

    public boolean isCouple() {
        return seatType == SeatType.COUPLE;
    }

    public boolean isDisabled() {
        return seatType == SeatType.DISABLED;
    }

    public String getFullSeatNumber() {
        return rowNumber + String.format("%02d", columnNumber);
    }

    public boolean isBookable() {
        return isAvailable() && !isBooked();
    }

    public boolean isBooked() {
        return bookingDetails.stream()
                .anyMatch(detail -> detail.getBooking().isValid());
    }

    // 預處理方法
    @PrePersist
    public void prePersist() {
        if (seatType == null) {
            seatType = SeatType.REGULAR;
        }
        if (status == null) {
            status = Status.ACTIVE;
        }
        if (bookingDetails == null) {
            bookingDetails = new ArrayList<>();
        }
    }

    // 座位資訊
    public String getSeatInfo() {
        return String.format("%s - %s (%s)",
                getFullSeatNumber(),
                seatType.getDisplayName(),
                status.getDisplayName());
    }

    // 驗證方法
    public boolean validateSeatType(Member member) {
        switch (seatType) {
            case VIP:
                return member.isVipMember();
            case DISABLED:
                return member.isDisabilityCardHolder();
            default:
                return true;
        }
    }

    // 座位位置驗證
    public boolean isValidPosition() {
        return rowNumber != null &&
                columnNumber != null &&
                columnNumber > 0 &&
                columnNumber <= venue.getSeatColumns();
    }

    // 更新狀態
    public void updateStatus(Status newStatus) {
        if (this.status != newStatus) {
            this.status = newStatus;
            // 可以添加狀態變更日誌或通知邏輯
        }
    }

}