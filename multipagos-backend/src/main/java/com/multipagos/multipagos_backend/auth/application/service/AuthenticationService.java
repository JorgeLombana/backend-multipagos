package com.multipagos.multipagos_backend.auth.application.service;

import com.multipagos.multipagos_backend.auth.domain.model.User;
import com.multipagos.multipagos_backend.auth.presentation.dto.LoginResponse;
import com.multipagos.multipagos_backend.shared.application.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Application service for authentication operations
 * Handles JWT token generation and response building
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

  private final JwtService jwtService;

  public LoginResponse createAuthResponse(User user) {
    log.info("[AUTH SERVICE] Creating authentication response for user ID: {}", user.getId());

    String token = jwtService.generateToken(
        user.getEmail(),
        user.getId(),
        user.getName());

    LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getPhoneNumber());

    return new LoginResponse(token, userInfo);
  }
}
