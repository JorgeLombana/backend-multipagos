package com.multipagos.multipagos_backend.topup.domain.port.out;

import com.multipagos.multipagos_backend.topup.domain.model.TransactionDomain;
import com.multipagos.multipagos_backend.topup.domain.model.TransactionStatus;
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
    Optional<TransactionDomain> findByTransactionId(String externalId);
    
    /**
     * Find all transactions for a user
     * @param userId the user identifier
     * @return List of user transactions
     */
    List<TransactionDomain> findByUserId(Long userId);
    
    /**
     * Find active transactions for a user
     * @param userId the user identifier
     * @return List of active user transactions
     */
    List<TransactionDomain> findByUserIdAndActiveTrue(Long userId);
    
    /**
     * Find transactions by phone number (active only)
     * @param phoneNumber the phone number
     * @return List of transactions for the phone number
     */
    List<TransactionDomain> findByPhoneNumberAndActiveTrue(String phoneNumber);
    
    /**
     * Find transactions by status
     * @param status the transaction status
     * @return List of transactions with the specified status
     */
    List<TransactionDomain> findByStatus(TransactionStatus status);
    
    /**
     * Update transaction status
     * @param id the transaction ID
     * @param status the new status
     * @return updated transaction
     */
    Optional<TransactionDomain> updateStatus(Long id, TransactionStatus status);
    
    /**
     * Mark transaction as inactive (soft delete)
     * @param id the transaction ID
     * @return true if successfully deactivated
     */
    boolean deactivateTransaction(Long id);
    
    /**
     * Check if transaction exists
     * @param id the transaction ID
     * @return true if transaction exists
     */
    boolean existsById(Long id);
    
    /**
     * Get total count of transactions
     * @return total number of transactions
     */
    long count();
    
    /**
     * Get count of active transactions for user
     * @param userId the user identifier
     * @return count of active transactions
     */
    long countActiveByUserId(Long userId);
}
