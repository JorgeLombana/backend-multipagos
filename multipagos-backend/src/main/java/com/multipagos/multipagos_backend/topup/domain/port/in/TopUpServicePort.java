package com.multipagos.multipagos_backend.topup.domain.port.in;

import com.multipagos.multipagos_backend.topup.domain.model.TopUpRequest;
import com.multipagos.multipagos_backend.topup.domain.model.TransactionDomain;

/**
 * TopUp Service Port (Inbound)
 * Use case contract for top-up operations
 */
public interface TopUpServicePort {
    
    /**
     * Execute a top-up transaction
     * @param request the top-up request
     * @return the created transaction domain object
     * @throws RuntimeException if top-up fails
     */
    TransactionDomain executeTopUp(TopUpRequest request);
    
    /**
     * Validate a top-up request before execution
     * @param request the top-up request to validate
     * @return true if request is valid for processing
     * @throws IllegalArgumentException if request is invalid
     */
    boolean validateTopUpRequest(TopUpRequest request);
}
