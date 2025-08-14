package com.multipagos.multipagos_backend.topup.domain.port.out;

import com.multipagos.multipagos_backend.topup.domain.model.AuthToken;

/**
 * Authentication Port (Outbound)
 * Represents the contract for external authentication services.
 * Generic port that can be implemented by different authentication providers.
 */
public interface AuthenticationPort {
    
    /**
     * Authenticate with external service and obtain auth token
     * @param username the username for authentication
     * @param password the password for authentication
     * @return AuthToken containing bearer token
     * @throws RuntimeException if authentication fails
     */
    AuthToken authenticate(String username, String password);
    
    /**
     * Check if authentication is required for the service
     * @return true if authentication is required, false otherwise
     */
    boolean isAuthenticationRequired();
    
    /**
     * Get the current authentication status
     * @return true if currently authenticated, false otherwise  
     */
    boolean isAuthenticated();
    
    /**
     * Clear current authentication state
     */
    void clearAuthentication();
}
