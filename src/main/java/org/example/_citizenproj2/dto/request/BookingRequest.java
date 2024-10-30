package org.example._citizenproj2.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotNull(message = "會員ID不能為空")
    private Long memberId;

    @NotNull(message = "場次ID不能為空")
    private Long showingId;

    @NotEmpty(message = "座位不能為空")
    private List<Long> seatIds;

    @NotEmpty(message = "票種不能為空")
    private List<String> ticketTypes;

    private String discountCode;

    private String paymentMethod;

    // 用於團體訂票
    private Boolean isGroupBooking;
    private Integer minMembers;
    private Integer maxMembers;
    private Date expiryTime;

    // 用於候補訂票
    private Boolean isWaitingList;
    private String preferredSeatType;
    private String notificationPreference;
    private Integer requestedSeats;

    // 驗證方法
    public void validateGroupBooking() {
        if (Boolean.TRUE.equals(isGroupBooking)) {
            if (minMembers == null || maxMembers == null) {
                throw new IllegalArgumentException("團體訂票必須指定最小和最大人數");
            }
            if (minMembers < 2) {
                throw new IllegalArgumentException("團體訂票最少需要2人");
            }
            if (maxMembers < minMembers) {
                throw new IllegalArgumentException("最大人數不能小於最小人數");
            }
            if (expiryTime == null) {
                throw new IllegalArgumentException("團體訂票必須指定截止時間");
            }
        }
    }

    public void validateWaitingList() {
        if (Boolean.TRUE.equals(isWaitingList)) {
            if (requestedSeats == null || requestedSeats < 1) {
                throw new IllegalArgumentException("候補訂票必須指定所需座位數");
            }
            if (notificationPreference == null) {
                throw new IllegalArgumentException("候補訂票必須指定通知方式");
            }
        }
    }
}