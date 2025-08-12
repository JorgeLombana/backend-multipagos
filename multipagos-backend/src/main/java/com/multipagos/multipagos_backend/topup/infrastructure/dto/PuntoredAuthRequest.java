package com.multipagos.multipagos_backend.topup.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuntoredAuthRequest {
  private String user;
  private String password;
}
