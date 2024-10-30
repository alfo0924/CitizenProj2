package org.example._citizenproj2.controller;

import lombok.RequiredArgsConstructor;
import org.example._citizenproj2.dto.request.MovieRequest;
import org.example._citizenproj2.dto.response.MovieResponse;
import org.example._citizenproj2.service.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieRequest request) {
        return new ResponseEntity<>(movieService.createMovie(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovie(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @GetMapping
    public ResponseEntity<Page<MovieResponse>> getAllMovies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return ResponseEntity.ok(movieService.getAllMovies(keyword, category, status, pageable));
    }

    @GetMapping("/showing")
    public ResponseEntity<List<MovieResponse>> getNowShowingMovies() {
        return ResponseEntity.ok(movieService.getNowShowingMovies());
    }

    @GetMapping("/coming")
    public ResponseEntity<List<MovieResponse>> getComingSoonMovies() {
        return ResponseEntity.ok(movieService.getComingSoonMovies());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponse> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody MovieRequest request) {
        return ResponseEntity.ok(movieService.updateMovie(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/showings")
    public ResponseEntity<List<MovieResponse.ShowingInfo>> getMovieShowings(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieShowings(id));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<MovieResponse.Category>> getAllCategories() {
        return ResponseEntity.ok(movieService.getAllCategories());
    }

    @PostMapping("/{id}/ratings")
    public ResponseEntity<MovieResponse.Rating> rateMovie(
            @PathVariable Long id,
            @Valid @RequestBody MovieRequest.Rating request) {
        return ResponseEntity.ok(movieService.rateMovie(id, request));
    }

    @GetMapping("/{id}/ratings")
    public ResponseEntity<Page<MovieResponse.Rating>> getMovieRatings(
            @PathVariable Long id,
            Pageable pageable) {
        return ResponseEntity.ok(movieService.getMovieRatings(id, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MovieResponse>> searchMovies(
            @RequestParam String query,
            Pageable pageable) {
        return ResponseEntity.ok(movieService.searchMovies(query, pageable));
    }
}