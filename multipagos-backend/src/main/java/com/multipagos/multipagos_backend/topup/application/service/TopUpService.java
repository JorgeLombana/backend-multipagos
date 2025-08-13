package com.multipagos.multipagos_backend.topup.application.service;

import com.multipagos.multipagos_backend.topup.domain.model.PuntoredToken;
import com.multipagos.multipagos_backend.topup.domain.model.TopUpRequest;
import com.multipagos.multipagos_backend.topup.domain.model.TopUpTransaction;
import com.multipagos.multipagos_backend.topup.domain.port.PuntoredAuthPort;
import com.multipagos.multipagos_backend.topup.domain.port.TopUpPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopUpService {

  private final TopUpPort topUpPort;
  private final PuntoredAuthPort authPort;

  public TopUpTransaction processTopUp(TopUpRequest request) {
    log.info("[TOP-UP SERVICE] Processing top-up for cellPhone: {}, value: {}, supplierId: {}",
        request.getCellPhone(), request.getValue(), request.getSupplierId());

    if (!request.isValid()) {
      log.error("[TOP-UP SERVICE] Request validation failed for cellPhone: {}", request.getCellPhone());
      throw new IllegalArgumentException("Solicitud de recarga inválida");
    }
    log.info("[TOP-UP SERVICE] Request validation passed");

    log.info("[TOP-UP SERVICE] Starting authentication process");
    PuntoredToken tokenObj = authPort.authenticate();
    String authToken = tokenObj.getBearerToken();
    log.info("[TOP-UP SERVICE] Authentication successful, proceeding with top-up");

    log.info("[TOP-UP SERVICE] Calling top-up port to process transaction");
    TopUpTransaction result = topUpPort.processTopUp(request, authToken);

    log.info("[TOP-UP SERVICE] Top-up processed successfully with transactionId: {}", result.getTransactionalID());
    return result;
  }
}