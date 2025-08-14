package com.multipagos.multipagos_backend.auth.application.service;

import com.multipagos.multipagos_backend.auth.domain.model.UserDomain;
import com.multipagos.multipagos_backend.auth.domain.port.in.AuthServicePort;
import com.multipagos.multipagos_backend.auth.domain.port.out.UserRepositoryPort;
import com.multipagos.multipagos_backend.auth.domain.port.out.PasswordEncoderPort;
import com.multipagos.multipagos_backend.shared.domain.exception.AuthenticationException;
import com.multipagos.multipagos_backend.shared.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Domain service implementing authentication business logic.
 * Coordinates user repository and password encoding operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService implements AuthServicePort {

  private final UserRepositoryPort userRepository;
  private final PasswordEncoderPort passwordEncoder;

  @Override
  public UserDomain authenticate(String email, String password) {
    log.info("Authenticating user with email: {}", email);

    try {
      Optional<UserDomain> userOpt = userRepository.findByEmail(email);

      if (userOpt.isEmpty()) {
        log.warn("Authentication failed - user not found: {}", email);
        throw new AuthenticationException("Email o contraseña incorrectos");
      }

      UserDomain user = userOpt.get();

      if (!passwordEncoder.matches(password, user.getPassword())) {
        log.warn("Authentication failed - invalid password for user: {}", email);
        throw new AuthenticationException("Email o contraseña incorrectos");
      }

      log.info("Authentication successful for user: {}", email);
      return user;

    } catch (AuthenticationException e) {
      throw e;
    } catch (Exception e) {
      log.error("Unexpected error during authentication for user: {}", email, e);
      throw new AuthenticationException("Error de autenticación");
    }
  }

  @Override
  public UserDomain register(String name, String email, String password) {
    log.info("Registering new user with email: {}", email);

    try {
      if (userRepository.findByEmail(email).isPresent()) {
        log.warn("Registration failed - user already exists: {}", email);
        throw new BusinessException("Ya existe un usuario registrado con este email");
      }

      String encodedPassword = passwordEncoder.encode(password);

      UserDomain newUser = UserDomain.builder()
          .name(name)
          .email(email)
          .password(encodedPassword)
          .build();

      newUser.prepareForRegistration();
      UserDomain savedUser = userRepository.save(newUser);

      log.info("User registered successfully with ID: {}", savedUser.getId());
      return savedUser;

    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      log.error("Unexpected error during registration for user: {}", email, e);
      throw new BusinessException("Error en el registro de usuario");
    }
  }

  @Override
  @Transactional(readOnly = true)
  public UserDomain getUserById(Long userId) {
    log.debug("Retrieving user with ID: {}", userId);

    try {
      Optional<UserDomain> userOpt = userRepository.findById(userId);

      if (userOpt.isEmpty()) {
        log.warn("User not found with ID: {}", userId);
        throw new BusinessException("Usuario no encontrado");
      }

      return userOpt.get();

    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      log.error("Unexpected error retrieving user with ID: {}", userId, e);
      throw new BusinessException("Error al obtener el usuario");
    }
  }
}
