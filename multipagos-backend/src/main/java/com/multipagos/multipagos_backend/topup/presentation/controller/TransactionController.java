package com.multipagos.multipagos_backend.topup.presentation.controller;

import com.multipagos.multipagos_backend.shared.application.util.ResponseFactory;
import com.multipagos.multipagos_backend.topup.application.service.TransactionService;
import com.multipagos.multipagos_backend.topup.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/transactions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Validated
public class TransactionController {

  private final TransactionService transactionService;

  @GetMapping("/user/{userId}")
  public ResponseEntity<?> getTransactionsByUser(
      @PathVariable @Min(value = 1, message = "ID de usuario debe ser mayor a 0") Long userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      HttpServletRequest request) {
    try {
      log.info("[TRANSACTION GET USER] Getting transactions for userId: {} | page: {} | size: {}",
          userId, page, size);

      Pageable pageable = PageRequest.of(page, size);
      Page<Transaction> transactions = transactionService.getTransactionsByUser(userId, pageable);

      log.info("[TRANSACTION GET USER SUCCESS] Retrieved {} transactions for userId: {}",
          transactions.getContent().size(), userId);

      return ResponseFactory.success(transactions, "Transacciones obtenidas exitosamente");

    } catch (IllegalArgumentException e) {
      log.error("[TRANSACTION GET USER VALIDATION ERROR] Validation failed for userId: {} | error: {}",
          userId, e.getMessage());
      return ResponseFactory.badRequest(e.getMessage(), request.getRequestURI());
    } catch (Exception e) {
      log.error("[TRANSACTION GET USER ERROR] Unexpected error getting transactions for userId: {} | error: {}",
          userId, e.getMessage(), e);
      return ResponseFactory.internalServerError(request.getRequestURI());
    }
  }

  @GetMapping("/phone/{phoneNumber}")
  public ResponseEntity<?> getTransactionsByPhone(
      @PathVariable @Pattern(regexp = "^3\\d{9}$", message = "Teléfono debe iniciar en 3 y tener 10 dígitos") String phoneNumber,
      HttpServletRequest request) {
    try {
      log.info("[TRANSACTION GET PHONE] Getting transactions for phone: {}", phoneNumber);

      List<Transaction> transactions = transactionService.getTransactionsByPhone(phoneNumber);

      log.info("[TRANSACTION GET PHONE SUCCESS] Retrieved {} transactions for phone: {}",
          transactions.size(), phoneNumber);

      return ResponseFactory.success(transactions, "Transacciones por teléfono obtenidas exitosamente");

    } catch (IllegalArgumentException e) {
      log.error("[TRANSACTION GET PHONE VALIDATION ERROR] Validation failed for phone: {} | error: {}",
          phoneNumber, e.getMessage());
      return ResponseFactory.badRequest(e.getMessage(), request.getRequestURI());
    } catch (Exception e) {
      log.error("[TRANSACTION GET PHONE ERROR] Unexpected error getting transactions for phone: {} | error: {}",
          phoneNumber, e.getMessage(), e);
      return ResponseFactory.internalServerError(request.getRequestURI());
    }
  }
}
