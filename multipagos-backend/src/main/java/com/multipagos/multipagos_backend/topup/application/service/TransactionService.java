package com.multipagos.multipagos_backend.topup.application.service;

import com.multipagos.multipagos_backend.auth.domain.port.out.UserRepositoryPort;
import com.multipagos.multipagos_backend.shared.domain.value.PageRequest;
import com.multipagos.multipagos_backend.shared.domain.value.PagedResult;
import com.multipagos.multipagos_backend.topup.domain.model.*;
import com.multipagos.multipagos_backend.topup.domain.model.valueobject.PhoneNumber;
import com.multipagos.multipagos_backend.topup.domain.port.in.TransactionServicePort;
import com.multipagos.multipagos_backend.topup.domain.port.out.TransactionRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Manages transaction queries and status updates.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService implements TransactionServicePort {

  private final TransactionRepositoryPort transactionRepository;
  private final UserRepositoryPort userRepository;

  @Override
  @Transactional(readOnly = true)
  public Optional<TransactionDomain> getTransactionById(Long id) {
    log.info("[TRANSACTION SERVICE] Retrieving transaction by ID: {}", id);
    return transactionRepository.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<TransactionDomain> getTransactionByTransactionId(String transactionId) {
    log.info("[TRANSACTION SERVICE] Retrieving transaction by external ID: {}", transactionId);
    return transactionRepository.findByExternalTransactionId(transactionId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TransactionDomain> getTransactionsByUser(Long userId) {
    log.info("[TRANSACTION SERVICE] Retrieving all transactions for user: {}", userId);

    userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

    return transactionRepository.findByUserId(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TransactionDomain> getActiveTransactionsByUser(Long userId) {
    log.info("[TRANSACTION SERVICE] Retrieving active transactions for user: {}", userId);

    userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

    return transactionRepository.findByUserIdAndActiveTrue(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TransactionDomain> getTransactionsByPhone(String phoneNumber) {
    log.info("[TRANSACTION SERVICE] Getting transactions for phone: {}", phoneNumber);

    PhoneNumber validatedPhone = PhoneNumber.of(phoneNumber);

    List<TransactionDomain> transactions = transactionRepository
        .findByPhoneNumberAndActiveTrue(validatedPhone.getValue());

    log.info("[TRANSACTION SERVICE] Retrieved {} transactions for phone: {}",
        transactions.size(), validatedPhone.getValue());

    return transactions;
  }

  @Override
  @Transactional
  public Optional<TransactionDomain> updateTransactionStatus(Long id, TransactionStatus status) {
    log.info("[TRANSACTION SERVICE] Updating transaction {} to status: {}", id, status);
    return transactionRepository.updateStatus(id, status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TransactionDomain> getTransactionHistory(Long userId) {
    log.info("[TRANSACTION SERVICE] Getting transaction history for user: {}", userId);
    return getActiveTransactionsByUser(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public PagedResult<TransactionDomain> getTransactionHistoryPaged(Long userId, PageRequest pageRequest) {
    log.info("[TRANSACTION SERVICE] Getting paginated transaction history for user: {} | page: {} | size: {}", 
        userId, pageRequest.getPage(), pageRequest.getSize());

    userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

    return transactionRepository.findByUserIdAndActiveTrue(userId, pageRequest);
  }
}
