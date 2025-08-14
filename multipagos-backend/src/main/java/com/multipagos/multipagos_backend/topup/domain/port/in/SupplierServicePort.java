package com.multipagos.multipagos_backend.topup.domain.port.in;

import com.multipagos.multipagos_backend.topup.domain.model.Supplier;
import java.util.List;
import java.util.Optional;

/**
 * Supplier Service Port (Inbound) 
 * Use case contract for supplier management operations
 */
public interface SupplierServicePort {
    
    /**
     * Get all available suppliers
     * @return List of all suppliers
     */
    List<Supplier> getAllSuppliers();
    
    /**
     * Get active suppliers only
     * @return List of active suppliers
     */
    List<Supplier> getActiveSuppliers();
    
    /**
     * Find supplier by ID
     * @param id the supplier identifier
     * @return Optional containing supplier if found
     */
    Optional<Supplier> getSupplierById(String id);
    
    /**
     * Check if supplier is valid and active
     * @param id the supplier identifier
     * @return true if supplier is valid and active
     */
    boolean isValidSupplier(String id);
    
    /**
     * Refresh supplier data from external source
     * @throws RuntimeException if refresh fails
     */
    void refreshSupplierData();
}
