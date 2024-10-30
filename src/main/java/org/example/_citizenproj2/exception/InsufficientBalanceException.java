package org.example._citizenproj2.exception;

import org.springframework.http.HttpStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class InsufficientBalanceException extends RuntimeException {

    private final BigDecimal currentBalance;
    private final BigDecimal requiredAmount;
    private final String transactionType;
    private final HttpStatus status;
    private final String errorCode;

    public InsufficientBalanceException(String message, BigDecimal currentBalance, BigDecimal requiredAmount) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.errorCode = "INSUFFICIENT_BALANCE";
        this.currentBalance = currentBalance;
        this.requiredAmount = requiredAmount;
        this.transactionType = "GENERAL";
    }

    public InsufficientBalanceException(String message, BigDecimal currentBalance, BigDecimal requiredAmount, String transactionType) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.errorCode = "INSUFFICIENT_BALANCE";
        this.currentBalance = currentBalance;
        this.requiredAmount = requiredAmount;
        this.transactionType = transactionType;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public BigDecimal getRequiredAmount() {
        return requiredAmount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getDeficit() {
        return requiredAmount.subtract(currentBalance);
    }

    // 預定義的錯誤類型
    public static InsufficientBalanceException forPayment(BigDecimal balance, BigDecimal amount) {
        return new InsufficientBalanceException(
                String.format("支付失敗，餘額不足。當前餘額: %s, 需要金額: %s",
                        balance.toString(), amount.toString()),
                balance,
                amount,
                "PAYMENT"
        );
    }

    public static InsufficientBalanceException forWithdrawal(BigDecimal balance, BigDecimal amount) {
        return new InsufficientBalanceException(
                String.format("提款失敗，餘額不足。當前餘額: %s",
                        balance.toString()),
                balance,
                amount,
                "WITHDRAWAL"
        );
    }

    public static InsufficientBalanceException forTransfer(BigDecimal balance, BigDecimal amount) {
        return new InsufficientBalanceException(
                String.format("轉帳失敗，餘額不足。當前餘額: %s",
                        balance.toString()),
                balance,
                amount,
                "TRANSFER"
        );
    }

    public static InsufficientBalanceException forBooking(BigDecimal balance, BigDecimal amount) {
        return new InsufficientBalanceException(
                String.format("訂票失敗，餘額不足。當前餘額: %s",
                        balance.toString()),
                balance,
                amount,
                "BOOKING"
        );
    }

    // 獲取本地化錯誤訊息
    @Override
    public String getLocalizedMessage() {
        return String.format("%s：餘額不足 %s 元",
                getTransactionTypeDisplay(),
                getDeficit().setScale(2, RoundingMode.UP));
    }

    private String getTransactionTypeDisplay() {
        switch (transactionType) {
            case "PAYMENT": return "付款失敗";
            case "WITHDRAWAL": return "提款失敗";
            case "TRANSFER": return "轉帳失敗";
            case "BOOKING": return "訂票失敗";
            default: return "交易失敗";
        }
    }

    // 獲取錯誤詳情
    public String getErrorDetails() {
        return String.format("交易類型: %s, 當前餘額: %s, 需要金額: %s, 不足金額: %s",
                getTransactionTypeDisplay(),
                currentBalance.setScale(2, RoundingMode.DOWN),
                requiredAmount.setScale(2, RoundingMode.DOWN),
                getDeficit().setScale(2, RoundingMode.UP));
    }

    // 檢查是否需要自動儲值
    public boolean needsAutoTopUp() {
        return getDeficit().compareTo(new BigDecimal("1000")) <= 0;
    }

    // 獲取建議儲值金額
    public BigDecimal getSuggestedTopUpAmount() {
        BigDecimal deficit = getDeficit();
        // 建議儲值金額為不足金額的1.2倍，並無條件進位到百位
        BigDecimal suggested = deficit.multiply(new BigDecimal("1.2"))
                .setScale(-2, RoundingMode.UP);
        return suggested.max(new BigDecimal("1000")); // 最低儲值1000元
    }
}