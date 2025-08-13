package com.multipagos.multipagos_backend.topup.infrastructure.adapter;

import com.multipagos.multipagos_backend.topup.domain.model.TopUpRequest;
import com.multipagos.multipagos_backend.topup.domain.model.TopUpTransaction;
import com.multipagos.multipagos_backend.topup.domain.port.TopUpPort;
import com.multipagos.multipagos_backend.topup.infrastructure.config.PuntoredApiProperties;
import com.multipagos.multipagos_backend.topup.infrastructure.dto.PuntoredBuyRequest;
import com.multipagos.multipagos_backend.topup.infrastructure.dto.PuntoredBuyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PuntoredTopUpAdapter implements TopUpPort {

  private final RestTemplate restTemplate;
  private final PuntoredApiProperties apiProperties;

  @Override
  public TopUpTransaction processTopUp(TopUpRequest request, String authToken) {
    try {
      log.info(
          "[PUNTORED BUY] Calling Puntored API for top-up | cellPhone: {} | value: {} | supplierId: {} | supplier: {}",
          request.getCellPhone(), request.getValue(), request.getSupplierId(),
          getSupplierName(request.getSupplierId()));

      PuntoredBuyRequest puntoredRequest = PuntoredBuyRequest.builder()
          .cellPhone(request.getCellPhone())
          .value(request.getValue())
          .supplierId(request.getSupplierId())
          .build();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("Authorization", authToken);

      HttpEntity<PuntoredBuyRequest> entity = new HttpEntity<>(puntoredRequest, headers);

      String url = apiProperties.getBaseUrl() + "/buy";
      log.info("[PUNTORED BUY] Making API call | url: {}", url);

      ResponseEntity<PuntoredBuyResponse> response = restTemplate.exchange(
          url, HttpMethod.POST, entity, PuntoredBuyResponse.class);

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        PuntoredBuyResponse puntoredResponse = response.getBody();

        log.info("[PUNTORED BUY] API call successful | transactionId: {} | message: {}",
            puntoredResponse.getTransactionalID(), puntoredResponse.getMessage());

        return TopUpTransaction.builder()
            .id(UUID.randomUUID().toString())
            .cellPhone(puntoredResponse.getCellPhone())
            .value(puntoredResponse.getValue())
            .supplierId(request.getSupplierId())
            .supplierName(getSupplierName(request.getSupplierId()))
            .status(TopUpTransaction.TransactionStatus.COMPLETED)
            .transactionalID(puntoredResponse.getTransactionalID())
            .message(puntoredResponse.getMessage())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
      } else {
        log.error("[PUNTORED BUY] API call failed | status: {} | body: {}",
            response.getStatusCode(), response.getBody());
        return createFailedTransaction(request, "Error en llamada a API externa");
      }

    } catch (Exception e) {
      log.error("[PUNTORED BUY] Error calling Puntored API | cellPhone: {} | error: {}",
          request.getCellPhone(), e.getMessage(), e);
      return createFailedTransaction(request, "Error del servicio externo: " + e.getMessage());
    }
  }

  private TopUpTransaction createFailedTransaction(TopUpRequest request, String errorMessage) {
    log.warn("[PUNTORED BUY] Creating failed transaction | cellPhone: {} | supplier: {} | error: {}",
        request.getCellPhone(), getSupplierName(request.getSupplierId()), errorMessage);

    return TopUpTransaction.builder()
        .id(UUID.randomUUID().toString())
        .cellPhone(request.getCellPhone())
        .value(request.getValue())
        .supplierId(request.getSupplierId())
        .supplierName(getSupplierName(request.getSupplierId()))
        .status(TopUpTransaction.TransactionStatus.FAILED)
        .message("Transacción fallida")
        .errorMessage(errorMessage)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  private String getSupplierName(String supplierId) {
    return switch (supplierId) {
      case "8753" -> "Claro";
      case "9773" -> "Movistar";
      case "3398" -> "Tigo";
      case "4689" -> "ETB";
      default -> "Desconocido";
    };
  }
}