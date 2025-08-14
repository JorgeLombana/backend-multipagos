package com.multipagos.multipagos_backend.shared.application.service;

import com.multipagos.multipagos_backend.shared.application.service.health.DatabaseHealthChecker;
import com.multipagos.multipagos_backend.shared.application.service.health.SystemHealthChecker;
import com.multipagos.multipagos_backend.shared.domain.port.HealthServicePort;
import com.multipagos.multipagos_backend.shared.domain.value.HealthCheckConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Comprehensive Health Check Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HealthService implements HealthServicePort {

  private final DatabaseHealthChecker databaseHealthChecker;
  private final SystemHealthChecker systemHealthChecker;

  @Override
  public Map<String, Object> checkHealth() {
    log.info("[HEALTH SERVICE] Starting comprehensive health check");

    Map<String, Object> healthData = new HashMap<>();
    Map<String, Object> components = new HashMap<>();

    try {
      // Check database health
      Map<String, Object> databaseHealth = databaseHealthChecker.checkDatabaseHealthWithTimeout();
      components.put(HealthCheckConstants.COMPONENT_DATABASE, databaseHealth);

      // Check system resources
      Map<String, Object> systemHealth = systemHealthChecker.checkSystemResources();
      components.put(HealthCheckConstants.COMPONENT_SYSTEM, systemHealth);

      // Determine overall status
      String overallStatus = determineOverallStatus(databaseHealth, systemHealth);

      // Build response
      healthData.put(HealthCheckConstants.FIELD_STATUS, overallStatus);
      healthData.put(HealthCheckConstants.FIELD_TIMESTAMP, LocalDateTime.now());
      healthData.put(HealthCheckConstants.FIELD_COMPONENTS, components);
      healthData.put(HealthCheckConstants.FIELD_VERSION, "1.0.0");

      log.info("[HEALTH SERVICE] Health check completed | overall: {} | database: {} | system: {}",
          overallStatus, databaseHealth.get(HealthCheckConstants.FIELD_STATUS),
          systemHealth.get(HealthCheckConstants.FIELD_STATUS));

    } catch (Exception e) {
      log.error("[HEALTH SERVICE] Health check failed with exception: {}", e.getMessage(), e);

      healthData.put(HealthCheckConstants.FIELD_STATUS, HealthCheckConstants.STATUS_DOWN);
      healthData.put(HealthCheckConstants.FIELD_TIMESTAMP, LocalDateTime.now());
      healthData.put(HealthCheckConstants.FIELD_ERROR, "Health check execution failed");
      healthData.put(HealthCheckConstants.FIELD_COMPONENTS, components);
    }

    return healthData;
  }

  private String determineOverallStatus(Map<String, Object> databaseHealth,
      Map<String, Object> systemHealth) {
    boolean isDatabaseHealthy = HealthCheckConstants.STATUS_UP.equals(
        databaseHealth.get(HealthCheckConstants.FIELD_STATUS));
    boolean isSystemHealthy = HealthCheckConstants.STATUS_UP.equals(
        systemHealth.get(HealthCheckConstants.FIELD_STATUS));

    if (isDatabaseHealthy && isSystemHealthy) {
      return HealthCheckConstants.STATUS_UP;
    }

    // If any component is degraded but not down, overall is degraded
    boolean isDatabaseDegraded = HealthCheckConstants.STATUS_DEGRADED.equals(
        databaseHealth.get(HealthCheckConstants.FIELD_STATUS));
    boolean isSystemDegraded = HealthCheckConstants.STATUS_DEGRADED.equals(
        systemHealth.get(HealthCheckConstants.FIELD_STATUS));

    if ((isDatabaseHealthy || isDatabaseDegraded) && (isSystemHealthy || isSystemDegraded)) {
      return HealthCheckConstants.STATUS_DEGRADED;
    }

    return HealthCheckConstants.STATUS_DOWN;
  }
}
