package com.multipagos.multipagos_backend.topup.domain.model.valueobject;

import lombok.Value;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * PhoneNumber Value Object
 * Encapsulates Colombian mobile phone number validation and business rules
 * Immutable and self-validating following DDD principles
 */
@Value
public class PhoneNumber {

  private static final Pattern PHONE_PATTERN = Pattern.compile("^3\\d{9}$");
  private static final int REQUIRED_LENGTH = 10;
  private static final String REQUIRED_PREFIX = "3";

  String value;

  /**
   * Create a PhoneNumber with validation
   * 
   * @param value the phone number string
   * @throws IllegalArgumentException if phone number is invalid
   */
  public PhoneNumber(String value) {
    if (value == null) {
      throw new IllegalArgumentException("El número de teléfono es requerido");
    }

    String cleanValue = value.trim();

    if (cleanValue.isEmpty()) {
      throw new IllegalArgumentException("El número de teléfono no puede estar vacío");
    }

    if (cleanValue.length() != REQUIRED_LENGTH) {
      throw new IllegalArgumentException("El número de teléfono debe tener exactamente 10 dígitos");
    }

    if (!cleanValue.startsWith(REQUIRED_PREFIX)) {
      throw new IllegalArgumentException("El número de teléfono debe empezar con 3");
    }

    if (!PHONE_PATTERN.matcher(cleanValue).matches()) {
      throw new IllegalArgumentException("El número de teléfono debe contener solo dígitos");
    }

    this.value = cleanValue;
  }

  /**
   * Static factory method for creating PhoneNumber
   * 
   * @param value the phone number string
   * @return PhoneNumber instance
   */
  public static PhoneNumber of(String value) {
    return new PhoneNumber(value);
  }

  /**
   * Check if phone number belongs to a specific operator based on number range
   * This is business logic specific to Colombian mobile operators
   */
  public boolean isClaroNumber() {
    return value.startsWith("300") || value.startsWith("301") ||
        value.startsWith("302") || value.startsWith("303") ||
        value.startsWith("304") || value.startsWith("305");
  }

  public boolean isMovistarNumber() {
    return value.startsWith("310") || value.startsWith("311") ||
        value.startsWith("312") || value.startsWith("313") ||
        value.startsWith("314") || value.startsWith("315");
  }

  public boolean isTigoNumber() {
    return value.startsWith("320") || value.startsWith("321") ||
        value.startsWith("322") || value.startsWith("323");
  }

  public boolean isWomNumber() {
    return value.startsWith("330") || value.startsWith("331");
  }

  /**
   * Format for display purposes
   */
  public String toDisplayFormat() {
    return String.format("(%s) %s-%s",
        value.substring(0, 3),
        value.substring(3, 6),
        value.substring(6));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof PhoneNumber))
      return false;
    PhoneNumber that = (PhoneNumber) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return value;
  }
}
