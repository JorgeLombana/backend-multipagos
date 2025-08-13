package com.multipagos.multipagos_backend.shared.domain.response;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiErrorResponseTest {

    @Test
    void shouldCreateApiErrorResponseWithAllFields() {
        // Given
        String error = "Validation Error";
        String message = "Request validation failed";
        String path = "/api/v1/test";
        String apiVersion = "v1";

        List<ApiErrorResponse.ValidationError> validationErrors = Arrays.asList(
                ApiErrorResponse.ValidationError.builder()
                        .field("email")
                        .rejectedValue("invalid-email")
                        .message("Email format is invalid")
                        .build());

        // When
        ApiErrorResponse response = ApiErrorResponse.builder()
                .error(error)
                .message(message)
                .path(path)
                .apiVersion(apiVersion)
                .validationErrors(validationErrors)
                .build();

        // Then
        assertThat(response.getStatus()).isEqualTo("error");
        assertThat(response.getError()).isEqualTo(error);
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getPath()).isEqualTo(path);
        assertThat(response.getApiVersion()).isEqualTo(apiVersion);
        assertThat(response.getValidationErrors()).hasSize(1);
        assertThat(response.getValidationErrors().get(0).getField()).isEqualTo("email");
        assertThat(response.getTimestamp()).isNotNull();
        assertThat(response.getTimestamp()).isBefore(LocalDateTime.now().plusSeconds(1));
    }

    @Test
    void shouldCreateApiErrorResponseWithDefaultStatus() {
        // When
        ApiErrorResponse response = ApiErrorResponse.builder()
                .error("Test Error")
                .message("Test message")
                .build();

        // Then
        assertThat(response.getStatus()).isEqualTo("error");
    }

    @Test
    void shouldCreateApiErrorResponseWithDefaultTimestamp() {
        // Given
        LocalDateTime beforeCreation = LocalDateTime.now();

        // When
        ApiErrorResponse response = ApiErrorResponse.builder()
                .error("Test Error")
                .message("Test message")
                .build();

        // Then
        LocalDateTime afterCreation = LocalDateTime.now();
        assertThat(response.getTimestamp()).isAfter(beforeCreation.minusSeconds(1));
        assertThat(response.getTimestamp()).isBefore(afterCreation.plusSeconds(1));
    }

    @Test
    void shouldCreateValidationError() {
        // Given
        String field = "username";
        Object rejectedValue = "";
        String message = "Username cannot be empty";

        // When
        ApiErrorResponse.ValidationError validationError = ApiErrorResponse.ValidationError.builder()
                .field(field)
                .rejectedValue(rejectedValue)
                .message(message)
                .build();

        // Then
        assertThat(validationError.getField()).isEqualTo(field);
        assertThat(validationError.getRejectedValue()).isEqualTo(rejectedValue);
        assertThat(validationError.getMessage()).isEqualTo(message);
    }

    @Test
    void shouldCreateApiErrorResponseWithNoArgsConstructor() {
        // When
        ApiErrorResponse response = new ApiErrorResponse();

        // Then
        assertThat(response).isNotNull();
        // Status and timestamp have @Builder.Default so they will be set even with
        // no-args constructor
        assertThat(response.getStatus()).isEqualTo("error");
        assertThat(response.getError()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getPath()).isNull();
        assertThat(response.getApiVersion()).isNull();
        assertThat(response.getValidationErrors()).isNull();
        assertThat(response.getTimestamp()).isNotNull();
    }
}