package org.example._citizenproj2.exception;

import org.springframework.http.HttpStatus;

public class SeatNotAvailableException extends CustomException {

    public SeatNotAvailableException(String message) {
        super(HttpStatus.BAD_REQUEST, message, "SEAT_NOT_AVAILABLE");
    }

    public SeatNotAvailableException(String message, String errorCode) {
        super(HttpStatus.BAD_REQUEST, message, errorCode);
    }

    // 預定義的錯誤類型
    public static SeatNotAvailableException seatOccupied(String seatNumber) {
        return new SeatNotAvailableException(
                String.format("座位 %s 已被預訂", seatNumber),
                "SEAT_OCCUPIED"
        );
    }

    public static SeatNotAvailableException seatMaintenance(String seatNumber) {
        return new SeatNotAvailableException(
                String.format("座位 %s 正在維護中", seatNumber),
                "SEAT_MAINTENANCE"
        );
    }

    public static SeatNotAvailableException invalidSeatType() {
        return new SeatNotAvailableException(
                "無效的座位類型",
                "INVALID_SEAT_TYPE"
        );
    }

    public static SeatNotAvailableException seatReserved(String seatNumber) {
        return new SeatNotAvailableException(
                String.format("座位 %s 已被暫時預留", seatNumber),
                "SEAT_RESERVED"
        );
    }
}