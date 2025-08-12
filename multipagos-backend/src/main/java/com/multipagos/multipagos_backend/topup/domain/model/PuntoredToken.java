package com.multipagos.multipagos_backend.topup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuntoredToken {
  private String token;

  public String getBearerToken() {
    return token;
  }
}
