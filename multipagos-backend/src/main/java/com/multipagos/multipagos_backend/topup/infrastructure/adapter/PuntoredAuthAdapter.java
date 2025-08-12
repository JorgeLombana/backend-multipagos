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

      log.info("Authenticating with Puntored API");
      ResponseEntity<PuntoredAuthResponse> response = restTemplate.exchange(
          url,
          HttpMethod.POST,
          entity,
          PuntoredAuthResponse.class);

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        log.info("Authentication successful");
        return new PuntoredToken(response.getBody().getToken());
      } else {
        throw new RuntimeException("Authentication failed: " + response.getStatusCode());
      }
    } catch (RestClientException e) {
      log.error("Error authenticating with Puntored API", e);
      throw new RuntimeException("Authentication failed", e);
    }
  }
}
