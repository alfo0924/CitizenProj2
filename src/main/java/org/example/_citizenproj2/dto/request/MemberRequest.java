package org.example._citizenproj2.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example._citizenproj2.model.Member;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequest {

    @NotBlank(message = "Email不能為空")
    @Email(message = "Email格式不正確")
    private String email;

    @NotBlank(message = "密碼不能為空")
    @Size(min = 6, max = 20, message = "密碼長度必須在6-20之間")
    private String password;

    @NotBlank(message = "手機號碼不能為空")
    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式不正確")
    private String phone;

    @Size(max = 50, message = "名字長度不能超過50")
    private String firstName;

    @Size(max = 50, message = "姓氏長度不能超過50")
    private String lastName;

    @Past(message = "生日必須是過去的日期")
    private LocalDate birthday;  // 改用 LocalDate

    @NotNull(message = "性別不能為空")
    private Member.Gender gender;

    private String address;

    // 用於更新密碼
    private String oldPassword;
    private String newPassword;

    // 用於驗證
    private String verificationCode;

    // 用於更新會員狀態
    private Boolean isActive;

    // 用於角色管理
    private Member.Role role;

    // 驗證方法
    public void validatePasswordUpdate() {
        if (oldPassword == null || newPassword == null) {
            throw new IllegalArgumentException("舊密碼和新密碼都不能為空");
        }
        if (oldPassword.equals(newPassword)) {
            throw new IllegalArgumentException("新密碼不能與舊密碼相同");
        }
        if (newPassword.length() < 6 || newPassword.length() > 20) {
            throw new IllegalArgumentException("新密碼長度必須在6-20之間");
        }
    }

    public class Login {
    }
}