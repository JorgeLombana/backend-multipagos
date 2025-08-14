package com.multipagos.multipagos_backend.topup.domain.port;

import com.multipagos.multipagos_backend.topup.domain.model.Transaction;
import com.multipagos.multipagos_backend.topup.domain.model.TopUpRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Domain port defining transaction service operations
 */
public interface TransactionServicePort {

  Transaction processTopUpTransaction(TopUpRequest request, Long userId);

  Page<Transaction> getUserTransactions(Long userId, Pageable pageable);

  Transaction getTransactionById(Long transactionId, Long userId);
}
