package com.multipagos.multipagos_backend.shared.domain.valueobject;

/**
 * Shared Value Object for validation results across domains
 * Encapsulates validation outcome with optional error message and validated value
 * 
 * DOMAIN-DRIVEN DESIGN:
 * ✅ Immutable Value Object
 * ✅ Self-validating
 * ✅ Rich domain meaning
 * ✅ Reusable across contexts
 */
public final class ValidationResult {

  private final boolean valid;
  private final String errorMessage;
  private final Object validatedValue;

  private ValidationResult(boolean valid, String errorMessage, Object validatedValue) {
    this.valid = valid;
    this.errorMessage = errorMessage;
    this.validatedValue = validatedValue;
  }

  public static ValidationResult valid() {
    return new ValidationResult(true, null, null);
  }

  public static ValidationResult valid(Object value) {
    return new ValidationResult(true, null, value);
  }

  public static ValidationResult invalid(String message) {
    return new ValidationResult(false, message, null);
  }

  public boolean isValid() {
    return valid;
  }

  public boolean isInvalid() {
    return !valid;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  @SuppressWarnings("unchecked")
  public <T> T getValue() {
    return (T) validatedValue;
  }

  public ValidationResult and(ValidationResult other) {
    if (this.isInvalid()) {
      return this;
    }
    return other;
  }

  public ValidationResult flatMap(java.util.function.Supplier<ValidationResult> supplier) {
    if (isInvalid()) {
      return this;
    }
    return supplier.get();
  }
}
