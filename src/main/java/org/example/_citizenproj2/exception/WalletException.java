package org.example._citizenproj2.exception;

import org.springframework.http.HttpStatus;

public class WalletException extends CustomException {

    public WalletException(String message) {
        super(HttpStatus.BAD_REQUEST, message, "WALLET_ERROR");
    }

    public WalletException(String message, String errorCode) {
        super(HttpStatus.BAD_REQUEST, message, errorCode);
    }

    public WalletException(HttpStatus status, String message, String errorCode) {
        super(status, message, errorCode);
    }

    // 預定義的錯誤類型
    public static WalletException walletNotFound() {
        return new WalletException(
                HttpStatus.NOT_FOUND,
                "錢包不存在",
                "WALLET_NOT_FOUND"
        );
    }

    public static WalletException walletLocked() {
        return new WalletException(
                "錢包已被鎖定",
                "WALLET_LOCKED"
        );
    }

    public static WalletException invalidStatus() {
        return new WalletException(
                "錢包狀態無效",
                "INVALID_WALLET_STATUS"
        );
    }

    public static WalletException exceedDailyLimit() {
        return new WalletException(
                "超過每日交易限額",
                "EXCEED_DAILY_LIMIT"
        );
    }

    public static WalletException invalidTransaction() {
        return new WalletException(
                "無效的交易",
                "INVALID_TRANSACTION"
        );
    }

    public static WalletException verificationRequired() {
        return new WalletException(
                "需要驗證",
                "VERIFICATION_REQUIRED"
        );
    }

    public static WalletException duplicateTransaction() {
        return new WalletException(
                "重複的交易",
                "DUPLICATE_TRANSACTION"
        );
    }
}