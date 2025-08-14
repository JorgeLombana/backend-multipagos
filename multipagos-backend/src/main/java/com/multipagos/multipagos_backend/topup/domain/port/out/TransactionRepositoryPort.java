package com.multipagos.multipagos_backend.topup.domain.port.out;

import com.multipagos.multipagos_backend.shared.domain.value.PageRequest;
import com.multipagos.multipagos_backend.shared.domain.value.PagedResult;
import com.multipagos.multipagos_backend.topup.domain.model.TransactionDomain;
import com.multipagos.multipagos_backend.topup.domain.model.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Transaction Repository Port (Outbound)
 * Contract for persisting and retrieving transaction data
 */
public interface TransactionRepositoryPort {
    
    /**
     * Save transaction to persistence store
     * @param transaction the transaction domain object to save
     * @return the saved transaction with generated ID
     */
    TransactionDomain save(TransactionDomain transaction);
    
    /**
     * Find transaction by ID
     * @param id the transaction identifier
     * @return Optional containing transaction if found
     */
    Optional<TransactionDomain> findById(Long id);
    
    /**
     * Find transaction by external transaction ID
     * @param externalId the external transaction identifier
     * @return Optional containing transaction if found
     */
    Optional<TransactionDomain> findByExternalTransactionId(String externalId);
    
    /**
     * Find all transactions for a user (including inactive)
     * @param userId the user identifier
     * @return List of all user transactions
     */
    List<TransactionDomain> findByUserId(Long userId);
    
    /**
     * Find active transactions for a user (simple list)
     * @param userId the user identifier
     * @return List of active user transactions
     */
    List<TransactionDomain> findByUserIdAndActiveTrue(Long userId);
    
    /**
     * Find active transactions for a user with pagination
     * @param userId the user identifier
     * @param pageRequest pagination parameters
     * @return PagedResult containing transactions
     */
    PagedResult<TransactionDomain> findByUserIdAndActiveTrue(Long userId, PageRequest pageRequest);
    
    /**
     * Find transactions for a user with specific status (active only)
     * @param userId the user identifier
     * @param status the transaction status
     * @return List of transactions matching criteria
     */
    List<TransactionDomain> findByUserIdAndStatus(Long userId, TransactionStatus status);
    
    /**
     * Find transactions by phone number (active only)
     * @param phoneNumber the phone number
     * @return List of transactions for the phone number
     */
    List<TransactionDomain> findByPhoneNumberAndActiveTrue(String phoneNumber);
    
    /**
     * Find transactions by user and date range
     * @param userId the user identifier
     * @param startDate start of date range
     * @param endDate end of date range
     * @return List of transactions in date range
     */
    List<TransactionDomain> findByUserAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get total transaction amount for user (completed transactions only)
     * @param userId the user identifier
     * @return total amount
     */
    BigDecimal getTotalAmountByUser(Long userId);
    
    /**
     * Count transactions by status (active only)
     * @param status the transaction status
     * @return count of transactions with status
     */
    Long countByStatus(TransactionStatus status);
    
    /**
     * Count active transactions for user
     * @param userId the user identifier
     * @return count of active transactions
     */
    Long countByUserId(Long userId);
    
    /**
     * Find latest transactions for user (limited)
     * @param userId the user identifier
     * @param limit maximum number of transactions
     * @return List of latest transactions
     */
    List<TransactionDomain> findLatestByUserId(Long userId, int limit);
    
    /**
     * Check if transaction exists
     * @param id the transaction ID
     * @return true if transaction exists
     */
    boolean existsById(Long id);
    
    /**
     * Soft delete transaction (mark as inactive)
     * @param id the transaction ID
     */
    void deleteById(Long id);
    
    /**
     * Update transaction status
     * @param id the transaction ID
     * @param status the new status
     * @return updated transaction
     */
    Optional<TransactionDomain> updateStatus(Long id, TransactionStatus status);
}
