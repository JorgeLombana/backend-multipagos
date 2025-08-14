package com.multipagos.multipagos_backend.topup.domain.port.in;

import com.multipagos.multipagos_backend.shared.domain.value.PageRequest;
import com.multipagos.multipagos_backend.shared.domain.value.PagedResult;
import com.multipagos.multipagos_backend.topup.domain.model.TransactionDomain;
import com.multipagos.multipagos_backend.topup.domain.model.TransactionStatus;
import java.util.List;
import java.util.Optional;

/**
 * Transaction Service Port (Inbound)
 * Use case contract for transaction management operations
 */
public interface TransactionServicePort {
    
    /**
     * Get transaction by ID
     * @param id the transaction identifier
     * @return Optional containing transaction if found
     */
    Optional<TransactionDomain> getTransactionById(Long id);
    
    /**
     * Get transaction by external transaction ID
     * @param transactionId the external transaction identifier
     * @return Optional containing transaction if found
     */
    Optional<TransactionDomain> getTransactionByTransactionId(String transactionId);
    
    /**
     * Get all transactions for a user
     * @param userId the user identifier
     * @return List of user transactions
     */
    List<TransactionDomain> getTransactionsByUser(Long userId);
    
    /**
     * Get active transactions for a user
     * @param userId the user identifier
     * @return List of active user transactions
     */
    List<TransactionDomain> getActiveTransactionsByUser(Long userId);
    
    /**
     * Get transactions by phone number
     * @param phoneNumber the phone number
     * @return List of transactions for the phone number
     */
    List<TransactionDomain> getTransactionsByPhone(String phoneNumber);
    
    /**
     * Update transaction status
     * @param id the transaction ID
     * @param status the new status
     * @return updated transaction
     * @throws RuntimeException if update fails
     */
    Optional<TransactionDomain> updateTransactionStatus(Long id, TransactionStatus status);
    
    /**
     * Get transaction history for user (active transactions only)
     * @param userId the user identifier
     * @return List of transaction history
     */
    List<TransactionDomain> getTransactionHistory(Long userId);
    
    /**
     * Get paginated transaction history for user
     * @param userId the user identifier
     * @param pageRequest pagination parameters
     * @return Paged result of transaction history
     */
    PagedResult<TransactionDomain> getTransactionHistoryPaged(Long userId, PageRequest pageRequest);
}
