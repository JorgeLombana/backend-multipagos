package com.multipagos.multipagos_backend.topup.infrastructure.adapter;

import com.multipagos.multipagos_backend.topup.domain.model.TopUpRequest;
import com.multipagos.multipagos_backend.topup.domain.port.out.TopUpPort;
import com.multipagos.multipagos_backend.topup.domain.port.out.AuthenticationPort;
import com.multipagos.multipagos_backend.topup.infrastructure.config.PuntoredApiProperties;
import com.multipagos.multipagos_backend.topup.infrastructure.dto.PuntoredBuyRequest;
import com.multipagos.multipagos_backend.topup.infrastructure.dto.PuntoredBuyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class PuntoredTopUpAdapter implements TopUpPort {

  private final RestTemplate restTemplate;
  private final PuntoredApiProperties apiProperties;
  private final AuthenticationPort authenticationPort;

  @Override
  public String executeTopUp(TopUpRequest request) {
    try {
      log.info(
          "[PUNTORED BUY] Calling Puntored API for top-up | cellPhone: {} | value: {} | supplierId: {}",
          request.getCellPhone(), request.getValue(), request.getSupplierId());

      var authToken = authenticationPort.authenticate(
          apiProperties.getUsername(), apiProperties.getPassword());

      PuntoredBuyRequest puntoredRequest = PuntoredBuyRequest.builder()
          .cellPhone(request.getCellPhone())
          .value(request.getValue())
          .supplierId(request.getSupplierId())
          .build();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("Authorization", authToken.toAuthorizationHeader());

      HttpEntity<PuntoredBuyRequest> entity = new HttpEntity<>(puntoredRequest, headers);

      String url = apiProperties.getBaseUrl() + "/buy";
      log.info("[PUNTORED BUY] Making API call | url: {}", url);

      ResponseEntity<PuntoredBuyResponse> response = restTemplate.exchange(
          url, HttpMethod.POST, entity, PuntoredBuyResponse.class);

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        PuntoredBuyResponse puntoredResponse = response.getBody();

        log.info("[PUNTORED BUY] API call successful | transactionId: {} | message: {}",
            puntoredResponse.getTransactionalID(), puntoredResponse.getMessage());

        return puntoredResponse.getTransactionalID();
      } else {
        log.error("[PUNTORED BUY] API call failed | status: {} | body: {}",
            response.getStatusCode(), response.getBody());
        throw new RuntimeException("Error en llamada a API externa");
      }

    } catch (Exception e) {
      log.error("[PUNTORED BUY] Error calling Puntored API | cellPhone: {} | error: {}",
          request.getCellPhone(), e.getMessage(), e);
      throw new RuntimeException("Error del servicio externo: " + e.getMessage(), e);
    }
  }

  @Override
  public boolean isServiceAvailable() {
    return true;
  }

  @Override
  public String getProviderName() {
    return "Puntored";
  }
}