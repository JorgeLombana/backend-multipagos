package com.multipagos.multipagos_backend.topup.presentation.validation;

import com.multipagos.multipagos_backend.topup.domain.port.in.SupplierServicePort;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validator for supplier ID that checks against active suppliers dynamically
 * Follows hexagonal architecture by using domain service port
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ValidSupplierValidator implements ConstraintValidator<ValidSupplier, String> {

  private final SupplierServicePort supplierService;

  @Override
  public void initialize(ValidSupplier constraintAnnotation) {
    // No initialization needed
  }

  @Override
  public boolean isValid(String supplierId, ConstraintValidatorContext context) {
    if (supplierId == null || supplierId.trim().isEmpty()) {
      return false;
    }

    try {
      log.debug("[VALID SUPPLIER VALIDATOR] Validating supplier ID: {}", supplierId);
      boolean isValid = supplierService.isValidSupplier(supplierId);
      log.debug("[VALID SUPPLIER VALIDATOR] Supplier {} is {}", supplierId, isValid ? "valid" : "invalid");
      return isValid;
    } catch (Exception e) {
      log.error("[VALID SUPPLIER VALIDATOR] Error validating supplier {}: {}", supplierId, e.getMessage());
      return false;
    }
  }
}
