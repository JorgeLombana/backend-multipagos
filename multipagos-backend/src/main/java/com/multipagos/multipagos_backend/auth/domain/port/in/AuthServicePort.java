package com.multipagos.multipagos_backend.auth.domain.port.in;

import com.multipagos.multipagos_backend.auth.domain.model.UserDomain;
import com.multipagos.multipagos_backend.shared.domain.exception.AuthenticationException;
import com.multipagos.multipagos_backend.shared.domain.exception.BusinessException;

/**
 * Domain port defining authentication service contract.
 */
public interface AuthServicePort {

    /**
     * Authenticates user credentials.
     * 
     * @param email    User email
     * @param password Raw password
     * @return Authenticated user domain object
     * @throws AuthenticationException if authentication fails
     */
    UserDomain authenticate(String email, String password);

    /**
     * Registers new user.
     * 
     * @param name     User full name
     * @param email    User email
     * @param password Raw password
     * @return Registered user domain object
     * @throws BusinessException if registration fails
     */
    UserDomain register(String name, String email, String password);

    /**
     * Retrieves user by ID.
     * 
     * @param userId User identifier
     * @return User domain object
     * @throws BusinessException if user not found
     */
    UserDomain getUserById(Long userId);
}
