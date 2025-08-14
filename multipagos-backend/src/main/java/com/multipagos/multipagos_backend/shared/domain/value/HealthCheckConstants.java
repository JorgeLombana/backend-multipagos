package com.multipagos.multipagos_backend.shared.domain.value;

public final class HealthCheckConstants {

  private HealthCheckConstants() {
  }

  public static final long HEALTH_CHECK_TIMEOUT_MS = 5000L;

  public static final double MEMORY_USAGE_HIGH_THRESHOLD = 80.0;
  public static final double MEMORY_USAGE_CRITICAL_THRESHOLD = 90.0;

  public static final String STATUS_UP = "UP";
  public static final String STATUS_DOWN = "DOWN";
  public static final String STATUS_DEGRADED = "DEGRADED";

  public static final String COMPONENT_DATABASE = "database";
  public static final String COMPONENT_SYSTEM = "system";

  public static final String FIELD_STATUS = "status";
  public static final String FIELD_TIMESTAMP = "timestamp";
  public static final String FIELD_COMPONENTS = "components";
  public static final String FIELD_VERSION = "version";
  public static final String FIELD_DETAILS = "details";
  public static final String FIELD_ERROR = "error";
}
