package org.example._citizenproj2.service;

import lombok.RequiredArgsConstructor;
import org.example._citizenproj2.dto.request.MovieRequest;
import org.example._citizenproj2.dto.response.MovieResponse;
import org.example._citizenproj2.exception.MovieNotFoundException;
import org.example._citizenproj2.model.Movie;
import org.example._citizenproj2.model.MovieCategory;
import org.example._citizenproj2.repository.MovieCategoryRepository;
import org.example._citizenproj2.repository.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieCategoryRepository categoryRepository;

    @Transactional
    public MovieResponse createMovie(MovieRequest request) {
        MovieCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        Movie movie = new Movie();
        movie.setMovieName(request.getMovieName());
        movie.setOriginalName(request.getOriginalName());
        movie.setDirector(request.getDirector());
        movie.setCast(request.getCast());
        movie.setDuration(request.getDuration());
        movie.setDescription(request.getDescription());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setEndDate(request.getEndDate());
        movie.setRating(request.getRating());
        movie.setLanguage(request.getLanguage());
        movie.setSubtitle(request.getSubtitle());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setTrailerUrl(request.getTrailerUrl());
        movie.setCategory(category);
        movie.setMovieStatus(Movie.MovieStatus.COMING);

        Movie savedMovie = movieRepository.save(movie);
        return convertToResponse(savedMovie);
    }

    @Transactional(readOnly = true)
    public MovieResponse getMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));
        return convertToResponse(movie);
    }

    @Transactional
    public MovieResponse updateMovie(Long id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));

        MovieCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        movie.setMovieName(request.getMovieName());
        movie.setOriginalName(request.getOriginalName());
        movie.setDirector(request.getDirector());
        movie.setCast(request.getCast());
        movie.setDuration(request.getDuration());
        movie.setDescription(request.getDescription());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setEndDate(request.getEndDate());
        movie.setRating(request.getRating());
        movie.setLanguage(request.getLanguage());
        movie.setSubtitle(request.getSubtitle());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setTrailerUrl(request.getTrailerUrl());
        movie.setCategory(category);

        Movie updatedMovie = movieRepository.save(movie);
        return convertToResponse(updatedMovie);
    }

    @Transactional
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new MovieNotFoundException("Movie not found with id: " + id);
        }
        movieRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> getCurrentlyShowingMovies() {
        return movieRepository.findCurrentlyShowingMovies().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> getUpcomingMovies() {
        return movieRepository.findUpcomingMovies().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<MovieResponse> searchMovies(String name, String director,
                                            Movie.MovieStatus status, Long categoryId,
                                            Pageable pageable) {
        return movieRepository.searchMovies(name, director, status, categoryId, pageable)
                .map(this::convertToResponse);
    }

    @Transactional
    public void updateMovieStatus(Long id, Movie.MovieStatus status) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));
        movie.setMovieStatus(status);
        movieRepository.save(movie);
    }

    private MovieResponse convertToResponse(Movie movie) {
        return MovieResponse.builder()
                .movieId(movie.getMovieId())
                .movieName(movie.getMovieName())
                .originalName(movie.getOriginalName())
                .director(movie.getDirector())
                .cast(movie.getCast())
                .duration(movie.getDuration())
                .description(movie.getDescription())
                .releaseDate(movie.getReleaseDate())
                .endDate(movie.getEndDate())
                .rating(movie.getRating())
                .language(movie.getLanguage())
                .subtitle(movie.getSubtitle())
                .posterUrl(movie.getPosterUrl())
                .trailerUrl(movie.getTrailerUrl())
                .categoryId(movie.getCategory().getCategoryId())
                .categoryName(movie.getCategory().getCategoryName())
                .movieStatus(movie.getMovieStatus())
                .build();
    }
}