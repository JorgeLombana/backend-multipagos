package com.multipagos.multipagos_backend.auth.domain.model;

import com.multipagos.multipagos_backend.shared.domain.valueobject.ValidationResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Domain model representing a user entity with business rules and validations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDomain {

  private Long id;
  private String name;
  private String email;
  private String password;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Boolean active;

  private static final Pattern EMAIL_PATTERN = Pattern.compile(
      "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
  private static final int MIN_NAME_LENGTH = 2;
  private static final int MAX_NAME_LENGTH = 100;
  private static final int MIN_PASSWORD_LENGTH = 6;

  public boolean isValidEmail() {
    return email != null && EMAIL_PATTERN.matcher(email).matches();
  }

  public boolean isValidName() {
    return name != null &&
        !name.trim().isEmpty() &&
        name.length() >= MIN_NAME_LENGTH &&
        name.length() <= MAX_NAME_LENGTH;
  }

  public boolean isValidPassword() {
    return password != null && password.length() >= MIN_PASSWORD_LENGTH;
  }

  public boolean isActive() {
    return Boolean.TRUE.equals(this.active);
  }

  public void activate() {
    this.active = true;
    this.updatedAt = LocalDateTime.now();
  }

  public void deactivate() {
    this.active = false;
    this.updatedAt = LocalDateTime.now();
  }

  public void normalizeEmail() {
    if (this.email != null) {
      this.email = this.email.toLowerCase().trim();
    }
  }

  /**
   * Validates all user attributes according to business rules.
   *
   * @return validation result with success status and error message if invalid
   */
  public ValidationResult validate() {
    if (!isValidName()) {
      return ValidationResult.invalid(
          "Nombre debe tener entre " + MIN_NAME_LENGTH + " y " + MAX_NAME_LENGTH + " caracteres");
    }
    if (!isValidEmail()) {
      return ValidationResult.invalid("Formato de email inválido");
    }
    if (!isValidPassword()) {
      return ValidationResult.invalid(
          "Contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres");
    }
    return ValidationResult.valid();
  }

  /**
   * Prepares user instance for registration by normalizing data and setting
   * defaults.
   */
  public void prepareForRegistration() {
    normalizeEmail();
    this.active = true;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }
}
