package org.example._citizenproj2.exception;

import org.springframework.http.HttpStatus;

public class BookingException extends CustomException {

    public BookingException(String message) {
        super(HttpStatus.BAD_REQUEST, message, "BOOKING_ERROR");
    }

    public BookingException(String message, String errorCode) {
        super(HttpStatus.BAD_REQUEST, message, errorCode);
    }

    // 預定義的錯誤類型
    public static BookingException showingNotFound() {
        return new BookingException("場次不存在", "SHOWING_NOT_FOUND");
    }

    public static BookingException invalidSeats() {
        return new BookingException("座位選擇無效", "INVALID_SEATS");
    }

    public static BookingException bookingExpired() {
        return new BookingException("訂單已過期", "BOOKING_EXPIRED");
    }

    public static BookingException cannotCancel() {
        return new BookingException("無法取消訂單", "CANNOT_CANCEL");
    }

    public static BookingException showingFull() {
        return new BookingException("場次已滿", "SHOWING_FULL");
    }

    public static BookingException invalidPaymentStatus() {
        return new BookingException("支付狀態無效", "INVALID_PAYMENT_STATUS");
    }

    public static BookingException duplicateBooking() {
        return new BookingException("重複訂票", "DUPLICATE_BOOKING");
    }
}