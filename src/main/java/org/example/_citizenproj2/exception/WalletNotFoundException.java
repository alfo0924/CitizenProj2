package org.example._citizenproj2.exception;

import org.springframework.http.HttpStatus;

public class WalletNotFoundException extends CustomException {

    public WalletNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message, "WALLET_NOT_FOUND");
    }

    public WalletNotFoundException(Long walletId) {
        super(HttpStatus.NOT_FOUND,
                String.format("找不到ID為 %d 的錢包", walletId),
                "WALLET_NOT_FOUND");
    }

    public WalletNotFoundException(Long memberId, String message) {
        super(HttpStatus.NOT_FOUND,
                String.format("會員ID %d: %s", memberId, message),
                "WALLET_NOT_FOUND");
    }
}