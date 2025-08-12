package com.multipagos.multipagos_backend.topup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopUpRequest {
  private String cellPhone;
  private BigDecimal value;
  private String supplierId;
  private List<String> validSupplierIds = VALID_SUPPLIER_IDS;

  private static final List<String> VALID_SUPPLIER_IDS = Arrays.asList("8753", "9773", "3398", "4689");
  private static final BigDecimal MIN_VALUE = new BigDecimal("1000");
  private static final BigDecimal MAX_VALUE = new BigDecimal("100000");

  public boolean isValid() {
    return isValidCellPhone() && isValidValue() && isValidSupplierId();
  }

  public void setValidSupplierIds(List<String> validSupplierIds) {
    this.validSupplierIds = validSupplierIds;
  }

  private boolean isValidCellPhone() {
    return cellPhone != null
        && cellPhone.length() == 10
        && cellPhone.startsWith("3")
        && cellPhone.matches("\\d{10}");
  }

  private boolean isValidValue() {
    return value != null
        && value.compareTo(MIN_VALUE) >= 0
        && value.compareTo(MAX_VALUE) <= 0;
  }

  private boolean isValidSupplierId() {
    return supplierId != null && validSupplierIds != null && validSupplierIds.contains(supplierId);
  }

  public String getCellPhoneValidationError() {
    if (cellPhone == null || cellPhone.length() != 10) {
      return "\"cellPhone\" length must be 10 characters long";
    }
    if (!cellPhone.startsWith("3") || !cellPhone.matches("\\d{10}")) {
      return "\"cellPhone\" must start with 3 and contain only numbers";
    }
    return null;
  }

  public String getValueValidationError() {
    if (value == null || value.compareTo(MIN_VALUE) < 0) {
      return "\"value\" must be greater than or equal to " + MIN_VALUE;
    }
    if (value.compareTo(MAX_VALUE) > 0) {
      return "\"value\" must be less than or equal to " + MAX_VALUE;
    }
    return null;
  }

  public String getSupplierIdValidationError() {
    if (!isValidSupplierId()) {
      return "\"supplierId\" must be one of " + (validSupplierIds != null ? validSupplierIds.toString() : "[]");
    }
    return null;
  }
}
