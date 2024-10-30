package org.example._citizenproj2.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordReset {

    @NotBlank(message = "Email不能為空")
    @Email(message = "Email格式不正確")
    private String email;

    private String phone;

    @Builder.Default
    private ResetMethod resetMethod = ResetMethod.EMAIL;

    private String deviceInfo;
    private String ipAddress;
    private String userAgent;

    public enum ResetMethod {
        EMAIL,      // 通過電子郵件重設
        SMS,        // 通過簡訊重設
        BOTH        // 同時使用電子郵件和簡訊
    }
}