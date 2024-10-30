package org.example._citizenproj2.service;

import lombok.RequiredArgsConstructor;
import org.example._citizenproj2.dto.request.Login;
import org.example._citizenproj2.dto.request.MemberRequest;
import org.example._citizenproj2.dto.request.PasswordReset;
import org.example._citizenproj2.dto.request.PasswordResetConfirm;
import org.example._citizenproj2.dto.response.MemberResponse;
import org.example._citizenproj2.exception.MemberNotFoundException;
import org.example._citizenproj2.model.Member;

import org.example._citizenproj2.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletService walletService;
    private final EmailService emailService;
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int PASSWORD_RESET_EXPIRY_HOURS = 24;

    @Transactional
    public MemberResponse login(Login request) {
        Member member = memberRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new MemberNotFoundException("會員不存在"));

        // 檢查帳號狀態
        if (!member.isActive()) {
            throw new IllegalStateException("帳號已被停用");
        }

        // 檢查登入嘗試次數
        if (member.getLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
            if (member.getLastFailedLogin().plusHours(1).isAfter(LocalDateTime.now())) {
                throw new IllegalStateException("登入嘗試次數過多，請稍後再試");
            } else {
                member.setLoginAttempts(0);
            }
        }

        // 驗證密碼
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            member.setLoginAttempts(member.getLoginAttempts() + 1);
            member.setLastFailedLogin(LocalDateTime.now());
            memberRepository.save(member);
            throw new IllegalArgumentException("密碼錯誤");
        }

        // 重置登入嘗試次數並更新登入時間
        member.setLoginAttempts(0);
        member.setLastFailedLogin(null);
        member.setLastLoginTime(LocalDateTime.now());
        memberRepository.save(member);

        return convertToResponse(member);
    }

    @Transactional
    public void requestPasswordReset(PasswordReset request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new MemberNotFoundException("會員不存在"));

        String resetToken = UUID.randomUUID().toString();
        member.setPasswordResetToken(resetToken);
        member.setPasswordResetExpiry(LocalDateTime.now().plusHours(PASSWORD_RESET_EXPIRY_HOURS));
        memberRepository.save(member);

        // 發送重設密碼郵件
        emailService.sendPasswordResetEmail(member.getEmail(), resetToken);
    }

    @Transactional
    public void confirmPasswordReset(PasswordResetConfirm request) {
        Member member = memberRepository.findByPasswordResetToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("無效的重設token"));

        if (member.getPasswordResetExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("重設token已過期");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("密碼確認不符");
        }

        member.setPassword(passwordEncoder.encode(request.getNewPassword()));
        member.setPasswordResetToken(null);
        member.setPasswordResetExpiry(null);
        member.setLastPasswordChange(LocalDateTime.now());
        memberRepository.save(member);
    }

    @Transactional
    public MemberResponse createMember(MemberRequest request) {
        // 驗證Email和手機是否已存在
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email已被使用");
        }
        if (memberRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("手機號碼已被使用");
        }

        // 創建新會員
        Member member = new Member();
        member.setEmail(request.getEmail());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setPhone(request.getPhone());
        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setBirthday(request.getBirthday());
        member.setGender(request.getGender());
        member.setAddress(request.getAddress());
        member.setRole(Member.Role.USER);
        member.setIsActive(true);
        member.setRegisterDate(LocalDateTime.now());
        member.setLastPasswordChange(LocalDateTime.now());

        Member savedMember = memberRepository.save(member);

        // 創建電子錢包
        walletService.createWallet(savedMember.getMemberId());

        // 發送歡迎郵件
        emailService.sendWelcomeEmail(savedMember.getEmail());

        return convertToResponse(savedMember);
    }

    public MemberResponse getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("會員不存在"));
        return convertToResponse(member);
    }

    @Transactional
    public MemberResponse updateMember(Long id, MemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("會員不存在"));

        // 驗證手機號碼是否被其他會員使用
        if (!member.getPhone().equals(request.getPhone()) &&
                memberRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("手機號碼已被使用");
        }

        member.setPhone(request.getPhone());
        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setBirthday(request.getBirthday());
        member.setGender(request.getGender());
        member.setAddress(request.getAddress());

        return convertToResponse(memberRepository.save(member));
    }

    @Transactional
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("會員不存在"));

        if (!passwordEncoder.matches(oldPassword, member.getPassword())) {
            throw new IllegalArgumentException("舊密碼錯誤");
        }

        member.setPassword(passwordEncoder.encode(newPassword));
        member.setLastPasswordChange(LocalDateTime.now());
        memberRepository.save(member);
    }

    @Transactional
    public void deactivateMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("會員不存在"));
        member.setIsActive(false);
        memberRepository.save(member);
    }

    @Transactional
    public void reactivateMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("會員不存在"));
        member.setIsActive(true);
        member.setLoginAttempts(0);
        member.setLastFailedLogin(null);
        memberRepository.save(member);
    }

    public Page<MemberResponse> getAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    private MemberResponse convertToResponse(Member member) {
        return MemberResponse.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .phone(member.getPhone())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .birthday(member.getBirthday())
                .gender(member.getGender())
                .address(member.getAddress())
                .role(member.getRole())
                .isVerified(member.getIsVerified())
                .isActive(member.getIsActive())
                .registerDate(member.getRegisterDate())
                .lastLoginTime(member.getLastLoginTime())
                .build();
    }
}