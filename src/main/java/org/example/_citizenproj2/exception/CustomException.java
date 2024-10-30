package org.example._citizenproj2.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final HttpStatus status;
    private final String message;
    private final String errorCode;

    public CustomException(String message) {
        this(HttpStatus.BAD_REQUEST, message, null);
    }

    public CustomException(HttpStatus status, String message) {
        this(status, message, null);
    }

    public CustomException(HttpStatus status, String message, String errorCode) {
        super(message);
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
    }

    // 預定義的異常
    public static class MemberNotFoundException extends CustomException {
        public MemberNotFoundException(String message) {
            super(HttpStatus.NOT_FOUND, message, "MEMBER_NOT_FOUND");
        }
    }

    public static class MovieNotFoundException extends CustomException {
        public MovieNotFoundException(String message) {
            super(HttpStatus.NOT_FOUND, message, "MOVIE_NOT_FOUND");
        }
    }

    public static class BookingNotFoundException extends CustomException {
        public BookingNotFoundException(String message) {
            super(HttpStatus.NOT_FOUND, message, "BOOKING_NOT_FOUND");
        }
    }

    public static class SeatNotAvailableException extends CustomException {
        public SeatNotAvailableException(String message) {
            super(HttpStatus.BAD_REQUEST, message, "SEAT_NOT_AVAILABLE");
        }
    }

    public static class InsufficientBalanceException extends CustomException {
        public InsufficientBalanceException(String message) {
            super(HttpStatus.BAD_REQUEST, message, "INSUFFICIENT_BALANCE");
        }
    }

    public static class InvalidOperationException extends CustomException {
        public InvalidOperationException(String message) {
            super(HttpStatus.BAD_REQUEST, message, "INVALID_OPERATION");
        }
    }

    public static class AuthenticationException extends CustomException {
        public AuthenticationException(String message) {
            super(HttpStatus.UNAUTHORIZED, message, "AUTHENTICATION_FAILED");
        }
    }

    public static class AuthorizationException extends CustomException {
        public AuthorizationException(String message) {
            super(HttpStatus.FORBIDDEN, message, "AUTHORIZATION_FAILED");
        }
    }
}