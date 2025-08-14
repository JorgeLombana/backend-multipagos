package com.multipagos.multipagos_backend.topup.domain.model;

import com.multipagos.multipagos_backend.topup.domain.model.valueobject.Amount;
import com.multipagos.multipagos_backend.topup.domain.model.valueobject.PhoneNumber;
import com.multipagos.multipagos_backend.topup.domain.model.valueobject.SupplierId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pure Domain Model for Transaction
 * Now uses Value Objects for proper domain modeling
 * No JPA annotations, no framework dependencies
 * Contains only business logic and domain rules
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDomain {

  private Long id;
  private Long userId;
  private PhoneNumber phoneNumber;
  private Amount amount;
  private SupplierId supplierId;
  private String supplierName;
  private TransactionStatus status;
  private String externalTransactionId;
  private String responseMessage;
  private String responseData;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Boolean active;

  /**
   * Constructor for backward compatibility with primitive types
   */
  public TransactionDomain(Long id, Long userId, String phoneNumberValue,
      BigDecimal amountValue, String supplierIdValue,
      String supplierName, TransactionStatus status) {
    this.id = id;
    this.userId = userId;
    this.phoneNumber = phoneNumberValue != null ? PhoneNumber.of(phoneNumberValue) : null;
    this.amount = amountValue != null ? Amount.of(amountValue) : null;
    this.supplierId = supplierIdValue != null ? SupplierId.of(supplierIdValue) : null;
    this.supplierName = supplierName;
    this.status = status;
    this.active = true;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Backward compatibility methods for existing code
   */
  public String getPhoneNumberValue() {
    return phoneNumber != null ? phoneNumber.getValue() : null;
  }

  public void setPhoneNumberValue(String phoneNumberValue) {
    this.phoneNumber = phoneNumberValue != null ? PhoneNumber.of(phoneNumberValue) : null;
  }

  public BigDecimal getAmountValue() {
    return amount != null ? amount.getValue() : null;
  }

  public void setAmountValue(BigDecimal amountValue) {
    this.amount = amountValue != null ? Amount.of(amountValue) : null;
  }

  public String getSupplierIdValue() {
    return supplierId != null ? supplierId.getValue() : null;
  }

  public void setSupplierIdValue(String supplierIdValue) {
    this.supplierId = supplierIdValue != null ? SupplierId.of(supplierIdValue) : null;
    if (this.supplierId != null && this.supplierName == null) {
      this.supplierName = this.supplierId.getSupplierName();
    }
  }

  /**
   * Domain business logic: check if transaction is successful
   */
  public boolean isSuccessful() {
    return TransactionStatus.COMPLETED.equals(this.status);
  }

  /**
   * Domain business logic: check if transaction failed
   */
  public boolean isFailed() {
    return TransactionStatus.FAILED.equals(this.status);
  }

  /**
   * Domain business logic: check if transaction is pending
   */
  public boolean isPending() {
    return TransactionStatus.PENDING.equals(this.status);
  }

  /**
   * Domain business logic: complete transaction
   */
  public void complete(String externalTransactionId, String message) {
    if (!canBeModified()) {
      throw new IllegalStateException("Cannot complete a transaction that is not pending");
    }

    this.status = TransactionStatus.COMPLETED;
    this.externalTransactionId = externalTransactionId;
    this.responseMessage = message;
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Domain business logic: fail transaction
   */
  public void fail(String errorMessage) {
    if (!canBeModified()) {
      throw new IllegalStateException("Cannot fail a transaction that is not pending");
    }

    this.status = TransactionStatus.FAILED;
    this.responseMessage = errorMessage;
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Domain business rule: check if transaction can be modified
   */
  public boolean canBeModified() {
    return TransactionStatus.PENDING.equals(this.status);
  }

  /**
   * Domain business rule: check if transaction is active
   */
  public boolean isActive() {
    return Boolean.TRUE.equals(this.active);
  }

  /**
   * Domain business logic: soft delete transaction
   */
  public void deactivate() {
    this.active = false;
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Domain business logic: reactivate transaction
   */
  public void reactivate() {
    this.active = true;
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Business rule: Check if transaction belongs to user
   */
  public boolean belongsToUser(Long userId) {
    return this.userId != null && this.userId.equals(userId);
  }

  /**
   * Business rule: Check if supplier is compatible with phone number
   */
  public boolean isSupplierCompatible() {
    return phoneNumber != null && supplierId != null &&
        supplierId.supportsPhoneNumber(phoneNumber);
  }

  /**
   * Get formatted amount for display
   */
  public String getFormattedAmount() {
    return amount != null ? amount.toCurrencyString() : "N/A";
  }

  /**
   * Get formatted phone number for display
   */
  public String getFormattedPhoneNumber() {
    return phoneNumber != null ? phoneNumber.toDisplayFormat() : "N/A";
  }

  /**
   * Get supplier name from supplier ID if not already set
   */
  public String getSupplierNameOrDerived() {
    if (supplierName != null && !supplierName.trim().isEmpty()) {
      return supplierName;
    }

    return supplierId != null ? supplierId.getSupplierName() : "Desconocido";
  }

  /**
   * Create a pending transaction from a TopUpRequest
   */
  public static TransactionDomain createPending(TopUpRequest request, Long userId) {
    if (request == null || userId == null) {
      throw new IllegalArgumentException("Request and user ID are required");
    }

    return TransactionDomain.builder()
        .userId(userId)
        .phoneNumber(request.getPhoneNumberVO())
        .amount(request.getAmountVO())
        .supplierId(request.getSupplierIdVO())
        .supplierName(request.getSupplierName())
        .status(TransactionStatus.PENDING)
        .active(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  /**
   * Update transaction with external response
   */
  public void updateWithExternalResponse(String externalId, String message, boolean successful) {
    if (!canBeModified()) {
      throw new IllegalStateException("Cannot update a transaction that is not pending");
    }

    if (successful) {
      complete(externalId, message);
    } else {
      fail(message != null ? message : "Error en servicio externo");
    }
  }
}
