package org.example._citizenproj2.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetConfirm {

    @NotBlank(message = "Token不能為空")
    private String token;

    @NotBlank(message = "新密碼不能為空")
    @Size(min = 8, max = 20, message = "密碼長度必須在8-20之間")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = "密碼必須包含數字、大小寫字母和特殊符號")
    private String newPassword;

    @NotBlank(message = "確認密碼不能為空")
    private String confirmPassword;

    private String verificationCode;
    private String deviceInfo;
    private String ipAddress;
    private String userAgent;
}