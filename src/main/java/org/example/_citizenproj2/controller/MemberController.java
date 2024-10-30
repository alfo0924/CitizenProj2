package org.example._citizenproj2.controller;

import lombok.RequiredArgsConstructor;
import org.example._citizenproj2.dto.request.Login;
import org.example._citizenproj2.dto.request.MemberRequest;
import org.example._citizenproj2.dto.request.PasswordReset;
import org.example._citizenproj2.dto.request.PasswordResetConfirm;
import org.example._citizenproj2.dto.response.MemberResponse;
import org.example._citizenproj2.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<MemberResponse> registerMember(@Valid @RequestBody MemberRequest request) {
        return new ResponseEntity<>(memberService.createMember(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<MemberResponse> login(@Valid @RequestBody Login request) {
        return ResponseEntity.ok(memberService.login(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMember(id));
    }

    @GetMapping
    public ResponseEntity<Page<MemberResponse>> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "memberId") String sort) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sort));
        return ResponseEntity.ok(memberService.getAllMembers(pageRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequest request) {
        return ResponseEntity.ok(memberService.updateMember(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateMember(@PathVariable Long id) {
        memberService.deactivateMember(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reactivate")
    public ResponseEntity<Void> reactivateMember(@PathVariable Long id) {
        memberService.reactivateMember(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<Void> requestPasswordReset(@Valid @RequestBody PasswordReset request) {
        memberService.requestPasswordReset(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<Void> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirm request) {
        memberService.confirmPasswordReset(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        memberService.updatePassword(id, oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    // 如果需要當前會員資料，需要透過 Spring Security 獲取
    @GetMapping("/current")
    public ResponseEntity<MemberResponse> getCurrentMember() {
        // 假設已經實作了獲取當前會員ID的方法
        Long currentMemberId = getCurrentMemberId();
        return ResponseEntity.ok(memberService.getMember(currentMemberId));
    }

    // 輔助方法 - 在實際應用中應該從 Spring Security 獲取
    private Long getCurrentMemberId() {
        // 實作獲取當前登入會員ID的邏輯
        throw new UnsupportedOperationException("需要實作獲取當前會員ID的方法");
    }
}