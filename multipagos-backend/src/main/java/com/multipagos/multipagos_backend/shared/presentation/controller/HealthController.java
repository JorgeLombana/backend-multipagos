package com.multipagos.multipagos_backend.shared.presentation.controller;

import com.multipagos.multipagos_backend.auth.infrastructure.repository.UserRepository;
import com.multipagos.multipagos_backend.shared.application.util.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Health check controller for database connectivity
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/health")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class HealthController {

  private final UserRepository userRepository;

  @GetMapping("/db")
  public ResponseEntity<?> checkDatabaseHealth(HttpServletRequest request) {
    try {
      log.info("[HEALTH CHECK] Starting database health check");

      Long userCount = userRepository.count();

      Map<String, Object> healthData = Map.of(
          "status", "UP",
          "database", "Connected",
          "userCount", userCount,
          "timestamp", System.currentTimeMillis());

      log.info("[HEALTH CHECK SUCCESS] Database is healthy | userCount: {}", userCount);
      return ResponseFactory.success(healthData, "Base de datos saludable");

    } catch (Exception e) {
      log.error("[HEALTH CHECK ERROR] Database health check failed | error: {}", e.getMessage(), e);

      Map<String, Object> errorData = Map.of(
          "status", "DOWN",
          "database", "Error: " + e.getMessage(),
          "timestamp", System.currentTimeMillis());

      return ResponseFactory.success(errorData, "Error en verificación de base de datos");
    }
  }
}
