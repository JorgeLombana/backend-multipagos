package com.multipagos.multipagos_backend.topup.infrastructure.adapter;

import com.multipagos.multipagos_backend.topup.domain.model.PuntoredToken;
import com.multipagos.multipagos_backend.topup.domain.port.PuntoredAuthPort;
import com.multipagos.multipagos_backend.topup.infrastructure.config.PuntoredApiProperties;
import com.multipagos.multipagos_backend.topup.infrastructure.dto.PuntoredAuthRequest;
import com.multipagos.multipagos_backend.topup.infrastructure.dto.PuntoredAuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class PuntoredAuthAdapter implements PuntoredAuthPort {

  private final PuntoredApiProperties apiProperties;
  private final RestTemplate restTemplate;

  @Override
  public PuntoredToken authenticate() {
    try {
      String url = apiProperties.getBaseUrl() + "/auth";

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("x-api-key", apiProperties.getKey());

      PuntoredAuthRequest request = new PuntoredAuthRequest(
          apiProperties.getUsername(),
          apiProperties.getPassword());

      HttpEntity<PuntoredAuthRequest> entity = new HttpEntity<>(request, headers);

      log.info("[PUNTORED AUTH] Authenticating with Puntored API | username: {}", apiProperties.getUsername());
      ResponseEntity<PuntoredAuthResponse> response = restTemplate.exchange(
          url,
          HttpMethod.POST,
          entity,
          PuntoredAuthResponse.class);

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        log.info("[PUNTORED AUTH] Authentication successful | status: {}", response.getStatusCode());
        return new PuntoredToken(response.getBody().getToken());
      } else {
        log.error("[PUNTORED AUTH] Authentication failed | status: {} | body: {}",
            response.getStatusCode(), response.getBody());
        throw new RuntimeException("Error de autenticación: " + response.getStatusCode());
      }
    } catch (RestClientException e) {
      log.error("[PUNTORED AUTH] Error authenticating with Puntored API | error: {}", e.getMessage(), e);
      throw new RuntimeException("Error de autenticación", e);
    }
  }
}
