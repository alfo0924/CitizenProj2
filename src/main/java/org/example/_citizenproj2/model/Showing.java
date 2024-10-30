package org.example._citizenproj2.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "showings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Showing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long showingId;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(nullable = false)
    private LocalDate showDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private Integer availableSeats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShowingStatus showingStatus = ShowingStatus.AVAILABLE;

    @OneToMany(mappedBy = "showing", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum ShowingStatus {
        AVAILABLE("可訂票"),
        ALMOST_FULL("即將額滿"),
        FULL("已滿座"),
        CANCELLED("已取消");

        private final String displayName;

        ShowingStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // 業務方法
    public LocalDateTime getShowTime() {
        return LocalDateTime.of(showDate, startTime);
    }

    public boolean isAvailable() {
        return showingStatus == ShowingStatus.AVAILABLE
                || showingStatus == ShowingStatus.ALMOST_FULL;
    }

    public boolean isFull() {
        return showingStatus == ShowingStatus.FULL;
    }

    public boolean isCancelled() {
        return showingStatus == ShowingStatus.CANCELLED;
    }

    public void updateAvailableSeats(int bookedSeats) {
        this.availableSeats -= bookedSeats;
        updateStatus();
    }

    private void updateStatus() {
        if (availableSeats <= 0) {
            this.showingStatus = ShowingStatus.FULL;
        } else if (availableSeats <= venue.getTotalCapacity() * 0.2) {
            this.showingStatus = ShowingStatus.ALMOST_FULL;
        } else {
            this.showingStatus = ShowingStatus.AVAILABLE;
        }
    }

    public void cancel() {
        this.showingStatus = ShowingStatus.CANCELLED;
    }

    public boolean isBookable() {
        return isAvailable() &&
                LocalDateTime.now().isBefore(getShowTime()) &&
                availableSeats > 0;
    }

    public LocalDateTime getEndDateTime() {
        return LocalDateTime.of(showDate, endTime);
    }

    public long getDurationMinutes() {
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    public boolean hasConflict(Showing other) {
        if (!this.venue.equals(other.venue)) {
            return false;
        }

        LocalDateTime thisStart = this.getShowTime();
        LocalDateTime thisEnd = this.getEndDateTime();
        LocalDateTime otherStart = other.getShowTime();
        LocalDateTime otherEnd = other.getEndDateTime();

        return !thisEnd.isBefore(otherStart) && !otherEnd.isBefore(thisStart);
    }

    // 預處理方法
    @PrePersist
    public void prePersist() {
        if (showingStatus == null) {
            showingStatus = ShowingStatus.AVAILABLE;
        }
        if (bookings == null) {
            bookings = new ArrayList<>();
        }
    }

    // 添加訂票
    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setShowing(this);
        updateAvailableSeats(booking.getTicketCount());
    }

    // 移除訂票
    public void removeBooking(Booking booking) {
        bookings.remove(booking);
        booking.setShowing(null);
        updateAvailableSeats(-booking.getTicketCount());
    }
}