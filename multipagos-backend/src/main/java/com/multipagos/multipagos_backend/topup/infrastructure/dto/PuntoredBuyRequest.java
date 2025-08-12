package com.multipagos.multipagos_backend.topup.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PuntoredBuyRequest {
  private String cellPhone;
  private BigDecimal value;
  private String supplierId;
}
