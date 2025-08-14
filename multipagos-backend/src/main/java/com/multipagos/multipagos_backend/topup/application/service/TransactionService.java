package com.multipagos.multipagos_backend.topup.application.service;

import com.multipagos.multipagos_backend.auth.domain.model.User;
import com.multipagos.multipagos_backend.auth.infrastructure.repository.UserRepository;
import com.multipagos.multipagos_backend.topup.domain.model.*;
import com.multipagos.multipagos_backend.topup.domain.port.PuntoredAuthPort;
import com.multipagos.multipagos_backend.topup.domain.port.TopUpPort;
import com.multipagos.multipagos_backend.topup.domain.port.TransactionServicePort;
import com.multipagos.multipagos_backend.topup.infrastructure.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Application service implementing transaction business logic
 * Implements hexagonal architecture port
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService implements TransactionServicePort {

  private final TransactionRepository transactionRepository;
  private final UserRepository userRepository;
  private final TopUpPort topUpPort;
  private final PuntoredAuthPort authPort;

  private static final Map<String, String> SUPPLIER_NAMES = Map.of(
      "8753", "Claro",
      "9773", "Movistar",
      "3398", "Tigo",
      "4689", "WOM");

  @Override
  @Transactional
  public Transaction processTopUpTransaction(TopUpRequest request, Long userId) {
    log.info("[TRANSACTION SERVICE] Processing top-up transaction for user: {} | phone: {} | value: {}",
        userId, request.getCellPhone(), request.getValue());

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

    Transaction transaction = createPendingTransaction(request, user);
    Transaction savedTransaction = transactionRepository.save(transaction);
    log.info("[TRANSACTION SERVICE] Transaction created with ID: {}", savedTransaction.getId());

    try {
      log.info("[TRANSACTION SERVICE] Authenticating with Puntored API");
      PuntoredToken tokenObj = authPort.authenticate();
      String authToken = tokenObj.getBearerToken();

      log.info("[TRANSACTION SERVICE] Processing top-up with external provider");
      TopUpTransaction puntoredResponse = topUpPort.processTopUp(request, authToken);

      updateTransactionWithResponse(savedTransaction, puntoredResponse);
      savedTransaction = transactionRepository.save(savedTransaction);

      log.info("[TRANSACTION SERVICE] Transaction completed successfully | ID: {} | external ID: {}",
          savedTransaction.getId(), savedTransaction.getExternalTransactionId());

      return savedTransaction;

    } catch (Exception e) {
      log.error("[TRANSACTION SERVICE] Error processing top-up | transaction ID: {} | error: {}",
          savedTransaction.getId(), e.getMessage(), e);

      savedTransaction.setStatus(TransactionStatus.FAILED);
      savedTransaction.setResponseMessage(e.getMessage());
      transactionRepository.save(savedTransaction);

      throw new RuntimeException("Error procesando la recarga: " + e.getMessage());
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Transaction> getUserTransactions(Long userId, Pageable pageable) {
    log.info("[TRANSACTION SERVICE] Retrieving transactions for user: {} | page: {} | size: {}",
        userId, pageable.getPageNumber(), pageable.getPageSize());

    userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

    Page<Transaction> transactions = transactionRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId,
        pageable);
    log.info("[TRANSACTION SERVICE] Found {} transactions for user: {}", transactions.getNumberOfElements(), userId);

    return transactions;
  }

  @Override
  @Transactional(readOnly = true)
  public Transaction getTransactionById(Long transactionId, Long userId) {
    log.info("[TRANSACTION SERVICE] Retrieving transaction: {} for user: {}", transactionId, userId);

    Transaction transaction = transactionRepository.findById(transactionId)
        .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada"));

    if (!transaction.getUser().getId().equals(userId)) {
      log.warn("[TRANSACTION SERVICE] Access denied - transaction {} does not belong to user {}",
          transactionId, userId);
      throw new IllegalArgumentException("No tienes acceso a esta transacción");
    }

    if (!transaction.getActive()) {
      throw new IllegalArgumentException("Transacción no disponible");
    }

    return transaction;
  }

  @Transactional(readOnly = true)
  public Page<Transaction> getTransactionsByUser(Long userId, Pageable pageable) {
    log.info("[TRANSACTION SERVICE] Getting transactions for userId: {}", userId);

    if (userId == null || userId <= 0) {
      throw new IllegalArgumentException("ID de usuario requerido");
    }

    Page<Transaction> transactions = transactionRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId,
        pageable);

    log.info("[TRANSACTION SERVICE] Retrieved {} transactions for userId: {}",
        transactions.getContent().size(), userId);

    return transactions;
  }

  @Transactional(readOnly = true)
  public List<Transaction> getTransactionsByPhone(String phoneNumber) {
    log.info("[TRANSACTION SERVICE] Getting transactions for phone: {}", phoneNumber);

    if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
      throw new IllegalArgumentException("Número de teléfono requerido");
    }

    if (!phoneNumber.matches("^3\\d{9}$")) {
      throw new IllegalArgumentException("Número de teléfono debe iniciar en 3 y tener 10 dígitos");
    }

    List<Transaction> transactions = transactionRepository
        .findByPhoneNumberAndActiveTrueOrderByCreatedAtDesc(phoneNumber);

    log.info("[TRANSACTION SERVICE] Retrieved {} transactions for phone: {}",
        transactions.size(), phoneNumber);

    return transactions;
  }

  private Transaction createPendingTransaction(TopUpRequest request, User user) {
    Transaction transaction = new Transaction();
    transaction.setUser(user);
    transaction.setPhoneNumber(request.getCellPhone());
    transaction.setAmount(request.getValue());
    transaction.setSupplierId(request.getSupplierId());
    transaction.setSupplierName(SUPPLIER_NAMES.getOrDefault(request.getSupplierId(), "Desconocido"));
    transaction.setStatus(TransactionStatus.PENDING);
    return transaction;
  }

  private void updateTransactionWithResponse(Transaction transaction, TopUpTransaction response) {
    boolean isSuccessful = response.getStatus() == TopUpTransaction.TransactionStatus.COMPLETED ||
        (response.getMessage() != null && response.getMessage().contains("exitosa"));

    if (isSuccessful) {
      transaction.setStatus(TransactionStatus.COMPLETED);
      transaction.setExternalTransactionId(response.getTransactionalID());
      transaction.setResponseMessage(response.getMessage());
    } else {
      transaction.setStatus(TransactionStatus.FAILED);
      transaction.setResponseMessage(response.getMessage() != null ? response.getMessage() : "Error en la recarga");
    }

    transaction.setResponseData(response.toString());
  }
}
