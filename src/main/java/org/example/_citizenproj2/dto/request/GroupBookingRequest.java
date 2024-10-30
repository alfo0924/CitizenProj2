package org.example._citizenproj2.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBookingRequest {

    @NotNull(message = "場次ID不能為空")
    private Long showingId;

    @NotNull(message = "團體領導人ID不能為空")
    private Long organizerId;

    @Min(value = 2, message = "最小人數不能少於2人")
    private Integer minMembers;

    @Max(value = 20, message = "最大人數不能超過20人")
    private Integer maxMembers;

    @NotNull(message = "截止時間不能為空")
    private LocalDateTime expiryTime;

    private List<Long> preferredSeatIds;
    private String note;
    private String paymentMethod;

    @Builder.Default
    private Boolean splitPayment = false;

    private List<GroupMemberRequest> members;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupMemberRequest {
        private Long memberId;
        private Long preferredSeatId;
        private String ticketType;

        @Builder.Default
        private Boolean isPaid = false;
    }
}