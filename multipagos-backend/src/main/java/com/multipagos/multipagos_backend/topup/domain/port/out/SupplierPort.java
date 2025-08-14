package com.multipagos.multipagos_backend.topup.domain.port.out;

import com.multipagos.multipagos_backend.topup.domain.model.Supplier;
import java.util.List;
import java.util.Optional;

/**
 * Supplier Port (Outbound)
 * Contract for accessing supplier information from external sources
 */
public interface SupplierPort {
    
    /**
     * Get all available suppliers
     * @return List of suppliers
     * @throws RuntimeException if supplier retrieval fails
     */
    List<Supplier> getAllSuppliers();
    
    /**
     * Find supplier by ID
     * @param id the supplier identifier  
     * @return Optional containing the supplier if found
     */
    Optional<Supplier> findById(String id);
    
    /**
     * Check if supplier exists
     * @param id the supplier identifier
     * @return true if supplier exists, false otherwise
     */
    boolean existsById(String id);
    
    /**
     * Get active suppliers only
     * @return List of active suppliers
     */
    List<Supplier> getActiveSuppliers();
    
    /**
     * Refresh supplier cache/data from external source
     * @throws RuntimeException if refresh fails
     */
    void refreshSuppliers();
}
