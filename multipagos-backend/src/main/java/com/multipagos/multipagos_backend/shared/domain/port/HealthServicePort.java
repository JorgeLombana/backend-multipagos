package com.multipagos.multipagos_backend.shared.domain.port;

import java.util.Map;

public interface HealthServicePort {

    Map<String, Object> checkHealth();
}
