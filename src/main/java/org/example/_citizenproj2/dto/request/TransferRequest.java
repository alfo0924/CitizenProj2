package org.example._citizenproj2.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @NotNull(message = "收款方ID不能為空")
    private Long receiverMemberId;

    @NotNull(message = "轉帳金額不能為空")
    @DecimalMin(value = "0.01", message = "轉帳金額必須大於0")
    @DecimalMax(value = "100000.00", message = "單筆轉帳不能超過100000")
    private BigDecimal amount;

    private String note;
    private String verificationCode;
    private String deviceInfo;
    private String ipAddress;

    @Builder.Default
    private TransferType transferType = TransferType.NORMAL;

    @Builder.Default
    private Boolean isRecurring = false;

    private String scheduledTime;
    private String recurringPattern;

    public enum TransferType {
        NORMAL,    // 一般轉帳
        EXPRESS,   // 即時轉帳
        SCHEDULED  // 預約轉帳
    }
}