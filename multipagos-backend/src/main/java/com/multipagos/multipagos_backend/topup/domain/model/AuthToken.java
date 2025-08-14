package com.multipagos.multipagos_backend.topup.domain.model;

import lombok.Value;

import java.util.Objects;

/**
 * Pure Domain Value Object for Authentication Token
 * Generic abstraction for any authentication token type
 * No dependencies on specific providers or infrastructure
 * Immutable and self-validating
 */
@Value
public class AuthToken {

  String value;

  /**
   * Create an authentication token with validation
   * 
   * @param value the token value
   * @throws IllegalArgumentException if token is invalid
   */
  public AuthToken(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("Token value cannot be null or empty");
    }
    this.value = value.trim();
  }

  /**
   * Check if token is valid (not null or empty)
   * 
   * @return true if token has a value
   */
  public boolean isValid() {
    return value != null && !value.trim().isEmpty();
  }

  /**
   * Get token formatted for HTTP Authorization header
   * 
   * @return token prefixed with Bearer if not already present
   */
  public String toAuthorizationHeader() {
    if (value.toLowerCase().startsWith("bearer ")) {
      return value;
    }
    return "Bearer " + value;
  }

  /**
   * Get raw token value without any prefix
   * 
   * @return clean token value
   */
  public String getRawValue() {
    if (value.toLowerCase().startsWith("bearer ")) {
      return value.substring(7).trim();
    }
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof AuthToken))
      return false;
    AuthToken authToken = (AuthToken) o;
    return Objects.equals(value, authToken.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "AuthToken{value='***'}"; // Don't expose token in logs
  }
}
