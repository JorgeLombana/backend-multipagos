package com.multipagos.multipagos_backend.shared.application.service.health;

import com.multipagos.multipagos_backend.shared.application.util.ByteFormatter;
import com.multipagos.multipagos_backend.shared.domain.value.HealthCheckConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * System resources health checker
 * Follows Single Responsibility Principle - only checks system resources
 */
@Component
@Slf4j
public class SystemHealthChecker {
    
    public Map<String, Object> checkSystemResources() {
        Map<String, Object> systemHealth = new HashMap<>();
        
        try {
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            long maxMemory = runtime.maxMemory();
            
            double memoryUsagePercentage = ByteFormatter.calculateMemoryUsagePercentage(usedMemory, maxMemory);
            String systemStatus = determineSystemStatus(memoryUsagePercentage);
            
            systemHealth.put(HealthCheckConstants.FIELD_STATUS, systemStatus);
            systemHealth.put(HealthCheckConstants.FIELD_DETAILS, buildSystemDetails(
                runtime, totalMemory, freeMemory, usedMemory, maxMemory, memoryUsagePercentage));
            
            log.debug("[SYSTEM HEALTH] Check completed | status: {} | memory usage: {:.1f}%", 
                     systemStatus, memoryUsagePercentage);
            
        } catch (Exception e) {
            log.error("[SYSTEM HEALTH] Check failed: {}", e.getMessage());
            
            systemHealth.put(HealthCheckConstants.FIELD_STATUS, HealthCheckConstants.STATUS_DOWN);
            systemHealth.put(HealthCheckConstants.FIELD_ERROR, "System resources check failed");
            systemHealth.put(HealthCheckConstants.FIELD_DETAILS, 
                Map.of("errorMessage", e.getMessage()));
        }
        
        return systemHealth;
    }
    
    private String determineSystemStatus(double memoryUsagePercentage) {
        if (memoryUsagePercentage > HealthCheckConstants.MEMORY_USAGE_CRITICAL_THRESHOLD) {
            return HealthCheckConstants.STATUS_DOWN;
        } else if (memoryUsagePercentage > HealthCheckConstants.MEMORY_USAGE_HIGH_THRESHOLD) {
            return HealthCheckConstants.STATUS_DEGRADED;
        }
        return HealthCheckConstants.STATUS_UP;
    }
    
    private Map<String, Object> buildSystemDetails(Runtime runtime, long totalMemory, 
                                                  long freeMemory, long usedMemory, 
                                                  long maxMemory, double memoryUsagePercentage) {
        return Map.of(
            "memory", Map.of(
                "total", ByteFormatter.formatBytes(totalMemory),
                "used", ByteFormatter.formatBytes(usedMemory),
                "free", ByteFormatter.formatBytes(freeMemory),
                "max", ByteFormatter.formatBytes(maxMemory),
                "usagePercentage", String.format("%.1f%%", memoryUsagePercentage)
            ),
            "processors", runtime.availableProcessors(),
            "uptime", System.currentTimeMillis()
        );
    }
}
