package com.multipagos.multipagos_backend.auth.presentation.controller;

import com.multipagos.multipagos_backend.auth.application.service.UserService;
import com.multipagos.multipagos_backend.auth.domain.model.User;
import com.multipagos.multipagos_backend.shared.application.util.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<?> createUser(@Valid @RequestBody User user, HttpServletRequest request) {
    try {
      log.info("[USER CREATE] Creating user with email: {}", user.getEmail());

      User createdUser = userService.createUser(user);
      createdUser.setPassword("***");

      log.info("[USER CREATE SUCCESS] User created successfully | ID: {} | email: {}",
          createdUser.getId(), createdUser.getEmail());

      return ResponseFactory.success(createdUser, "Usuario creado exitosamente");

    } catch (IllegalArgumentException e) {
      log.error("[USER CREATE ERROR] Validation failed | email: {} | error: {}",
          user.getEmail(), e.getMessage());
      return ResponseFactory.badRequest(e.getMessage(), request.getRequestURI());
    } catch (Exception e) {
      log.error("[USER CREATE ERROR] Unexpected error | email: {} | error: {}",
          user.getEmail(), e.getMessage(), e);
      return ResponseFactory.internalServerError(request.getRequestURI());
    }
  }

  @PostMapping("/validate-password")
  public ResponseEntity<?> validatePassword(@RequestBody Map<String, String> credentials,
      HttpServletRequest request) {
    try {
      String email = credentials.get("email");
      String password = credentials.get("password");

      log.info("[USER LOGIN] Login attempt for email: {}", email);

      if (email == null || password == null) {
        log.warn("[USER LOGIN ERROR] Missing credentials | email: {} | password: {}",
            email != null ? email : "null", password != null ? "provided" : "null");
        return ResponseFactory.badRequest("Email y contraseña son requeridos", request.getRequestURI());
      }

      boolean isValid = userService.validatePassword(email, password);

      if (isValid) {
        log.info("[USER LOGIN SUCCESS] Valid credentials | email: {}", email);
        return ResponseFactory.success(Map.of("valid", true), "Credenciales válidas");
      } else {
        log.warn("[USER LOGIN FAILED] Invalid credentials | email: {}", email);
        return ResponseFactory.success(Map.of("valid", false), "Credenciales inválidas");
      }

    } catch (Exception e) {
      log.error("[USER LOGIN ERROR] Error validating password | error: {}", e.getMessage(), e);
      return ResponseFactory.internalServerError(request.getRequestURI());
    }
  }
}
