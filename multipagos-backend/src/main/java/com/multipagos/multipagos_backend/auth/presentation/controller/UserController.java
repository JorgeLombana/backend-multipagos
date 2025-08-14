package com.multipagos.multipagos_backend.auth.presentation.controller;

import com.multipagos.multipagos_backend.auth.application.mapper.UserMapper;
import com.multipagos.multipagos_backend.auth.application.service.AuthenticationService;
import com.multipagos.multipagos_backend.auth.domain.model.User;
import com.multipagos.multipagos_backend.auth.domain.port.UserServicePort;
import com.multipagos.multipagos_backend.auth.presentation.dto.LoginRequest;
import com.multipagos.multipagos_backend.auth.presentation.dto.LoginResponse;
import com.multipagos.multipagos_backend.auth.presentation.dto.RegisterRequest;
import com.multipagos.multipagos_backend.shared.application.util.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * REST controller for user registration and authentication
 * Follows hexagonal architecture and single responsibility principle
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "${app.cors.allowed-origins:http://localhost:3000,http://localhost:3001}")
@RequiredArgsConstructor
public class UserController {

  private final UserServicePort userService;
  private final AuthenticationService authenticationService;
  private final UserMapper userMapper;

  @PostMapping
  public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest,
      HttpServletRequest request) {
    try {
      log.info("[USER REGISTER] Registration attempt for email: {}", registerRequest.getEmail());

      User user = userMapper.toDomain(registerRequest);
      User registeredUser = userService.registerUser(user);

      LoginResponse response = authenticationService.createAuthResponse(registeredUser);

      log.info("[USER REGISTER SUCCESS] User registered and authenticated | ID: {} | email: {}",
          registeredUser.getId(), registeredUser.getEmail());

      return ResponseFactory.success(response, "Usuario registrado exitosamente");

    } catch (IllegalArgumentException e) {
      log.error("[USER REGISTER ERROR] Business validation failed | email: {} | error: {}",
          registerRequest.getEmail(), e.getMessage());
      return ResponseFactory.badRequest(e.getMessage(), request.getRequestURI());
    } catch (Exception e) {
      log.error("[USER REGISTER ERROR] Unexpected error | email: {} | error: {}",
          registerRequest.getEmail(), e.getMessage(), e);
      return ResponseFactory.internalServerError(request.getRequestURI());
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
    try {
      log.info("[USER LOGIN] Authentication attempt for email: {}", loginRequest.getEmail());

      User authenticatedUser = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

      if (authenticatedUser != null) {
        LoginResponse response = authenticationService.createAuthResponse(authenticatedUser);

        log.info("[USER LOGIN SUCCESS] Authentication successful | email: {} | ID: {}",
            loginRequest.getEmail(), authenticatedUser.getId());

        return ResponseFactory.success(response, "Login exitoso");
      } else {
        log.warn("[USER LOGIN FAILED] Invalid credentials | email: {}", loginRequest.getEmail());
        return ResponseFactory.badRequest("Credenciales inválidas", request.getRequestURI());
      }

    } catch (Exception e) {
      log.error("[USER LOGIN ERROR] Unexpected error during authentication | email: {} | error: {}",
          loginRequest.getEmail(), e.getMessage(), e);
      return ResponseFactory.internalServerError(request.getRequestURI());
    }
  }
}
