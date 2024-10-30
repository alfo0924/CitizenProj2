package org.example._citizenproj2.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movie_categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = false, unique = true, length = 50)
    private String categoryName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Movie> movies = new ArrayList<>();

    @Column(nullable = false)
    private Boolean isActive = true;

    private Integer displayOrder;

    @Enumerated(EnumType.STRING)
    private CategoryType categoryType = CategoryType.GENRE;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum CategoryType {
        GENRE("類型"),
        LANGUAGE("語言"),
        SPECIAL("特別"),
        OTHER("其他");

        private final String displayName;

        CategoryType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // 業務方法
    public void addMovie(Movie movie) {
        movies.add(movie);
        movie.setCategory(this);
    }

    public void removeMovie(Movie movie) {
        movies.remove(movie);
        movie.setCategory(null);
    }

    public int getMovieCount() {
        return movies.size();
    }

    public List<Movie> getActiveMovies() {
        return movies.stream()
                .filter(movie -> movie.getMovieStatus() == Movie.MovieStatus.SHOWING)
                .toList();
    }

    public List<Movie> getUpcomingMovies() {
        return movies.stream()
                .filter(movie -> movie.getMovieStatus() == Movie.MovieStatus.COMING)
                .toList();
    }

    // 預處理方法
    @PrePersist
    public void prePersist() {
        if (isActive == null) {
            isActive = true;
        }
        if (categoryType == null) {
            categoryType = CategoryType.GENRE;
        }
        if (displayOrder == null) {
            displayOrder = 0;
        }
    }
}