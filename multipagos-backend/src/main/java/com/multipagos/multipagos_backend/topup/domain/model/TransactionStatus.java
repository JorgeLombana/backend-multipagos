package com.multipagos.multipagos_backend.topup.domain.model;

public enum TransactionStatus {
  PENDING("PENDING", "Pendiente"),
  COMPLETED("COMPLETED", "Completada"),
  FAILED("FAILED", "Fallida");

  private final String code;
  private final String displayName;

  TransactionStatus(String code, String displayName) {
    this.code = code;
    this.displayName = displayName;
  }

  public String getCode() {
    return code;
  }

  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String toString() {
    return this.displayName;
  }
}
