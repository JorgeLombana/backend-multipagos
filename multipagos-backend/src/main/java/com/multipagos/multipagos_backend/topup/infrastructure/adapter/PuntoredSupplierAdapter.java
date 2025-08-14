package com.multipagos.multipagos_backend.topup.infrastructure.adapter;

import com.multipagos.multipagos_backend.topup.domain.model.Supplier;
import com.multipagos.multipagos_backend.topup.domain.port.out.AuthenticationPort;
import com.multipagos.multipagos_backend.topup.domain.port.out.SupplierPort;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PuntoredSupplierAdapter implements SupplierPort {

  private final PuntoredApiProperties apiProperties;
  private final RestTemplate restTemplate;
  private final AuthenticationPort authPort;

  @Override
  public List<Supplier> getAllSuppliers() {
    try {
      var token = authPort.authenticate(apiProperties.getUsername(), apiProperties.getPassword());
      String url = apiProperties.getBaseUrl() + "/getSuppliers";

      HttpHeaders headers = new HttpHeaders();
      headers.set("authorization", token.toAuthorizationHeader());
      HttpEntity<Void> entity = new HttpEntity<>(headers);

      log.info("[PUNTORED SUPPLIERS] Calling Puntored API | url: {}", url);
      ResponseEntity<List<SupplierDto>> response = restTemplate.exchange(
          url,
          HttpMethod.GET,
          entity,
          new ParameterizedTypeReference<List<SupplierDto>>() {
          });

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        log.info("[PUNTORED SUPPLIERS] Successfully retrieved {} suppliers from API", response.getBody().size());
        return response.getBody().stream()
            .map(dto -> new Supplier(dto.getId(), dto.getName()))
            .collect(Collectors.toList());
      } else {
        log.error("[PUNTORED SUPPLIERS] Failed to get suppliers | status: {}", response.getStatusCode());
        throw new RuntimeException("Error al obtener proveedores: " + response.getStatusCode());
      }
    } catch (RestClientException e) {
      log.error("[PUNTORED SUPPLIERS] Error getting suppliers from Puntored API | error: {}", e.getMessage(), e);
      throw new RuntimeException("Error al obtener proveedores", e);
    }
  }

  @Override
  public List<Supplier> getActiveSuppliers() {
    return getAllSuppliers().stream()
        .filter(supplier -> supplier.isActive())
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Supplier> findById(String id) {
    return getAllSuppliers().stream()
        .filter(supplier -> supplier.getId().equals(id))
        .findFirst();
  }

  @Override
  public boolean existsById(String id) {
    return findById(id).isPresent();
  }

  @Override
  public void refreshSuppliers() {
    // Puntored API fetches suppliers fresh on each call - no caching needed
    log.info("[PUNTORED SUPPLIERS] Suppliers are refreshed on each getAllSuppliers() call");
  }
}
