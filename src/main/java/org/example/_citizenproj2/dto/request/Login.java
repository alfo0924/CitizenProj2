package org.example._citizenproj2.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Login {

    @NotBlank(message = "帳號不能為空")
    private String username;

    @NotBlank(message = "密碼不能為空")
    private String password;

    @Builder.Default
    private Boolean rememberMe = false;

    private String verificationCode;
    private String deviceInfo;
    private String ipAddress;
    private String userAgent;
}