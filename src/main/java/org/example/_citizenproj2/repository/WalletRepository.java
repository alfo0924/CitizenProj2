package org.example._citizenproj2.repository;

import jakarta.transaction.Transaction;
import org.example._citizenproj2.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // 基本查詢
    Optional<Wallet> findByMemberMemberId(Long memberId);

    List<Wallet> findByWalletStatus(Wallet.WalletStatus status);

    // 餘額相關操作
    @Modifying
    @Query("UPDATE Wallet w SET w.balance = w.balance + :amount, " +
            "w.lastTransactionTime = CURRENT_TIMESTAMP " +
            "WHERE w.walletId = :walletId")
    int updateBalance(@Param("walletId") Long walletId, @Param("amount") BigDecimal amount);

    @Query("SELECT w.balance FROM Wallet w WHERE w.walletId = :walletId")
    Optional<BigDecimal> findBalanceById(@Param("walletId") Long walletId);

    // 交易統計
    @Query("SELECT new map(" +
            "w.member.memberId as memberId, " +
            "SUM(CASE WHEN t.transactionType = 'DEPOSIT' THEN t.amount ELSE 0 END) as totalDeposit, " +
            "SUM(CASE WHEN t.transactionType = 'WITHDRAWAL' THEN t.amount ELSE 0 END) as totalWithdrawal) " +
            "FROM Wallet w LEFT JOIN w.transactions t " +
            "WHERE w.walletId = :walletId " +
            "GROUP BY w.member.memberId")
    Map<String, Object> getWalletStatistics(@Param("walletId") Long walletId);

    // 狀態更新
    @Modifying
    @Query("UPDATE Wallet w SET w.walletStatus = :status " +
            "WHERE w.walletId = :walletId")
    int updateWalletStatus(@Param("walletId") Long walletId,
                           @Param("status") Wallet.WalletStatus status);

    // 交易記錄查詢
    @Query("SELECT t FROM Transaction t " +
            "WHERE t.wallet.walletId = :walletId " +
            "AND t.transactionTime BETWEEN :startTime AND :endTime " +
            "ORDER BY t.transactionTime DESC")
    List<Transaction> findTransactionHistory(
            @Param("walletId") Long walletId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    // 餘額檢查
    @Query("SELECT CASE WHEN w.balance >= :amount THEN true ELSE false END " +
            "FROM Wallet w WHERE w.walletId = :walletId")
    boolean hasEnoughBalance(@Param("walletId") Long walletId,
                             @Param("amount") BigDecimal amount);

    // 活躍錢包查詢
    @Query("SELECT w FROM Wallet w " +
            "WHERE w.lastTransactionTime >= :since " +
            "AND w.walletStatus = 'ACTIVE'")
    List<Wallet> findActiveWallets(@Param("since") LocalDateTime since);

    // 交易金額統計
    @Query("SELECT new map(" +
            "t.transactionType as type, " +
            "COUNT(t) as count, " +
            "SUM(t.amount) as totalAmount) " +
            "FROM Wallet w JOIN w.transactions t " +
            "WHERE w.walletId = :walletId " +
            "AND t.transactionTime BETWEEN :startTime AND :endTime " +
            "GROUP BY t.transactionType")
    List<Map<String, Object>> getTransactionStatistics(
            @Param("walletId") Long walletId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}