package com.multipagos.multipagos_backend.topup.domain.port.out;

import com.multipagos.multipagos_backend.topup.domain.model.TopUpRequest;

/**
 * TopUp Port (Outbound)
 * Contract for executing top-up operations with external services
 */
public interface TopUpPort {
    
    /**
     * Execute top-up transaction with external provider
     * @param request the top-up request containing all necessary data
     * @return transaction ID from the external provider
     * @throws RuntimeException if top-up execution fails
     */
    String executeTopUp(TopUpRequest request);
    
    /**
     * Check if top-up service is available
     * @return true if service is available, false otherwise
     */
    boolean isServiceAvailable();
    
    /**
     * Get service provider name
     * @return name of the external top-up provider
     */
    String getProviderName();
}
