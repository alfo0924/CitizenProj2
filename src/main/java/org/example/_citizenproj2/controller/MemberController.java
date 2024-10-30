package org.example._citizenproj2.controller;

import lombok.RequiredArgsConstructor;
import org.example._citizenproj2.dto.request.MemberRequest;
import org.example._citizenproj2.dto.response.MemberResponse;
import org.example._citizenproj2.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

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
    public ResponseEntity<MemberResponse> loginMember(@Valid @RequestBody MemberRequest.Login request) {
        return ResponseEntity.ok(memberService.loginMember(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequest request) {
        return ResponseEntity.ok(memberService.updateMember(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/verify-email")
    public ResponseEntity<Void> verifyEmail(@PathVariable Long id, @RequestParam String token) {
        memberService.verifyEmail(id, token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password/request")
    public ResponseEntity<Void> requestPasswordReset(@RequestBody MemberRequest.PasswordReset request) {
        memberService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<Void> confirmPasswordReset(@Valid @RequestBody MemberRequest.PasswordResetConfirm request) {
        memberService.confirmPasswordReset(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<MemberResponse> getProfile() {
        return ResponseEntity.ok(memberService.getCurrentMemberProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<MemberResponse> updateProfile(@Valid @RequestBody MemberRequest request) {
        return ResponseEntity.ok(memberService.updateCurrentMemberProfile(request));
    }
}