package com.multipagos.multipagos_backend.shared.domain.port;

import java.util.Date;

/**
 * Domain Port for Token Generation and Validation
 * Abstracts JWT or any other token mechanism from domain layer
 * Allows domain to work with authentication tokens without knowing implementation details
 */
public interface TokenGeneratorPort {

    /**
     * Generate authentication token for user
     * @param email User email (subject)
     * @param userId User ID
     * @param name User name
     * @return Generated token string
     */
    String generateToken(String email, Long userId, String name);

    /**
     * Validate token for given user
     * @param token Token to validate
     * @param email User email to validate against
     * @return true if token is valid for the user
     */
    boolean validateToken(String token, String email);

    /**
     * Extract email from token
     * @param token Token to extract from
     * @return User email from token
     */
    String extractEmail(String token);

    /**
     * Extract user ID from token
     * @param token Token to extract from
     * @return User ID from token
     */
    Long extractUserId(String token);

    /**
     * Extract user name from token
     * @param token Token to extract from
     * @return User name from token
     */
    String extractName(String token);

    /**
     * Extract expiration date from token
     * @param token Token to extract from
     * @return Expiration date from token
     */
    Date extractExpiration(String token);

    /**
     * Check if token is expired
     * @param token Token to check
     * @return true if token is expired
     */
    boolean isTokenExpired(String token);
}
