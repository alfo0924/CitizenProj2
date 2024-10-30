package org.example._citizenproj2.service;

import lombok.RequiredArgsConstructor;
import org.example._citizenproj2.dto.request.MemberRequest;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponse createMember(MemberRequest request) {
        // 檢查 email 是否已存在
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // 檢查手機號碼是否已存在
        if (memberRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

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

        Member savedMember = memberRepository.save(member);
        return convertToResponse(savedMember);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + id));
        return convertToResponse(member);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with email: " + email));
        return convertToResponse(member);
    }

    @Transactional
    public MemberResponse updateMember(Long id, MemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + id));

        // 更新基本資料
        member.setPhone(request.getPhone());
        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setBirthday(request.getBirthday());
        member.setGender(request.getGender());
        member.setAddress(request.getAddress());

        Member updatedMember = memberRepository.save(member);
        return convertToResponse(updatedMember);
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + id));
        member.setIsActive(false);
        memberRepository.save(member);
    }

    @Transactional
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + id));

        if (!passwordEncoder.matches(oldPassword, member.getPassword())) {
            throw new IllegalArgumentException("Invalid old password");
        }

        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Page<MemberResponse> getAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    @Transactional
    public void updateLoginTime(Long id) {
        memberRepository.findById(id).ifPresent(member -> {
            member.setLastLoginTime(LocalDateTime.now());
            memberRepository.save(member);
        });
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