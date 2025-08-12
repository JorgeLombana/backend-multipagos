package com.multipagos.multipagos_backend.shared.infrastructure;

import com.multipagos.multipagos_backend.topup.presentation.dto.PuntoredErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(TopUpValidationException.class)
  public ResponseEntity<PuntoredErrorResponse> handleTopUpValidationException(TopUpValidationException ex) {
    log.error("TopUp validation error: {}", ex.getMessage());

    PuntoredErrorResponse errorResponse = PuntoredErrorResponse.builder()
        .message(ex.getMessage())
        .build();

    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
    log.error("Validation error: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Bad Request")
        .message(ex.getMessage())
        .build();

    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
    log.error("Internal server error: {}", ex.getMessage(), ex);

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error("Internal Server Error")
        .message("An unexpected error occurred")
        .build();

    return ResponseEntity.internalServerError().body(errorResponse);
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
  }

  public static class TopUpValidationException extends RuntimeException {
    public TopUpValidationException(String message) {
      super(message);
    }
  }
}
