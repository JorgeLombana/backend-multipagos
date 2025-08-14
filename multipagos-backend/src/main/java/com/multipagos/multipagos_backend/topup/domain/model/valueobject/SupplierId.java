package com.multipagos.multipagos_backend.topup.domain.model.valueobject;

import lombok.Value;

import java.util.Objects;

/**
 * SupplierId Value Object
 * Encapsulates supplier ID validation and business rules
 * Immutable and self-validating following DDD principles
 */
@Value
public class SupplierId {

  String value;
  SupplierType type;

  /**
   * Create a SupplierId with validation
   * 
   * @param value the supplier ID string
   * @throws IllegalArgumentException if supplier ID is invalid
   */
  public SupplierId(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("El ID del proveedor es requerido");
    }

    String cleanValue = value.trim();
    this.type = SupplierType.fromId(cleanValue); // This validates the ID
    this.value = cleanValue;
  }

  /**
   * Static factory method
   */
  public static SupplierId of(String value) {
    return new SupplierId(value);
  }

  /**
   * Create from SupplierType enum
   */
  public static SupplierId of(SupplierType type) {
    return new SupplierId(type.getId());
  }

  /**
   * Business logic methods
   */
  public String getSupplierName() {
    return type.getName();
  }

  public boolean isClaro() {
    return type == SupplierType.CLARO;
  }

  public boolean isMovistar() {
    return type == SupplierType.MOVISTAR;
  }

  public boolean isTigo() {
    return type == SupplierType.TIGO;
  }

  public boolean isWom() {
    return type == SupplierType.WOM;
  }

  /**
   * Check if this supplier supports a specific phone number
   */
  public boolean supportsPhoneNumber(PhoneNumber phoneNumber) {
    return type.supportsPhoneNumber(phoneNumber);
  }

  /**
   * Business validation
   */
  public boolean isCompatibleWith(PhoneNumber phoneNumber) {
    return supportsPhoneNumber(phoneNumber);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof SupplierId))
      return false;
    SupplierId that = (SupplierId) o;
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
