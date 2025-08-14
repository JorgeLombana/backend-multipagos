package com.multipagos.multipagos_backend.topup.domain.model;

import com.multipagos.multipagos_backend.topup.domain.model.valueobject.Amount;
import com.multipagos.multipagos_backend.topup.domain.model.valueobject.PhoneNumber;
import com.multipagos.multipagos_backend.topup.domain.model.valueobject.SupplierId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * TopUpRequest Domain Model
 * Uses Value Objects to ensure validation and business rules
 * Pure domain model with no infrastructure dependencies
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopUpRequest {

  private PhoneNumber phoneNumber;
  private Amount amount;
  private SupplierId supplierId;

  /**
   * Constructor for backward compatibility with primitive types
   * Automatically creates Value Objects with validation
   */
  public TopUpRequest(String cellPhone, BigDecimal value, String supplierIdValue) {
    this.phoneNumber = PhoneNumber.of(cellPhone);
    this.amount = Amount.of(value);
    this.supplierId = SupplierId.of(supplierIdValue);
  }

  /**
   * Backward compatibility methods for existing code
   */
  public String getCellPhone() {
    return phoneNumber != null ? phoneNumber.getValue() : null;
  }

  public void setCellPhone(String cellPhone) {
    this.phoneNumber = cellPhone != null ? PhoneNumber.of(cellPhone) : null;
  }

  public BigDecimal getValue() {
    return amount != null ? amount.getValue() : null;
  }

  public void setValue(BigDecimal value) {
    this.amount = value != null ? Amount.of(value) : null;
  }

  public String getSupplierId() {
    return supplierId != null ? supplierId.getValue() : null;
  }

  public void setSupplierId(String supplierIdValue) {
    this.supplierId = supplierIdValue != null ? SupplierId.of(supplierIdValue) : null;
  }

  /**
   * Domain validation using Value Objects
   */
  public boolean isValid() {
    return phoneNumber != null && amount != null && supplierId != null;
  }

  /**
   * Business rule: Check if supplier is compatible with phone number
   */
  public boolean isSupplierCompatible() {
    return phoneNumber != null && supplierId != null &&
        supplierId.supportsPhoneNumber(phoneNumber);
  }

  /**
   * Get supplier name for business operations
   */
  public String getSupplierName() {
    return supplierId != null ? supplierId.getSupplierName() : null;
  }

  /**
   * Get Value Object instances directly (not the primitive values)
   */
  public PhoneNumber getPhoneNumberVO() {
    return phoneNumber;
  }

  public Amount getAmountVO() {
    return amount;
  }

  public SupplierId getSupplierIdVO() {
    return supplierId;
  }
}