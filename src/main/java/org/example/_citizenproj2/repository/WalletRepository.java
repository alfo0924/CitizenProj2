package org.example._citizenproj2.repository;

import org.example._citizenproj2.model.Transaction;
import org.example._citizenproj2.model.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // 基本查詢方法
    @Query("SELECT w FROM Wallet w WHERE w.member.memberId = :memberId")
    Optional<Wallet> findByMember_MemberId(@Param("memberId") Long memberId);

    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM Wallet w WHERE w.member.memberId = :memberId")
    boolean existsByMember_MemberId(@Param("memberId") Long memberId);

    // 餘額查詢
    @Query("SELECT w.balance FROM Wallet w WHERE w.walletId = :walletId")
    Optional<BigDecimal> findBalanceById(@Param("walletId") Long walletId);

    // 交易統計
    @Query("SELECT new map(" +
            "t.transactionType as type, " +
            "COUNT(t) as count, " +
            "SUM(t.amount) as totalAmount) " +
            "FROM Wallet w JOIN w.transactions t " +
            "WHERE w.walletId = :walletId " +
            "AND t.status = 'COMPLETED' " +
            "GROUP BY t.transactionType")
    List<Map<String, Object>> getTransactionStatistics(@Param("walletId") Long walletId);

    // 活躍錢包查詢
    @Query("SELECT w FROM Wallet w " +
            "WHERE w.lastTransactionTime >= :since " +
            "AND w.walletStatus = 'ACTIVE'")
    List<Wallet> findActiveWallets(@Param("since") LocalDateTime since);

    // 餘額檢查
    @Query("SELECT CASE WHEN w.balance >= :amount THEN true ELSE false END " +
            "FROM Wallet w WHERE w.walletId = :walletId")
    boolean hasEnoughBalance(@Param("walletId") Long walletId,
                             @Param("amount") BigDecimal amount);

    // 錢包統計
    @Query("SELECT new map(" +
            "w.member.memberId as memberId, " +
            "SUM(CASE WHEN t.transactionType = 'DEPOSIT' THEN t.amount ELSE 0 END) as totalDeposit, " +
            "SUM(CASE WHEN t.transactionType = 'WITHDRAWAL' THEN t.amount ELSE 0 END) as totalWithdrawal) " +
            "FROM Wallet w LEFT JOIN w.transactions t " +
            "WHERE w.walletId = :walletId " +
            "GROUP BY w.member.memberId")
    Map<String, Object> getWalletStatistics(@Param("walletId") Long walletId);

    // 交易歷史查詢
    @Query("SELECT t FROM Transaction t " +
            "WHERE t.wallet.walletId = :walletId " +
            "AND t.transactionTime BETWEEN :startTime AND :endTime " +
            "ORDER BY t.transactionTime DESC")
    Page<Transaction> findTransactionHistory(
            @Param("walletId") Long walletId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    boolean existsByMemberMemberId(Long memberId);

    Optional<Object> findByMemberMemberId(Long memberId);
}