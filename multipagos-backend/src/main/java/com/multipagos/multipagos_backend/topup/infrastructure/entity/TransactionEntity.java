package com.multipagos.multipagos_backend.topup.infrastructure.entity;

import com.multipagos.multipagos_backend.auth.infrastructure.entity.UserEntity;
import com.multipagos.multipagos_backend.shared.infrastructure.entity.BaseEntity;
import com.multipagos.multipagos_backend.topup.domain.model.TransactionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * JPA Entity for Transaction - contains only infrastructure concerns
 * Separate from TransactionDomain to maintain clean domain layer
 */
@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_transaction_user", columnList = "user_id"),
    @Index(name = "idx_transaction_phone", columnList = "phone_number"),
    @Index(name = "idx_transaction_date", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransactionEntity extends BaseEntity {

  @NotNull(message = "Usuario requerido")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @NotBlank(message = "Número de teléfono requerido")
  @Pattern(regexp = "^3\\d{9}$", message = "Teléfono debe iniciar en 3 y tener 10 dígitos")
  @Column(name = "phone_number", nullable = false, length = 10)
  private String phoneNumber;

  @NotNull(message = "Valor requerido")
  @DecimalMin(value = "1000", message = "Valor mínimo: $1.000")
  @DecimalMax(value = "100000", message = "Valor máximo: $100.000")
  @Column(name = "amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @NotBlank(message = "Proveedor requerido")
  @Column(name = "supplier_id", nullable = false, length = 10)
  private String supplierId;

  @NotBlank(message = "Nombre del proveedor requerido")
  @Column(name = "supplier_name", nullable = false, length = 50)
  private String supplierName;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private TransactionStatus status = TransactionStatus.PENDING;

  @Column(name = "external_transaction_id", length = 100)
  private String externalTransactionId;

  @Column(name = "response_message", length = 255)
  private String responseMessage;

  @Column(name = "response_data", columnDefinition = "TEXT")
  private String responseData;
}
