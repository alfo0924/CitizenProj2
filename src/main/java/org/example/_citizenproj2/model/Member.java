package org.example._citizenproj2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "members")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(length = 50)
    private String firstName;

    @Column(length = 50)
    private String lastName;

    private Date birthday;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(columnDefinition = "TEXT")
    private String address;

    private Boolean isVerified = false;
    private Boolean isActive = true;

    private Integer loginAttempts = 0;
    private LocalDateTime lastFailedLogin;
    private LocalDateTime lastPasswordChange;

    private String passwordResetToken;
    private LocalDateTime passwordResetExpiry;

    @CreationTimestamp
    private LocalDateTime registerDate;

    private LocalDateTime lastLoginTime;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // 關聯關係
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private CitizenCard citizenCard;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Wallet wallet;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<VirtualCard> virtualCards = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();

    // 枚舉定義
    public enum Role {
        USER, ADMIN, STAFF
    }

    public enum Gender {
        M, F, OTHER
    }

    // 業務方法
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " +
                (lastName != null ? lastName : "").trim();
    }
    public boolean isActive() {
        return Boolean.TRUE.equals(this.getIsActive());
    }
    public boolean isAccountNonLocked() {
        return isActive;
    }

    public boolean isAccountVerified() {
        return isVerified;
    }

    public boolean canBook() {
        return isActive && isVerified;
    }

    public boolean needsPasswordChange() {
        if (lastPasswordChange == null) {
            return true;
        }
        return lastPasswordChange.plusMonths(3).isBefore(LocalDateTime.now());
    }

    public boolean isPasswordResetTokenValid() {
        if (passwordResetToken == null || passwordResetExpiry == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(passwordResetExpiry);
    }

    public void incrementLoginAttempts() {
        this.loginAttempts = (this.loginAttempts == null ? 0 : this.loginAttempts) + 1;
        this.lastFailedLogin = LocalDateTime.now();
    }

    public void resetLoginAttempts() {
        this.loginAttempts = 0;
        this.lastFailedLogin = null;
    }

    public boolean isAccountLocked() {
        if (loginAttempts == null || loginAttempts < 5) {
            return false;
        }
        if (lastFailedLogin == null) {
            return false;
        }
        return lastFailedLogin.plusHours(1).isAfter(LocalDateTime.now());
    }

    public void generatePasswordResetToken() {
        this.passwordResetToken = java.util.UUID.randomUUID().toString();
        this.passwordResetExpiry = LocalDateTime.now().plusHours(24);
    }

    public void clearPasswordResetToken() {
        this.passwordResetToken = null;
        this.passwordResetExpiry = null;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
        this.lastPasswordChange = LocalDateTime.now();
        clearPasswordResetToken();
        resetLoginAttempts();
    }

    // 預處理方法
    @PrePersist
    public void prePersist() {
        if (registerDate == null) {
            registerDate = LocalDateTime.now();
        }
        if (role == null) {
            role = Role.USER;
        }
        if (loginAttempts == null) {
            loginAttempts = 0;
        }
        if (isActive == null) {
            isActive = true;
        }
        if (isVerified == null) {
            isVerified = false;
        }
        lastPasswordChange = LocalDateTime.now();
    }
}