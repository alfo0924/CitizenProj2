package org.example._citizenproj2.repository;

import org.example._citizenproj2.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 基本查詢方法
    Optional<Member> findByEmail(String email);

    Optional<Member> findByPhone(String phone);

    Optional<Member> findByPasswordResetToken(String token);
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<Member> findByIsActiveTrue();

    // 複合查詢
    @Query("SELECT m FROM Member m WHERE m.isActive = true AND m.isVerified = true")
    List<Member> findActiveAndVerifiedMembers();

    // 自定義查詢
    @Query("SELECT m FROM Member m WHERE m.lastLoginTime > :date")
    List<Member> findRecentlyActiveMembers(@Param("date") LocalDateTime date);

    // 更新操作
    @Modifying
    @Query("UPDATE Member m SET m.isActive = :status WHERE m.memberId = :id")
    int updateMemberStatus(@Param("id") Long id, @Param("status") boolean status);

    @Modifying
    @Query("UPDATE Member m SET m.lastLoginTime = CURRENT_TIMESTAMP WHERE m.memberId = :id")
    void updateLastLoginTime(@Param("id") Long id);

    // 統計查詢
    @Query("SELECT COUNT(m) FROM Member m WHERE m.registerDate >= :startDate")
    long countNewMembers(@Param("startDate") LocalDateTime startDate);

    // 分頁和排序查詢
    List<Member> findByRoleOrderByRegisterDateDesc(Member.Role role);

    // 複雜條件查詢
    @Query("SELECT m FROM Member m WHERE " +
            "(:email IS NULL OR m.email LIKE %:email%) AND " +
            "(:phone IS NULL OR m.phone LIKE %:phone%) AND " +
            "(:role IS NULL OR m.role = :role)")
    List<Member> searchMembers(
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("role") Member.Role role);

    // 會員等級相關查詢
    @Query(value = "SELECT m.* FROM members m " +
            "JOIN member_levels ml ON m.level_id = ml.level_id " +
            "WHERE ml.minimum_points <= :points", nativeQuery = true)
    List<Member> findMembersEligibleForUpgrade(@Param("points") int points);

    // 安全相關更新
    @Modifying
    @Query("UPDATE Member m SET m.loginAttempts = :attempts WHERE m.memberId = :id")
    void updateLoginAttempts(@Param("id") Long id, @Param("attempts") int attempts);

    @Modifying
    @Query("UPDATE Member m SET m.password = :newPassword WHERE m.memberId = :id")
    void updatePassword(@Param("id") Long id, @Param("newPassword") String newPassword);
}