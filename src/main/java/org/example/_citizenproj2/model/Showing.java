package org.example._citizenproj2.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "showings")
@Data  // 已有，但確保存在
@Getter // 明確添加
@Setter // 明確添加
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

    @OneToMany(mappedBy = "showing")
    private List<Booking> bookings;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum ShowingStatus {
        AVAILABLE,
        ALMOST_FULL,
        FULL,
        CANCELLED
    }

    // 業務方法
    public boolean isAvailable() {
        return showingStatus == ShowingStatus.AVAILABLE
                || showingStatus == ShowingStatus.ALMOST_FULL;
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
}