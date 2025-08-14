package com.multipagos.multipagos_backend.topup.infrastructure.adapter;

import com.multipagos.multipagos_backend.topup.domain.model.AuthToken;
import com.multipagos.multipagos_backend.topup.domain.port.out.AuthenticationPort;
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
public class PuntoredAuthAdapter implements AuthenticationPort {

  private final PuntoredApiProperties apiProperties;
  private final RestTemplate restTemplate;

  @Override
  public AuthToken authenticate(String username, String password) {
    try {
      String url = apiProperties.getBaseUrl() + "/auth";

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("x-api-key", apiProperties.getKey());

      PuntoredAuthRequest request = new PuntoredAuthRequest(
          username != null ? username : apiProperties.getUsername(),
          password != null ? password : apiProperties.getPassword());

      HttpEntity<PuntoredAuthRequest> entity = new HttpEntity<>(request, headers);

      log.info("[PUNTORED AUTH] Authenticating with Puntored API | username: {}", apiProperties.getUsername());
      ResponseEntity<PuntoredAuthResponse> response = restTemplate.exchange(
          url,
          HttpMethod.POST,
          entity,
          PuntoredAuthResponse.class);

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        log.info("[PUNTORED AUTH] Authentication successful | status: {}", response.getStatusCode());
        return new AuthToken(response.getBody().getToken());
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

  @Override
  public boolean isAuthenticationRequired() {
    return true;
  }

  @Override
  public boolean isAuthenticated() {
    return false; // Puntored is stateless - each request needs authentication
  }

  @Override
  public void clearAuthentication() {
    log.debug("[PUNTORED AUTH] Clear authentication called (no-op for stateless service)");
  }
}
