package com.multipagos.multipagos_backend.shared.presentation.exception;

import com.multipagos.multipagos_backend.shared.application.util.ResponseFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global Exception Handler for Security-Focused Error Management
 * - Prevents information leakage
 * - Logs security-relevant exceptions
 * - Provides consistent error responses
 * - Handles common attack vectors
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles validation errors - prevents information leakage
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex, 
                                                 HttpServletRequest request) {
    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
    
    log.warn("[VALIDATION ERROR] URI: {} | IP: {} | Fields: {}", 
             request.getRequestURI(), getClientIP(request), 
             fieldErrors.stream()
               .map(FieldError::getField)
               .collect(Collectors.toList()));
    
    return ResponseFactory.validationError(fieldErrors, request.getRequestURI());
  }

  /**
   * Handles constraint violations
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex, 
                                                    HttpServletRequest request) {
    log.warn("[CONSTRAINT VIOLATION] URI: {} | IP: {} | Message: {}", 
             request.getRequestURI(), getClientIP(request), ex.getMessage());
    
    return ResponseFactory.badRequest("Datos de entrada no válidos", request.getRequestURI());
  }

  /**
   * Handles illegal arguments - often indicates security attacks
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex, 
                                                HttpServletRequest request) {
    String clientIP = getClientIP(request);
    
    // Log as potential security incident
    log.warn("[SECURITY] Illegal argument from IP: {} | URI: {} | Message: {}", 
             clientIP, request.getRequestURI(), ex.getMessage());
    
    return ResponseFactory.badRequest(ex.getMessage(), request.getRequestURI());
  }

  /**
   * Handles SQL exceptions - potential SQL injection attempts
   */
  @ExceptionHandler({SQLException.class, DataAccessException.class})
  public ResponseEntity<?> handleDatabaseErrors(Exception ex, HttpServletRequest request) {
    String clientIP = getClientIP(request);
    
    // Log as potential SQL injection attempt
    log.error("[SECURITY] Database error from IP: {} | URI: {} | Type: {} | Message: {}", 
              clientIP, request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage());
    
    // Don't expose database details to client
    return ResponseFactory.internalServerError(request.getRequestURI());
  }

  /**
   * Handles access denied - authorization failures
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex, 
                                             HttpServletRequest request) {
    String clientIP = getClientIP(request);
    
    log.warn("[SECURITY] Access denied from IP: {} | URI: {} | Message: {}", 
             clientIP, request.getRequestURI(), ex.getMessage());
    
    return ResponseFactory.forbidden("Acceso denegado", request.getRequestURI());
  }

  /**
   * Handles method not allowed - potential scanning attempts
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<?> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, 
                                                 HttpServletRequest request) {
    String clientIP = getClientIP(request);
    
    log.warn("[SECURITY] Method not allowed from IP: {} | URI: {} | Method: {} | Supported: {}", 
             clientIP, request.getRequestURI(), request.getMethod(), ex.getSupportedHttpMethods());
    
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(ResponseFactory.error(HttpStatus.METHOD_NOT_ALLOWED, "Método no permitido", 
                                   "El método HTTP no está permitido para este endpoint", 
                                   request.getRequestURI()).getBody());
  }

  /**
   * Handles unsupported media type - potential attack vectors
   */
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<?> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex, 
                                                     HttpServletRequest request) {
    String clientIP = getClientIP(request);
    
    log.warn("[SECURITY] Unsupported media type from IP: {} | URI: {} | ContentType: {}", 
             clientIP, request.getRequestURI(), request.getContentType());
    
    return ResponseFactory.error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Tipo de contenido no soportado", 
                                 "El tipo de contenido de la solicitud no es compatible", 
                                 request.getRequestURI());
  }

  /**
   * Handles malformed JSON - potential attack attempts
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> handleMalformedJson(HttpMessageNotReadableException ex, 
                                              HttpServletRequest request) {
    String clientIP = getClientIP(request);
    
    log.warn("[SECURITY] Malformed JSON from IP: {} | URI: {} | Message: {}", 
             clientIP, request.getRequestURI(), ex.getMessage());
    
    return ResponseFactory.badRequest("Formato de datos no válido", request.getRequestURI());
  }

  /**
   * Handles type mismatch - often indicates manipulation attempts
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex, 
                                             HttpServletRequest request) {
    String clientIP = getClientIP(request);
    
    log.warn("[SECURITY] Type mismatch from IP: {} | URI: {} | Parameter: {} | Value: {}", 
             clientIP, request.getRequestURI(), ex.getName(), ex.getValue());
    
    return ResponseFactory.badRequest("Tipo de parámetro no válido", request.getRequestURI());
  }

  /**
   * Handles 404 errors - potential scanning/enumeration attempts
   */
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<?> handleNotFound(NoHandlerFoundException ex, 
                                         HttpServletRequest request) {
    String clientIP = getClientIP(request);
    
    log.info("[SECURITY] Not found from IP: {} | URI: {} | Method: {}", 
             clientIP, request.getRequestURI(), request.getMethod());
    
    return ResponseFactory.notFound("Endpoint no encontrado", request.getRequestURI());
  }

  /**
   * Handles all other exceptions - prevents information leakage
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleGenericException(Exception ex, HttpServletRequest request) {
    String clientIP = getClientIP(request);
    
    // Log full details for debugging, but don't expose to client
    log.error("[SECURITY] Unexpected error from IP: {} | URI: {} | Type: {} | Message: {} | Stack: {}", 
              clientIP, request.getRequestURI(), ex.getClass().getSimpleName(), 
              ex.getMessage(), getStackTraceString(ex));
    
    // Generic error message to prevent information leakage
    return ResponseFactory.internalServerError(request.getRequestURI());
  }

  private String getClientIP(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }
    
    String xRealIP = request.getHeader("X-Real-IP");
    if (xRealIP != null && !xRealIP.isEmpty()) {
      return xRealIP;
    }
    
    return request.getRemoteAddr();
  }

  private String getStackTraceString(Exception ex) {
    if (ex.getStackTrace().length > 0) {
      return ex.getStackTrace()[0].toString();
    }
    return "No stack trace available";
  }
}
