package org.example._citizenproj2.exception;

import org.springframework.http.HttpStatus;

public class MovieNotFoundException extends CustomException {

    public MovieNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message, "MOVIE_NOT_FOUND");
    }

    public MovieNotFoundException(Long movieId) {
        super(HttpStatus.NOT_FOUND,
                String.format("找不到ID為 %d 的電影", movieId),
                "MOVIE_NOT_FOUND");
    }

    public MovieNotFoundException(String movieName, String message) {
        super(HttpStatus.NOT_FOUND,
                String.format("電影 '%s': %s", movieName, message),
                "MOVIE_NOT_FOUND");
    }

    public static class MovieShowingNotFoundException extends MovieNotFoundException {
        public MovieShowingNotFoundException(Long movieId) {
            super(String.format("電影ID %d 目前沒有場次", movieId));
        }
    }

    public static class MovieNotAvailableException extends MovieNotFoundException {
        public MovieNotAvailableException(Long movieId) {
            super(String.format("電影ID %d 目前未上映", movieId));
        }
    }
}