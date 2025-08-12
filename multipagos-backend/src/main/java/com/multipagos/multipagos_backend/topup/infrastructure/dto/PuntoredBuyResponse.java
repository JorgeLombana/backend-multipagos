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
public class PuntoredBuyResponse {
  private String message;
  private String transactionalID;
  private String cellPhone;
  private BigDecimal value;
}
