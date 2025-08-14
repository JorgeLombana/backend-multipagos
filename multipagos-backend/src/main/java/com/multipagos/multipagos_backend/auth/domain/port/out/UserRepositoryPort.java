package com.multipagos.multipagos_backend.auth.domain.port.out;

import com.multipagos.multipagos_backend.auth.domain.model.UserDomain;

import java.util.Optional;

/**
 * Domain port for user persistence operations.
 */
public interface UserRepositoryPort {

  UserDomain save(UserDomain user);

  Optional<UserDomain> findById(Long id);

  Optional<UserDomain> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsById(Long id);

  void deleteById(Long id);

  Long countActiveUsers();
}
