package com.multipagos.multipagos_backend.auth.domain.port.out;

/**
 * Domain port for password encoding operations.
 */
public interface PasswordEncoderPort {

    /**
     * Encodes raw password.
     * 
     * @param rawPassword plain text password
     * @return encoded password
     */
    String encode(String rawPassword);

    /**
     * Verifies if raw password matches encoded password.
     * 
     * @param rawPassword     plain text password to verify
     * @param encodedPassword encoded password to compare against
     * @return true if passwords match
     */
    boolean matches(String rawPassword, String encodedPassword);

    /**
     * Determines if encoding should be upgraded for security.
     * 
     * @param encodedPassword currently encoded password
     * @return true if encoding should be upgraded
     */
    boolean upgradeEncoding(String encodedPassword);
}
