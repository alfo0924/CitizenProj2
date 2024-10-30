package org.example._citizenproj2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "city_movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieId;

    @Column(nullable = false)
    private String movieName;

    private String originalName;
    private String director;

    @Column(columnDefinition = "TEXT")
    private String cast;

    @Column(nullable = false)
    private Integer duration;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Date releaseDate;

    private Date endDate;
    private String rating;
    private String language;
    private String subtitle;
    private String posterUrl;
    private String trailerUrl;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private MovieCategory category;

    @Enumerated(EnumType.STRING)
    private MovieStatus movieStatus = MovieStatus.COMING;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // 枚舉定義
    public enum MovieStatus {
        COMING, SHOWING, ENDED
    }
}