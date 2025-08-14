package com.multipagos.multipagos_backend.auth.domain.port;

import com.multipagos.multipagos_backend.auth.domain.model.User;

/**
 * Domain port defining user service operations
 * Part of hexagonal architecture - ensures loose coupling between layers
 */
public interface UserServicePort {

  User registerUser(User user);

  User authenticateUser(String email, String password);
}
