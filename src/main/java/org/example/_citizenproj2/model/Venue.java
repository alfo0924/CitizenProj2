package org.example._citizenproj2.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venues")
@Data  // 已有，但確保存在
@Getter // 明確添加
@Setter // 明確添加
@NoArgsConstructor
@AllArgsConstructor
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long venueId;

    @Column(nullable = false, length = 50)
    private String venueName;

    @Column(nullable = false, length = 10, unique = true)
    private String theaterNumber;

    @Column(nullable = false)
    private Integer seatRows;

    @Column(nullable = false)
    private Integer seatColumns;

    @Column(nullable = false)
    private Integer totalCapacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VenueType venueType = VenueType.TWO_D;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL)
    private List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL)
    private List<Showing> showings = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum VenueType {
        TWO_D("2D"),
        THREE_D("3D"),
        IMAX("IMAX"),
        FOUR_DX("4DX");

        private final String display;

        VenueType(String display) {
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }
    }

    public enum Status {
        ACTIVE,
        MAINTENANCE,
        INACTIVE
    }

    // 業務方法
    public boolean isAvailable() {
        return status == Status.ACTIVE;
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
        seat.setVenue(this);
    }

    public void removeSeat(Seat seat) {
        seats.remove(seat);
        seat.setVenue(null);
    }

    public void addShowing(Showing showing) {
        showings.add(showing);
        showing.setVenue(this);
    }

    public void removeShowing(Showing showing) {
        showings.remove(showing);
        showing.setVenue(null);
    }

    // 驗證方法
    @PrePersist
    @PreUpdate
    public void validateCapacity() {
        if (seatRows != null && seatColumns != null) {
            int calculatedCapacity = seatRows * seatColumns;
            if (totalCapacity == null || totalCapacity != calculatedCapacity) {
                totalCapacity = calculatedCapacity;
            }
        }
    }

    // 座位管理方法
    public List<Seat> getAvailableSeats() {
        return seats.stream()
                .filter(seat -> seat.getStatus() == Seat.Status.ACTIVE)
                .toList();
    }

    public boolean hasSufficientCapacity(int requiredSeats) {
        return getAvailableSeats().size() >= requiredSeats;
    }

    public List<Seat> getVipSeats() {
        return seats.stream()
                .filter(seat -> seat.getSeatType() == Seat.SeatType.VIP)
                .toList();
    }

    // 場次管理方法
    public List<Showing> getActiveShowings() {
        return showings.stream()
                .filter(showing -> showing.getShowingStatus() == Showing.ShowingStatus.AVAILABLE)
                .toList();
    }

    public boolean hasConflictingShowing(LocalDateTime startTime, LocalDateTime endTime) {
        return showings.stream()
                .anyMatch(showing ->
                        (showing.getStartTime().isBefore(LocalTime.from(endTime)) &&
                                showing.getEndTime().isAfter(LocalTime.from(startTime))));
    }
}