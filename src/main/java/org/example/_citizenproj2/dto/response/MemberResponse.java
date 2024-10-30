package org.example._citizenproj2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example._citizenproj2.model.Member;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private Long memberId;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private Date birthday;
    private Member.Gender gender;
    private String address;
    private Member.Role role;
    private Boolean isVerified;
    private Boolean isActive;
    private LocalDateTime registerDate;
    private LocalDateTime lastLoginTime;

    // 擴展資訊
    private WalletInfo walletInfo;
    private CardInfo cardInfo;
    private MembershipInfo membershipInfo;

    @Data
    @Builder
    public static class WalletInfo {
        private Long walletId;
        private Double balance;
        private String walletStatus;
        private LocalDateTime lastTransactionTime;
    }

    @Data
    @Builder
    public static class CardInfo {
        private String cardNumber;
        private String cardType;
        private String cardStatus;
        private Date expiryDate;
    }

    @Data
    @Builder
    public static class MembershipInfo {
        private String levelName;
        private Integer points;
        private Date levelExpiryDate;
        private Double discountRate;
    }
}