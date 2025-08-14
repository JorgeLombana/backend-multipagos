package com.multipagos.multipagos_backend.auth.application.service;

import com.multipagos.multipagos_backend.auth.domain.model.UserDomain;
import com.multipagos.multipagos_backend.auth.domain.port.in.AuthServicePort;
import com.multipagos.multipagos_backend.auth.presentation.dto.LoginRequest;
import com.multipagos.multipagos_backend.auth.presentation.dto.LoginResponse;
import com.multipagos.multipagos_backend.auth.presentation.dto.RegisterRequest;
import com.multipagos.multipagos_backend.shared.application.util.SecurityValidator;
import com.multipagos.multipagos_backend.shared.domain.exception.AuthenticationException;
import com.multipagos.multipagos_backend.shared.domain.exception.BusinessException;
import com.multipagos.multipagos_backend.shared.domain.port.TokenGeneratorPort;
import com.multipagos.multipagos_backend.shared.domain.valueobject.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service orchestrating authentication operations.
 * Handles input validation, coordinates domain services, and manages tokens.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthApplicationService {

  private final AuthServicePort authService;
  private final SecurityValidator securityValidator;
  private final TokenGeneratorPort tokenGenerator;

  /**
   * Authenticates user and returns JWT token with user information.
   *
   * @param loginRequest user credentials
   * @return complete login response with JWT token
   * @throws AuthenticationException  if credentials are invalid
   * @throws IllegalArgumentException if input validation fails
   */
  public LoginResponse authenticateUser(LoginRequest loginRequest) {
    log.info("[AUTH APP] Authentication attempt for email: {}", loginRequest.getEmail());

    try {
      LoginRequest sanitizedRequest = validateAndSanitizeLoginRequest(loginRequest);
      UserDomain authenticatedUser = authService.authenticate(
          sanitizedRequest.getEmail(),
          sanitizedRequest.getPassword());

      String token = tokenGenerator.generateToken(
          authenticatedUser.getEmail(),
          authenticatedUser.getId(),
          authenticatedUser.getName());

      LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
          authenticatedUser.getId(),
          authenticatedUser.getName(),
          authenticatedUser.getEmail());

      LoginResponse response = new LoginResponse(token, userInfo);

      log.info("[AUTH APP] Authentication successful for user: {} | ID: {}",
          authenticatedUser.getEmail(), authenticatedUser.getId());

      return response;

    } catch (AuthenticationException | IllegalArgumentException e) {
      log.warn("[AUTH APP] Authentication failed for email: {} | reason: {}",
          loginRequest.getEmail(), e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("[AUTH APP] Unexpected error during authentication for email: {} | error: {}",
          loginRequest.getEmail(), e.getMessage(), e);
      throw new AuthenticationException("Authentication failed due to system error");
    }
  }

  /**
   * Registers new user in the system.
   *
   * @param registerRequest user registration data
   * @return newly registered user
   * @throws BusinessException        if registration business rules fail
   * @throws IllegalArgumentException if input validation fails
   */
  public UserDomain registerUser(RegisterRequest registerRequest) {
    log.info("[AUTH APP] Registration attempt for email: {}", registerRequest.getEmail());

    try {
      RegisterRequest sanitizedRequest = validateAndSanitizeRegisterRequest(registerRequest);
      UserDomain registeredUser = authService.register(
          sanitizedRequest.getName(),
          sanitizedRequest.getEmail(),
          sanitizedRequest.getPassword());

      log.info("[AUTH APP] Registration successful for user: {} | ID: {}",
          registeredUser.getEmail(), registeredUser.getId());

      return registeredUser;

    } catch (BusinessException | IllegalArgumentException e) {
      log.warn("[AUTH APP] Registration failed for email: {} | reason: {}",
          registerRequest.getEmail(), e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("[AUTH APP] Unexpected error during registration for email: {} | error: {}",
          registerRequest.getEmail(), e.getMessage(), e);
      throw new BusinessException("Registration failed due to system error");
    }
  }

  @Transactional(readOnly = true)
  public UserDomain getUserById(Long userId) {
    log.debug("[AUTH APP] Retrieving user with ID: {}", userId);
    return authService.getUserById(userId);
  }

  private LoginRequest validateAndSanitizeLoginRequest(LoginRequest originalRequest) {
    ValidationResult emailResult = securityValidator.validateEmail(originalRequest.getEmail());
    if (!emailResult.isValid()) {
      throw new IllegalArgumentException(emailResult.getErrorMessage());
    }

    if (originalRequest.getPassword() == null || originalRequest.getPassword().trim().isEmpty()) {
      throw new IllegalArgumentException("La contraseña es obligatoria");
    }

    return new LoginRequest(emailResult.getValue(), originalRequest.getPassword());
  }

  private RegisterRequest validateAndSanitizeRegisterRequest(RegisterRequest originalRequest) {
    ValidationResult emailResult = securityValidator.validateEmail(originalRequest.getEmail());
    if (!emailResult.isValid()) {
      throw new IllegalArgumentException(emailResult.getErrorMessage());
    }

    ValidationResult nameResult = securityValidator.validateUserName(originalRequest.getName());
    if (!nameResult.isValid()) {
      throw new IllegalArgumentException(nameResult.getErrorMessage());
    }

    if (originalRequest.getPassword() == null || originalRequest.getPassword().trim().isEmpty()) {
      throw new IllegalArgumentException("La contraseña es obligatoria");
    }

    return RegisterRequest.builder()
        .email(emailResult.getValue())
        .name(nameResult.getValue())
        .password(originalRequest.getPassword())
        .build();
  }
}
