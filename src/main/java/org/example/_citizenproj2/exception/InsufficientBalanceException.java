package org.example._citizenproj2.exception;

import org.springframework.http.HttpStatus;
import java.math.BigDecimal;

public class InsufficientBalanceException extends CustomException {

    private final BigDecimal currentBalance;
    private final BigDecimal requiredAmount;

    public InsufficientBalanceException(String message, BigDecimal currentBalance, BigDecimal requiredAmount) {
        super(HttpStatus.BAD_REQUEST, message, "INSUFFICIENT_BALANCE");
        this.currentBalance = currentBalance;
        this.requiredAmount = requiredAmount;
    }

    public InsufficientBalanceException(BigDecimal currentBalance, BigDecimal requiredAmount) {
        this(String.format("餘額不足。當前餘額: %s, 需要金額: %s",
                        currentBalance.toString(), requiredAmount.toString()),
                currentBalance, requiredAmount);
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public BigDecimal getRequiredAmount() {
        return requiredAmount;
    }

    public BigDecimal getDeficit() {
        return requiredAmount.subtract(currentBalance);
    }

    // 預定義的錯誤類型
    public static InsufficientBalanceException forPayment(BigDecimal balance, BigDecimal amount) {
        return new InsufficientBalanceException(
                String.format("支付失敗，餘額不足。需要充值: %s",
                        amount.subtract(balance).toString()),
                balance,
                amount
        );
    }

    public static InsufficientBalanceException forWithdrawal(BigDecimal balance, BigDecimal amount) {
        return new InsufficientBalanceException(
                String.format("提款失敗，餘額不足。當前餘額: %s", balance.toString()),
                balance,
                amount
        );
    }
}