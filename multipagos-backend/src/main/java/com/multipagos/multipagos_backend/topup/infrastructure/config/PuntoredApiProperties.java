package com.multipagos.multipagos_backend.topup.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "puntored.api")
public class PuntoredApiProperties {
  private String baseUrl;
  private String key;
  private String username;
  private String password;
}
