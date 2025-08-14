package com.multipagos.multipagos_backend.topup.application.service;

import com.multipagos.multipagos_backend.topup.domain.model.*;
import com.multipagos.multipagos_backend.topup.domain.model.valueobject.*;
import com.multipagos.multipagos_backend.topup.domain.port.in.TopUpServicePort;
import com.multipagos.multipagos_backend.topup.domain.port.out.TopUpPort;
import com.multipagos.multipagos_backend.topup.domain.port.out.TransactionRepositoryPort;
import com.multipagos.multipagos_backend.topup.domain.port.out.SupplierPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Executes mobile top-up transactions with external providers.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TopUpService implements TopUpServicePort {

  private final TopUpPort topUpPort;
  private final SupplierPort supplierPort;
  private final TransactionRepositoryPort transactionRepositoryPort;

  @Override
  @Transactional
  public TransactionDomain executeTopUp(TopUpRequest request, Long userId) {
    log.info("[TOPUP SERVICE] Processing top-up transaction for phone: {} | value: {} | supplier: {} | userId: {}",
        request.getCellPhone(), request.getValue(), request.getSupplierId(), userId);

    if (!validateTopUpRequest(request)) {
      throw new IllegalArgumentException("Invalid top-up request");
    }

    if (userId == null || userId <= 0) {
      throw new IllegalArgumentException("Valid user ID is required");
    }

    TransactionDomain transaction = createPendingTransaction(request, userId);
    TransactionDomain savedTransaction = transactionRepositoryPort.save(transaction);
    log.info("[TOPUP SERVICE] Transaction created with ID: {}", savedTransaction.getId());

    try {
      log.info("[TOPUP SERVICE] Executing top-up with external provider");
      String externalTransactionId = topUpPort.executeTopUp(request);

      savedTransaction.complete(externalTransactionId, "Top-up completed successfully");
      savedTransaction = transactionRepositoryPort.save(savedTransaction);

      log.info("[TOPUP SERVICE] Transaction completed successfully | ID: {} | external ID: {}",
          savedTransaction.getId(), savedTransaction.getExternalTransactionId());

      return savedTransaction;

    } catch (Exception e) {
      log.error("[TOPUP SERVICE] Error processing top-up | transaction ID: {} | error: {}",
          savedTransaction.getId(), e.getMessage(), e);

      savedTransaction.fail(e.getMessage());
      transactionRepositoryPort.save(savedTransaction);

      throw new RuntimeException("Error procesando la recarga: " + e.getMessage());
    }
  }

  @Override
  public boolean validateTopUpRequest(TopUpRequest request) {
    log.info("[TOPUP SERVICE] Validating top-up request");

    if (request == null) {
      return false;
    }

    try {
      PhoneNumber.of(request.getCellPhone());
      Amount.of(request.getValue());
      SupplierId.of(request.getSupplierId());

      return supplierPort.existsById(request.getSupplierId());

    } catch (IllegalArgumentException e) {
      log.warn("[TOPUP SERVICE] Validation failed: {}", e.getMessage());
      return false;
    }
  }

  private TransactionDomain createPendingTransaction(TopUpRequest request, Long userId) {
    String supplierName = supplierPort.findById(request.getSupplierId())
        .map(Supplier::getName)
        .orElse("Desconocido");

    return TransactionDomain.builder()
        .userId(userId)
        .phoneNumber(PhoneNumber.of(request.getCellPhone()))
        .amount(Amount.of(request.getValue()))
        .supplierId(SupplierId.of(request.getSupplierId()))
        .supplierName(supplierName)
        .status(TransactionStatus.PENDING)
        .active(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }
}
