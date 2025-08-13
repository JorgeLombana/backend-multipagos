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

  private static final List<String> VALID_SUPPLIER_IDS = Arrays.asList("8753", "9773", "3398", "4689");
  private static final BigDecimal MIN_VALUE = new BigDecimal("1000");
  private static final BigDecimal MAX_VALUE = new BigDecimal("100000");

  public boolean isValid() {
    return isValidCellPhone() && isValidValue() && isValidSupplierId();
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
    return supplierId != null && VALID_SUPPLIER_IDS.contains(supplierId);
  }

  public String getCellPhoneValidationError() {
    if (cellPhone == null || cellPhone.isEmpty()) {
      return "El número de celular es requerido";
    }
    if (cellPhone.length() != 10) {
      return "El número de celular debe tener exactamente 10 dígitos";
    }
    if (!cellPhone.startsWith("3")) {
      return "El número de celular debe empezar con 3";
    }
    if (!cellPhone.matches("\\d{10}")) {
      return "El número de celular debe contener solo dígitos";
    }
    return null;
  }

  public String getValueValidationError() {
    if (value == null) {
      return "El valor es requerido";
    }
    if (value.compareTo(MIN_VALUE) < 0) {
      return "El valor debe ser mayor o igual a 1000";
    }
    if (value.compareTo(MAX_VALUE) > 0) {
      return "El valor debe ser menor o igual a 100000";
    }
    return null;
  }

  public String getSupplierIdValidationError() {
    if (supplierId == null || supplierId.isEmpty()) {
      return "El ID del proveedor es requerido";
    }
    if (!VALID_SUPPLIER_IDS.contains(supplierId)) {
      return "El ID del proveedor debe ser uno de: 8753 (Claro), 9773 (Movistar), 3398 (Tigo), 4689 (ETB)";
    }
    return null;
  }

}
