package com.multipagos.multipagos_backend.topup.infrastructure.repository;

import com.multipagos.multipagos_backend.topup.domain.model.Transaction;
import com.multipagos.multipagos_backend.topup.domain.model.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

       Page<Transaction> findByUserIdAndActiveTrueOrderByCreatedAtDesc(Long userId, Pageable pageable);

       List<Transaction> findByUserIdAndStatusAndActiveTrue(Long userId, TransactionStatus status);

       List<Transaction> findByPhoneNumberAndActiveTrueOrderByCreatedAtDesc(String phoneNumber);

       Optional<Transaction> findByExternalTransactionIdAndActiveTrue(String externalTransactionId);

       @Query("SELECT t FROM Transaction t WHERE t.active = true AND " +
                     "t.user.id = :userId AND " +
                     "t.createdAt BETWEEN :startDate AND :endDate " +
                     "ORDER BY t.createdAt DESC")
       List<Transaction> findByUserAndDateRange(@Param("userId") Long userId,
                     @Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
                     "t.user.id = :userId AND t.status = 'COMPLETED' AND t.active = true")
       BigDecimal getTotalAmountByUser(@Param("userId") Long userId);

       Long countByStatusAndActiveTrue(TransactionStatus status);

       Long countByUserIdAndActiveTrue(Long userId);

       List<Transaction> findTop10ByUserIdAndActiveTrueOrderByCreatedAtDesc(Long userId);
}
