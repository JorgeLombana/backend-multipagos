package com.multipagos.multipagos_backend.shared.infrastructure;

import com.multipagos.multipagos_backend.shared.application.util.ResponseFactory;
import com.multipagos.multipagos_backend.shared.domain.response.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        log.error("[GLOBAL EXCEPTION] Validation error on path: {}", request.getRequestURI(), ex);

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        return ResponseFactory.validationError(fieldErrors, request.getRequestURI());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        log.error("[GLOBAL EXCEPTION] Invalid request body on path: {}", request.getRequestURI(), ex);

        String message = "Cuerpo de solicitud inválido";
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Required request body is missing")) {
                message = "El cuerpo de la solicitud es requerido";
            } else if (ex.getMessage().contains("JSON parse error")) {
                message = "Formato JSON inválido";
            }
        }

        return ResponseFactory.badRequest(message, request.getRequestURI());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, HttpServletRequest request) {

        log.error("[GLOBAL EXCEPTION] Missing request parameter on path: {}", request.getRequestURI(), ex);
        return ResponseFactory.badRequest("Parámetro requerido faltante: " + ex.getParameterName(),
                request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {

        log.error("[GLOBAL EXCEPTION] Illegal argument error on path: {}", request.getRequestURI(), ex);
        return ResponseFactory.badRequest(ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        log.error("[GLOBAL EXCEPTION] Unexpected error on path: {}", request.getRequestURI(), ex);
        return ResponseFactory.internalServerError(request.getRequestURI());
    }


}