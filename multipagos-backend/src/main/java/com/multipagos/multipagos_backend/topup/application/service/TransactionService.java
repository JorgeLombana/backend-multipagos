package com.multipagos.multipagos_backend.topup.application.service;

import com.multipagos.multipagos_backend.topup.domain.model.Transaction;
import com.multipagos.multipagos_backend.topup.infrastructure.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

  private final TransactionRepository transactionRepository;

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
}
