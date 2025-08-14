package com.multipagos.multipagos_backend.shared.application.util;

public final class ByteFormatter {
    
    private ByteFormatter() {
    }
    
    public static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        }
        if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        }
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    public static double calculateMemoryUsagePercentage(long used, long total) {
        if (total == 0) return 0.0;
        return ((double) used / total) * 100.0;
    }
}
