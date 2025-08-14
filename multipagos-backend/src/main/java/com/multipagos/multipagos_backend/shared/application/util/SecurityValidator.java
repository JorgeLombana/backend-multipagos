package com.multipagos.multipagos_backend.shared.application.util;

import com.multipagos.multipagos_backend.shared.domain.validator.BusinessValidator;
import com.multipagos.multipagos_backend.shared.domain.valueobject.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.owasp.encoder.Encode;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Security Validator implementing comprehensive input validation and
 * sanitization
 * Focused ONLY on security threats - NOT business validation
 * Follows Single Responsibility Principle - handles only security validation
 * Thread-safe and stateless component
 */
@Slf4j
@Component
public class SecurityValidator {

  private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
      "(?i).*(union|select|insert|delete|update|drop|create|alter|exec|execute|script|javascript|vbscript|onload|onerror).*");

  private static final Pattern XSS_PATTERN = Pattern.compile(
      "(?i).*(<script|</script|javascript:|vbscript:|onload=|onerror=|onclick=|onmouseover=).*");

  private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
      ".*(\\.\\.[\\\\/]|[\\\\/]\\.\\.[\\\\/]|\\.\\.\\\\|\\.\\./).*");

  public String sanitizeForXSS(String input) {
    if (isNullOrEmpty(input)) {
      return input;
    }

    String sanitized = Encode.forHtml(input.trim());
    log.debug("[SECURITY] Input sanitized for XSS protection");
    return sanitized;
  }

  public SecurityValidationResult validateSecurityThreats(String input, String fieldName) {
    if (input == null) {
      return SecurityValidationResult.valid(input);
    }

    String trimmed = input.trim();

    if (SQL_INJECTION_PATTERN.matcher(trimmed).matches()) {
      String message = String.format("Campo '%s' contiene caracteres no permitidos", fieldName);
      log.warn("[SECURITY] SQL injection attempt detected in field '{}': {}", fieldName, trimmed);
      return SecurityValidationResult.invalid(message);
    }

    if (XSS_PATTERN.matcher(trimmed).matches()) {
      String message = String.format("Campo '%s' contiene código potencialmente peligroso", fieldName);
      log.warn("[SECURITY] XSS attempt detected in field '{}': {}", fieldName, trimmed);
      return SecurityValidationResult.invalid(message);
    }

    if (PATH_TRAVERSAL_PATTERN.matcher(trimmed).matches()) {
      String message = String.format("Campo '%s' contiene una ruta no válida", fieldName);
      log.warn("[SECURITY] Path traversal attempt detected in field '{}': {}", fieldName, trimmed);
      return SecurityValidationResult.invalid(message);
    }

    String sanitized = sanitizeForXSS(trimmed);
    return SecurityValidationResult.valid(sanitized);
  }

  public ValidationResult validateJwtToken(String token) {
    if (isNullOrEmpty(token)) {
      return ValidationResult.invalid("Token de autorización es requerido");
    }

    String[] parts = token.split("\\.");
    if (parts.length != 3) {
      return ValidationResult.invalid("Formato de token no válido");
    }

    return ValidationResult.valid(token);
  }

  public ValidationResult validateEmail(String email) {
    SecurityValidationResult securityResult = validateSecurityThreats(email, "email");
    if (!securityResult.isValid()) {
      return ValidationResult.invalid(securityResult.getErrorMessage());
    }

    ValidationResult businessResult = BusinessValidator.validateEmail(securityResult.getSanitizedValue());
    if (!businessResult.isValid()) {
      return ValidationResult.invalid(businessResult.getErrorMessage());
    }

    return ValidationResult.valid(businessResult.getValue());
  }

  public ValidationResult validateUserName(String name) {
    SecurityValidationResult securityResult = validateSecurityThreats(name, "nombre");
    if (!securityResult.isValid()) {
      return ValidationResult.invalid(securityResult.getErrorMessage());
    }

    ValidationResult businessResult = BusinessValidator.validateUserName(securityResult.getSanitizedValue());
    if (!businessResult.isValid()) {
      return ValidationResult.invalid(businessResult.getErrorMessage());
    }

    return ValidationResult.valid(businessResult.getValue());
  }

  private boolean isNullOrEmpty(String input) {
    return input == null || input.trim().isEmpty();
  }

  /**
   * Specialized validation result for security validation with sanitized values
   */
  public static class SecurityValidationResult {
    private final boolean valid;
    private final String errorMessage;
    private final String sanitizedValue;

    private SecurityValidationResult(boolean valid, String errorMessage, String sanitizedValue) {
      this.valid = valid;
      this.errorMessage = errorMessage;
      this.sanitizedValue = sanitizedValue;
    }

    public static SecurityValidationResult valid(String sanitizedValue) {
      return new SecurityValidationResult(true, null, sanitizedValue);
    }

    public static SecurityValidationResult invalid(String errorMessage) {
      return new SecurityValidationResult(false, errorMessage, null);
    }

    public boolean isValid() {
      return valid;
    }

    public String getErrorMessage() {
      return errorMessage;
    }

    public String getSanitizedValue() {
      return sanitizedValue;
    }
  }
}
