package org.example._citizenproj2.exception;

import org.springframework.http.HttpStatus;

public class MemberNotFoundException extends CustomException {

    public MemberNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message, "MEMBER_NOT_FOUND");
    }

    public MemberNotFoundException(Long memberId) {
        super(HttpStatus.NOT_FOUND,
                String.format("找不到ID為 %d 的會員", memberId),
                "MEMBER_NOT_FOUND");
    }

    public MemberNotFoundException(String email, String message) {
        super(HttpStatus.NOT_FOUND,
                String.format("會員 '%s': %s", email, message),
                "MEMBER_NOT_FOUND");
    }

    public static class MemberNotActiveException extends MemberNotFoundException {
        public MemberNotActiveException(Long memberId) {
            super(String.format("會員ID %d 帳號未啟用", memberId));
        }
    }

    public static class MemberNotVerifiedException extends MemberNotFoundException {
        public MemberNotVerifiedException(Long memberId) {
            super(String.format("會員ID %d 尚未驗證", memberId));
        }
    }

    public static class MemberBlockedException extends MemberNotFoundException {
        public MemberBlockedException(Long memberId, String reason) {
            super(String.format("會員ID %d 已被封鎖: %s", memberId, reason));
        }
    }

    // 用於處理特定業務場景的靜態工廠方法
    public static MemberNotFoundException notFoundByEmail(String email) {
        return new MemberNotFoundException(
                String.format("找不到使用此Email的會員: %s", email));
    }

    public static MemberNotFoundException notFoundByPhone(String phone) {
        return new MemberNotFoundException(
                String.format("找不到使用此手機號碼的會員: %s", phone));
    }

    public static MemberNotFoundException notFoundForBooking(Long memberId) {
        return new MemberNotFoundException(
                String.format("無法為會員ID %d 進行訂票，會員不存在", memberId));
    }

    public static MemberNotFoundException notFoundForWallet(Long memberId) {
        return new MemberNotFoundException(
                String.format("無法為會員ID %d 處理錢包操作，會員不存在", memberId));
    }
}