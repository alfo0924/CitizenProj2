package org.example._citizenproj2.controller;

import lombok.RequiredArgsConstructor;
import org.example._citizenproj2.dto.request.MovieRequest;
import org.example._citizenproj2.dto.request.RatingRequest;
import org.example._citizenproj2.dto.response.MovieResponse;
import org.example._citizenproj2.dto.response.CategoryResponse;
import org.example._citizenproj2.dto.response.RatingResponse;
import org.example._citizenproj2.service.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieRequest request) {
        return new ResponseEntity<>(movieService.createMovie(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovie(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovie(id));
    }

    @GetMapping
    public ResponseEntity<Page<MovieResponse>> getAllMovies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "releaseDate,desc") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return ResponseEntity.ok(movieService.getAllMovies(keyword, categoryId, status, pageable));
    }

    @GetMapping("/showing")
    public ResponseEntity<List<MovieResponse>> getCurrentlyShowingMovies() {
        return ResponseEntity.ok(movieService.getCurrentlyShowingMovies());
    }

    @GetMapping("/coming")
    public ResponseEntity<List<MovieResponse>> getUpcomingMovies() {
        return ResponseEntity.ok(movieService.getUpcomingMovies());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponse> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody MovieRequest request) {
        return ResponseEntity.ok(movieService.updateMovie(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/showings")
    public ResponseEntity<List<MovieResponse.ShowingInfo>> getMovieShowings(
            @PathVariable Long id,
            @RequestParam(required = false) String date) {
        return ResponseEntity.ok(movieService.getMovieShowings(id, date));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(movieService.getAllCategories());
    }

    @PostMapping("/{id}/ratings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RatingResponse> rateMovie(
            @PathVariable Long id,
            @Valid @RequestBody RatingRequest request) {
        return ResponseEntity.ok(movieService.rateMovie(id, request));
    }

    @GetMapping("/{id}/ratings")
    public ResponseEntity<Page<RatingResponse>> getMovieRatings(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(movieService.getMovieRatings(id, PageRequest.of(page, size)));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MovieResponse>> searchMovies(
            @RequestParam String query,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "releaseDate,desc") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return ResponseEntity.ok(movieService.searchMovies(query, categoryIds, statuses, pageable));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponse> updateMovieStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(movieService.updateMovieStatus(id, status));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getMovieStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(movieService.getMovieStatistics(startDate, endDate));
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<String> handleMovieNotFoundException(MovieNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}