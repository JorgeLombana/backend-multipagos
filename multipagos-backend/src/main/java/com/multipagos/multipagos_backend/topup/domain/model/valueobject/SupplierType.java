package com.multipagos.multipagos_backend.topup.domain.model.valueobject;

/**
 * SupplierType Enum
 * Centralizes all supplier business rules and mappings
 * Single source of truth for supplier information
 */
public enum SupplierType {

  CLARO("8753", "Claro"),
  MOVISTAR("9773", "Movistar"),
  TIGO("3398", "Tigo"),
  WOM("4689", "WOM");

  private final String id;
  private final String name;

  SupplierType(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  /**
   * Get supplier type by ID
   * 
   * @param id supplier ID
   * @return SupplierType
   * @throws IllegalArgumentException if supplier not found
   */
  public static SupplierType fromId(String id) {
    if (id == null) {
      throw new IllegalArgumentException("Supplier ID cannot be null");
    }

    for (SupplierType type : values()) {
      if (type.id.equals(id)) {
        return type;
      }
    }

    throw new IllegalArgumentException(
        String.format("Invalid supplier ID: %s. Valid IDs are: %s",
            id, getValidIdsString()));
  }

  /**
   * Check if supplier ID is valid
   */
  public static boolean isValidId(String id) {
    if (id == null)
      return false;

    for (SupplierType type : values()) {
      if (type.id.equals(id)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get all valid supplier IDs as array
   */
  public static String[] getValidIds() {
    return java.util.Arrays.stream(values())
        .map(SupplierType::getId)
        .toArray(String[]::new);
  }

  /**
   * Get all valid supplier IDs as formatted string
   */
  public static String getValidIdsString() {
    return java.util.Arrays.stream(values())
        .map(type -> type.getId() + " (" + type.getName() + ")")
        .collect(java.util.stream.Collectors.joining(", "));
  }

  /**
   * Business rule: Check if supplier supports specific phone number ranges
   */
  public boolean supportsPhoneNumber(PhoneNumber phoneNumber) {
    return switch (this) {
      case CLARO -> phoneNumber.isClaroNumber();
      case MOVISTAR -> phoneNumber.isMovistarNumber();
      case TIGO -> phoneNumber.isTigoNumber();
      case WOM -> phoneNumber.isWomNumber();
    };
  }

  /**
   * Business rule: Get recommended supplier for phone number
   */
  public static SupplierType getRecommendedForPhone(PhoneNumber phoneNumber) {
    for (SupplierType type : values()) {
      if (type.supportsPhoneNumber(phoneNumber)) {
        return type;
      }
    }
    // Default to first supplier if no specific match
    return CLARO;
  }

  @Override
  public String toString() {
    return String.format("%s (%s)", name, id);
  }
}
