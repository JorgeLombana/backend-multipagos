package com.multipagos.multipagos_backend.topup.domain.model;

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
public class TopUpTransaction {
  private String id;
  private String cellPhone;
  private BigDecimal value;
  private String supplierId;
  private String supplierName;
  private TransactionStatus status;
  private String transactionalID;
  private String message;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String errorMessage;

  public enum TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED,
    CANCELLED
  }
}
