package com.multipagos.multipagos_backend.auth.application.service;

import com.multipagos.multipagos_backend.auth.domain.model.User;
import com.multipagos.multipagos_backend.auth.domain.port.UserServicePort;
import com.multipagos.multipagos_backend.auth.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Application service implementing user business logic
 * Implements hexagonal architecture port
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserServicePort {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public User registerUser(User user) {
    log.info("[USER SERVICE] Registering new user with email: {}", user.getEmail());

    if (userRepository.existsByEmail(user.getEmail())) {
      throw new IllegalArgumentException("Usuario ya existe con este email");
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));

    User savedUser = userRepository.save(user);
    log.info("[USER SERVICE] User registered successfully with ID: {}", savedUser.getId());

    return savedUser;
  }

  @Override
  @Transactional(readOnly = true)
  public User authenticateUser(String email, String password) {
    log.info("[USER SERVICE] Authenticating user with email: {}", email);

    Optional<User> userOpt = userRepository.findByEmail(email);
    if (userOpt.isPresent()) {
      User user = userOpt.get();
      boolean isValid = passwordEncoder.matches(password, user.getPassword());

      if (isValid) {
        log.info("[USER SERVICE] Authentication successful for email: {}", email);
        return user;
      } else {
        log.warn("[USER SERVICE] Invalid password for email: {}", email);
        return null;
      }
    }

    log.warn("[USER SERVICE] User not found for email: {}", email);
    return null;
  }
}
