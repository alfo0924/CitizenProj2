package org.example._citizenproj2.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "city_movies")
@Data  // 已有，但確保存在
@Getter // 明確添加
@Setter // 明確添加
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
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<Showing> showings = new ArrayList<>();

    // getter and setter
    public List<Showing> getShowings() {
        return showings;
    }
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