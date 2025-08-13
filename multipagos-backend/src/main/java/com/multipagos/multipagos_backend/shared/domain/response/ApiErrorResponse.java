package com.multipagos.multipagos_backend.shared.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard API error response for all error scenarios.
 * Provides consistent error structure across all endpoints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

    /**
     * Response status - always "error" for error responses
     */
    @Builder.Default
    private String status = "error";

    /**
     * Error type or category
     */
    private String error;

    /**
     * Human-readable error message
     */
    private String message;

    /**
     * Request path where the error occurred
     */
    private String path;

    /**
     * Timestamp when the error occurred
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * API version information
     */
    private String apiVersion;

    /**
     * Optional list of validation errors for detailed field-level errors
     */
    private List<ValidationError> validationErrors;

    /**
     * Represents a single validation error for a specific field
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        /**
         * The field name that failed validation
         */
        private String field;

        /**
         * The rejected value
         */
        private Object rejectedValue;

        /**
         * The validation error message
         */
        private String message;
    }
}