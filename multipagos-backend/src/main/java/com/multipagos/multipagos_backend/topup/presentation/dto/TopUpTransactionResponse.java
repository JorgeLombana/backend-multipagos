package com.multipagos.multipagos_backend.topup.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopUpTransactionResponse {
  private Long id;
  private String cellPhone;
  private BigDecimal value;
  private String supplierName;
  private String status;
  private String transactionalID;
  private String message;
  private LocalDateTime createdAt;
}
