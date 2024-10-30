package org.example._citizenproj2.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    @NotNull(message = "金額不能為空")
    @DecimalMin(value = "0.01", message = "金額必須大於0")
    private BigDecimal amount;

    @Size(max = 50, message = "支付方式長度不能超過50")
    private String paymentMethod;

    @Size(max = 500, message = "描述長度不能超過500")
    private String description;

    private String referenceId;

    @Size(min = 6, max = 6, message = "驗證碼必須是6位")
    private String verificationCode;

    @Builder.Default
    private Boolean needVerification = false;

    // 用於大額交易
    private String verificationMethod;

    // 交易相關資訊
    private String deviceInfo;
    private String ipAddress;

    // 驗證方法
    public void validateTransaction() {
        if (amount.compareTo(new BigDecimal("50000")) > 0 && verificationCode == null) {
            throw new IllegalArgumentException("大額交易需要驗證碼");
        }
    }

    // 檢查是否需要驗證
    public boolean needsVerification() {
        return amount.compareTo(new BigDecimal("10000")) > 0 || Boolean.TRUE.equals(needVerification);
    }

    // 取得交易來源資訊
    public String getTransactionSource() {
        return String.format("IP: %s, Device: %s", ipAddress, deviceInfo);
    }
}