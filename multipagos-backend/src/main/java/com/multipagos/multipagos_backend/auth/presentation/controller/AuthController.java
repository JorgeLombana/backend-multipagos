package com.multipagos.multipagos_backend.auth.presentation.controller;

import com.multipagos.multipagos_backend.auth.application.service.AuthApplicationService;
import com.multipagos.multipagos_backend.auth.domain.model.UserDomain;
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
 * REST controller for authentication endpoints.
 * Handles HTTP requests and delegates to application service.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthApplicationService authApplicationService;

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest,
      HttpServletRequest request) {

    log.info("[AUTH CONTROLLER] Registration request for email: {}", registerRequest.getEmail());

    try {
      UserDomain registeredUser = authApplicationService.registerUser(registerRequest);

      log.info("[AUTH CONTROLLER] Registration successful for user: {} | ID: {}",
          registeredUser.getEmail(), registeredUser.getId());

      return ResponseFactory.success("Usuario registrado exitosamente", "Usuario registrado exitosamente");

    } catch (IllegalArgumentException e) {
      log.warn("[AUTH CONTROLLER] Registration validation failed for email: {} | error: {}",
          registerRequest.getEmail(), e.getMessage());
      return ResponseFactory.badRequest(e.getMessage(), request.getRequestURI());
    } catch (Exception e) {
      log.error("[AUTH CONTROLLER] Registration error for email: {} | error: {}",
          registerRequest.getEmail(), e.getMessage(), e);
      return ResponseFactory.internalServerError(request.getRequestURI());
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest,
      HttpServletRequest request) {

    log.info("[AUTH CONTROLLER] Login request for email: {}", loginRequest.getEmail());

    try {
      LoginResponse response = authApplicationService.authenticateUser(loginRequest);

      log.info("[AUTH CONTROLLER] Login successful for user: {} | ID: {}",
          loginRequest.getEmail(), response.getUser().getId());

      return ResponseFactory.success(response, "Login exitoso");

    } catch (IllegalArgumentException e) {
      log.warn("[AUTH CONTROLLER] Login validation failed for email: {} | error: {}",
          loginRequest.getEmail(), e.getMessage());
      return ResponseFactory.badRequest(e.getMessage(), request.getRequestURI());
    } catch (Exception e) {
      log.error("[AUTH CONTROLLER] Login error for email: {} | error: {}",
          loginRequest.getEmail(), e.getMessage(), e);
      return ResponseFactory.internalServerError(request.getRequestURI());
    }
  }
}
