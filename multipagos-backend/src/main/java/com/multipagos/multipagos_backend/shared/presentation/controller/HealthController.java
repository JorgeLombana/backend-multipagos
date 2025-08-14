package com.multipagos.multipagos_backend.shared.presentation.controller;

import com.multipagos.multipagos_backend.shared.application.util.ResponseFactory;
import com.multipagos.multipagos_backend.shared.domain.port.HealthServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Simple health check controller
 * Single endpoint for system health verification
 */
@Slf4j
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {

  private final HealthServicePort healthService;

  /**
   * Check overall system health
   * 
   * @param request HTTP request for error context
   * @return ResponseEntity with system health status
   */
  @GetMapping
  public ResponseEntity<?> checkHealth(HttpServletRequest request) {
    try {
      log.info("[HEALTH CONTROLLER] Health check requested");

      Map<String, Object> healthData = healthService.checkHealth();
      boolean isHealthy = "UP".equals(healthData.get("status"));

      String message = isHealthy ? "Sistema saludable" : "Problemas detectados en el sistema";

      log.info("[HEALTH CONTROLLER] Health check completed | status: {}", healthData.get("status"));
      return ResponseFactory.success(healthData, message);

    } catch (Exception e) {
      log.error("[HEALTH CONTROLLER] Error during health check | error: {}", e.getMessage(), e);
      return ResponseFactory.internalServerError(request.getRequestURI());
    }
  }
}
