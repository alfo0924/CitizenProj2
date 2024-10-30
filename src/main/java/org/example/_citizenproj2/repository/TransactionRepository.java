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
    // 修改返回類型為 Page<Transaction>
    Page<Transaction> findByWalletWalletId(Long walletId, Pageable pageable);



    List<Transaction> findByTransactionType(Transaction.TransactionType type);

    // 交易記錄查詢
    @Query("SELECT t FROM Transaction t WHERE t.wallet.walletId = :walletId " +
            "AND t.transactionTime BETWEEN :startTime AND :endTime")
    List<Transaction> findTransactionHistory(
            @Param("walletId") Long walletId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

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
    // 餘額查詢
    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.wallet.walletId = :walletId " +
            "AND t.status = 'COMPLETED'")
    BigDecimal calculateBalance(@Param("walletId") Long walletId);

    // 參考編號查詢
    List<Transaction> findByReferenceId(String referenceId);

    // 分頁查詢
    Page<Transaction> findByWalletWalletIdAndStatus(
            Long walletId,
            Transaction.Status status,
            Pageable pageable);
}