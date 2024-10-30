package org.example._citizenproj2.repository;

import org.example._citizenproj2.model.Transaction;
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

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    // 基本查詢
    Page<Transaction> findByWalletWalletId(Long walletId, Pageable pageable);

    List<Transaction> findByTransactionType(Transaction.TransactionType type);

    List<Transaction> findByReferenceId(String referenceId);

    // 狀態查詢
    Page<Transaction> findByWalletWalletIdAndStatus(
            Long walletId,
            Transaction.Status status,
            Pageable pageable);

    // 時間範圍查詢
    @Query("SELECT t FROM Transaction t WHERE t.wallet.walletId = :walletId " +
            "AND t.transactionTime BETWEEN :startTime AND :endTime " +
            "ORDER BY t.transactionTime DESC")
    List<Transaction> findByWalletWalletIdAndTransactionTimeBetween(
            @Param("walletId") Long walletId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    // 交易統計
    @Query("SELECT new map(" +
            "t.transactionType as type, " +
            "COUNT(t) as count, " +
            "SUM(t.amount) as totalAmount) " +
            "FROM Transaction t " +
            "WHERE t.wallet.walletId = :walletId " +
            "AND t.status = 'COMPLETED' " +
            "GROUP BY t.transactionType")
    List<Map<String, Object>> getTransactionStatistics(@Param("walletId") Long walletId);

    // 餘額計算
    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.wallet.walletId = :walletId " +
            "AND t.status = 'COMPLETED'")
    BigDecimal calculateBalance(@Param("walletId") Long walletId);

    // 日期統計
    @Query("SELECT new map(" +
            "FUNCTION('DATE', t.transactionTime) as date, " +
            "COUNT(t) as count, " +
            "SUM(t.amount) as totalAmount) " +
            "FROM Transaction t " +
            "WHERE t.wallet.walletId = :walletId " +
            "AND t.transactionTime BETWEEN :startTime AND :endTime " +
            "GROUP BY FUNCTION('DATE', t.transactionTime)")
    List<Map<String, Object>> getDailyStatistics(
            @Param("walletId") Long walletId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    // 類型統計
    @Query("SELECT new map(" +
            "t.transactionType as type, " +
            "COUNT(t) as count, " +
            "SUM(t.amount) as totalAmount, " +
            "MIN(t.amount) as minAmount, " +
            "MAX(t.amount) as maxAmount, " +
            "AVG(t.amount) as avgAmount) " +
            "FROM Transaction t " +
            "WHERE t.wallet.walletId = :walletId " +
            "AND t.status = 'COMPLETED' " +
            "AND t.transactionTime BETWEEN :startTime AND :endTime " +
            "GROUP BY t.transactionType")
    List<Map<String, Object>> getTypeStatistics(
            @Param("walletId") Long walletId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    // 最近交易查詢
    @Query("SELECT t FROM Transaction t " +
            "WHERE t.wallet.walletId = :walletId " +
            "AND t.status = 'COMPLETED' " +
            "ORDER BY t.transactionTime DESC")
    Page<Transaction> findRecentTransactions(
            @Param("walletId") Long walletId,
            Pageable pageable);

    // 大額交易查詢
    @Query("SELECT t FROM Transaction t " +
            "WHERE t.wallet.walletId = :walletId " +
            "AND ABS(t.amount) >= :minAmount " +
            "ORDER BY ABS(t.amount) DESC")
    List<Transaction> findLargeTransactions(
            @Param("walletId") Long walletId,
            @Param("minAmount") BigDecimal minAmount);

    // 參考編號相關交易
    @Query("SELECT t FROM Transaction t " +
            "WHERE t.referenceId = :referenceId " +
            "ORDER BY t.transactionTime")
    List<Transaction> findRelatedTransactions(@Param("referenceId") String referenceId);

    // 特定時間範圍的交易總額
    @Query("SELECT SUM(CASE WHEN t.amount > 0 THEN t.amount ELSE 0 END) as income, " +
            "SUM(CASE WHEN t.amount < 0 THEN t.amount ELSE 0 END) as expense " +
            "FROM Transaction t " +
            "WHERE t.wallet.walletId = :walletId " +
            "AND t.status = 'COMPLETED' " +
            "AND t.transactionTime BETWEEN :startTime AND :endTime")
    Map<String, BigDecimal> calculatePeriodBalance(
            @Param("walletId") Long walletId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}