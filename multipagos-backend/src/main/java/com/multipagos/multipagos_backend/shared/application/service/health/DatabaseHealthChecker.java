package com.multipagos.multipagos_backend.shared.application.service.health;

import com.multipagos.multipagos_backend.auth.domain.port.out.UserRepositoryPort;
import com.multipagos.multipagos_backend.shared.domain.value.HealthCheckConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Database health checker
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseHealthChecker {

  private final UserRepositoryPort userRepository;

  public Map<String, Object> checkDatabaseHealthWithTimeout() {
    try {
      CompletableFuture<Map<String, Object>> dbCheckFuture = CompletableFuture.supplyAsync(
          this::performDatabaseCheck);

      return dbCheckFuture.get(HealthCheckConstants.HEALTH_CHECK_TIMEOUT_MS, TimeUnit.MILLISECONDS);

    } catch (Exception e) {
      log.error("[DATABASE HEALTH] Check timed out or failed: {}", e.getMessage());

      return Map.of(
          HealthCheckConstants.FIELD_STATUS, HealthCheckConstants.STATUS_DOWN,
          HealthCheckConstants.FIELD_ERROR, "Database check timeout or failure",
          HealthCheckConstants.FIELD_DETAILS, Map.of(
              "timeout", HealthCheckConstants.HEALTH_CHECK_TIMEOUT_MS + "ms",
              "errorType", e.getClass().getSimpleName()));
    }
  }

  private Map<String, Object> performDatabaseCheck() {
    try {
      log.debug("[DATABASE HEALTH] Performing connectivity check");

      Long userCount = userRepository.countActiveUsers();

      log.debug("[DATABASE HEALTH] Check successful | activeUsers: {}", userCount);

      return Map.of(
          HealthCheckConstants.FIELD_STATUS, HealthCheckConstants.STATUS_UP,
          HealthCheckConstants.FIELD_DETAILS, Map.of(
              "connection", "OK",
              "responseTime", "< " + HealthCheckConstants.HEALTH_CHECK_TIMEOUT_MS + "ms",
              "activeUsers", userCount));

    } catch (Exception e) {
      log.error("[DATABASE HEALTH] Check failed: {}", e.getMessage());

      return Map.of(
          HealthCheckConstants.FIELD_STATUS, HealthCheckConstants.STATUS_DOWN,
          HealthCheckConstants.FIELD_ERROR, "Database connectivity failed",
          HealthCheckConstants.FIELD_DETAILS, Map.of(
              "connection", "FAILED",
              "errorMessage", e.getMessage(),
              "errorType", e.getClass().getSimpleName()));
    }
  }
}
