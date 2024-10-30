package org.example._citizenproj2.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class SeatNotAvailableException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public SeatNotAvailableException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.errorCode = "SEAT_NOT_AVAILABLE";
    }

    public SeatNotAvailableException(String message, String errorCode) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
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

    public static SeatNotAvailableException seatNotFound(String seatNumber) {
        return new SeatNotAvailableException(
                String.format("找不到座位 %s", seatNumber),
                "SEAT_NOT_FOUND"
        );
    }

    public static SeatNotAvailableException invalidVenue(String seatNumber, String venueName) {
        return new SeatNotAvailableException(
                String.format("座位 %s 不屬於影廳 %s", seatNumber, venueName),
                "INVALID_VENUE"
        );
    }

    public static SeatNotAvailableException multipleSeatsNotAvailable(List<String> seatNumbers) {
        return new SeatNotAvailableException(
                String.format("以下座位不可用: %s", String.join(", ", seatNumbers)),
                "MULTIPLE_SEATS_NOT_AVAILABLE"
        );
    }

    // 取得完整錯誤訊息
    @Override
    public String getMessage() {
        return String.format("%s (錯誤代碼: %s)", super.getMessage(), errorCode);
    }

    // 檢查是否為特定錯誤類型
    public boolean isErrorCode(String code) {
        return errorCode.equals(code);
    }

    // 檢查是否為暫時性錯誤
    public boolean isTemporary() {
        return "SEAT_RESERVED".equals(errorCode);
    }
}