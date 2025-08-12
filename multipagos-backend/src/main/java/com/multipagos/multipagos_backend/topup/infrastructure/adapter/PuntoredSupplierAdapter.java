package com.multipagos.multipagos_backend.topup.infrastructure.adapter;

import com.multipagos.multipagos_backend.topup.domain.model.Supplier;
import com.multipagos.multipagos_backend.topup.domain.port.PuntoredAuthPort;
import com.multipagos.multipagos_backend.topup.domain.port.SupplierPort;
import com.multipagos.multipagos_backend.topup.infrastructure.config.PuntoredApiProperties;
import com.multipagos.multipagos_backend.topup.infrastructure.dto.SupplierDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PuntoredSupplierAdapter implements SupplierPort {

  private final PuntoredApiProperties apiProperties;
  private final RestTemplate restTemplate;
  private final PuntoredAuthPort authPort;

  @Override
  public List<Supplier> getSuppliers() {
    try {
      var token = authPort.authenticate();
      String url = apiProperties.getBaseUrl() + "/getSuppliers";

      HttpHeaders headers = new HttpHeaders();
      headers.set("authorization", token.getBearerToken());
      HttpEntity<Void> entity = new HttpEntity<>(headers);

      log.info("Getting suppliers from Puntored API");
      ResponseEntity<List<SupplierDto>> response = restTemplate.exchange(
          url,
          HttpMethod.GET,
          entity,
          new ParameterizedTypeReference<List<SupplierDto>>() {
          });

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        log.info("Successfully retrieved {} suppliers", response.getBody().size());
        return response.getBody().stream()
            .map(dto -> new Supplier(dto.getId(), dto.getName()))
            .collect(Collectors.toList());
      } else {
        throw new RuntimeException("Failed to get suppliers: " + response.getStatusCode());
      }
    } catch (RestClientException e) {
      log.error("Error getting suppliers from Puntored API", e);
      throw new RuntimeException("Failed to get suppliers", e);
    }
  }
}
