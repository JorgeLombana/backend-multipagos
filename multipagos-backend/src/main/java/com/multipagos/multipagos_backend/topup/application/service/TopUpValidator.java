package com.multipagos.multipagos_backend.topup.application.service;

import com.multipagos.multipagos_backend.shared.infrastructure.GlobalExceptionHandler.TopUpValidationException;
import com.multipagos.multipagos_backend.topup.domain.model.TopUpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TopUpValidator {

  public void validate(TopUpRequest request) {
    String cellPhoneError = request.getCellPhoneValidationError();
    if (cellPhoneError != null) {
      throw new TopUpValidationException(cellPhoneError);
    }

    String valueError = request.getValueValidationError();
    if (valueError != null) {
      throw new TopUpValidationException(valueError);
    }

    String supplierIdError = request.getSupplierIdValidationError();
    if (supplierIdError != null) {
      throw new TopUpValidationException(supplierIdError);
    }

    log.info("TopUp request validation passed for cellPhone: {}, value: {}, supplierId: {}",
        request.getCellPhone(), request.getValue(), request.getSupplierId());
  }
}
