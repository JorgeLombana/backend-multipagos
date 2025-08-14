package com.multipagos.multipagos_backend.topup.presentation.controller;

import com.multipagos.multipagos_backend.shared.application.service.JwtService;
import com.multipagos.multipagos_backend.shared.application.util.ResponseFactory;
import com.multipagos.multipagos_backend.topup.application.service.TransactionService;
import com.multipagos.multipagos_backend.topup.domain.model.TopUpRequest;
import com.multipagos.multipagos_backend.topup.domain.model.Transaction;
import com.multipagos.multipagos_backend.topup.presentation.dto.TopUpRequestDto;
import com.multipagos.multipagos_backend.topup.presentation.dto.TopUpTransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/topup")
@CrossOrigin(origins = "${app.cors.allowed-origins:http://localhost:3000,http://localhost:3001}")
@RequiredArgsConstructor
@Validated
public class TopUpController {

  private final TransactionService transactionService;
  private final JwtService jwtService;

  @PostMapping
  public ResponseEntity<?> processTopUp(
      @Valid @RequestBody TopUpRequestDto requestDto,
      HttpServletRequest request) {

    try {
      log.info("[TOP-UP REQUEST] Starting top-up process for cellPhone: {} | value: {} | supplier: {}",
          requestDto.getCellPhone(), requestDto.getValue(), requestDto.getSupplierId());

      // Extract user ID from JWT token
      Long userId = extractUserIdFromRequest(request);
      log.info("[TOP-UP REQUEST] Processing for user ID: {}", userId);

      TopUpRequest domainRequest = TopUpRequest.builder()
          .cellPhone(requestDto.getCellPhone())
          .value(requestDto.getValue())
          .supplierId(requestDto.getSupplierId())
          .build();

      // Process and persist the transaction
      Transaction transaction = transactionService.processTopUpTransaction(domainRequest, userId);

      TopUpTransactionResponse response = TopUpTransactionResponse.builder()
          .id(transaction.getId().toString())
          .cellPhone(transaction.getPhoneNumber())
          .value(transaction.getAmount())
          .supplierName(transaction.getSupplierName())
          .status(transaction.getStatus().name())
          .transactionalID(transaction.getExternalTransactionId())
          .createdAt(transaction.getCreatedAt())
          .message(transaction.getResponseMessage())
          .build();

      log.info("[TOP-UP SUCCESS] Transaction completed | cellPhone: {} | transactionId: {} | status: {} | supplier: {}",
          requestDto.getCellPhone(), transaction.getExternalTransactionId(), transaction.getStatus(),
          transaction.getSupplierName());

      return ResponseFactory.success(response, "Recarga procesada exitosamente");

    } catch (IllegalArgumentException e) {
      log.error("[TOP-UP VALIDATION ERROR] Validation failed for cellPhone: {} | error: {}",
          requestDto.getCellPhone(), e.getMessage());
      return ResponseFactory.badRequest(e.getMessage(), request.getRequestURI());
    } catch (Exception e) {
      log.error("[TOP-UP ERROR] Unexpected error processing top-up for cellPhone: {} | error: {}",
          requestDto.getCellPhone(), e.getMessage(), e);
      return ResponseFactory.internalServerError(request.getRequestURI());
    }
  }

  @GetMapping("/history")
  public ResponseEntity<?> getUserTransactionHistory(
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
      HttpServletRequest request) {

    try {
      log.info("[TRANSACTION HISTORY] Retrieving transaction history | page: {} | size: {}",
          pageable.getPageNumber(), pageable.getPageSize());

      // Extract user ID from JWT token
      Long userId = extractUserIdFromRequest(request);
      log.info("[TRANSACTION HISTORY] Retrieving for user ID: {}", userId);

      Page<Transaction> transactions = transactionService.getUserTransactions(userId, pageable);

      Page<TopUpTransactionResponse> response = transactions.map(transaction -> TopUpTransactionResponse.builder()
          .id(transaction.getId().toString())
          .cellPhone(transaction.getPhoneNumber())
          .value(transaction.getAmount())
          .supplierName(transaction.getSupplierName())
          .status(transaction.getStatus().name())
          .transactionalID(transaction.getExternalTransactionId())
          .createdAt(transaction.getCreatedAt())
          .message(transaction.getResponseMessage())
          .build());

      log.info("[TRANSACTION HISTORY SUCCESS] Retrieved {} transactions for user: {}",
          response.getContent().size(), userId);

      return ResponseFactory.success(response, "Historial de transacciones obtenido exitosamente");

    } catch (IllegalArgumentException e) {
      log.error("[TRANSACTION HISTORY VALIDATION ERROR] Validation failed | error: {}", e.getMessage());
      return ResponseFactory.badRequest(e.getMessage(), request.getRequestURI());
    } catch (Exception e) {
      log.error("[TRANSACTION HISTORY ERROR] Unexpected error retrieving history | error: {}", e.getMessage(), e);
      return ResponseFactory.internalServerError(request.getRequestURI());
    }
  }

  @GetMapping("/{transactionId}")
  public ResponseEntity<?> getTransactionDetails(
      @PathVariable Long transactionId,
      HttpServletRequest request) {

    try {
      log.info("[TRANSACTION DETAILS] Retrieving transaction details for ID: {}", transactionId);

      // Extract user ID from JWT token
      Long userId = extractUserIdFromRequest(request);
      log.info("[TRANSACTION DETAILS] Retrieving for user ID: {}", userId);

      Transaction transaction = transactionService.getTransactionById(transactionId, userId);

      TopUpTransactionResponse response = TopUpTransactionResponse.builder()
          .id(transaction.getId().toString())
          .cellPhone(transaction.getPhoneNumber())
          .value(transaction.getAmount())
          .supplierName(transaction.getSupplierName())
          .status(transaction.getStatus().name())
          .transactionalID(transaction.getExternalTransactionId())
          .createdAt(transaction.getCreatedAt())
          .updatedAt(transaction.getUpdatedAt())
          .message(transaction.getResponseMessage())
          .build();

      log.info("[TRANSACTION DETAILS SUCCESS] Retrieved transaction: {} for user: {}", transactionId, userId);

      return ResponseFactory.success(response, "Detalle de transacción obtenido exitosamente");

    } catch (IllegalArgumentException e) {
      log.error("[TRANSACTION DETAILS VALIDATION ERROR] Validation failed | transactionId: {} | error: {}",
          transactionId, e.getMessage());
      return ResponseFactory.badRequest(e.getMessage(), request.getRequestURI());
    } catch (Exception e) {
      log.error("[TRANSACTION DETAILS ERROR] Unexpected error retrieving transaction: {} | error: {}",
          transactionId, e.getMessage(), e);
      return ResponseFactory.internalServerError(request.getRequestURI());
    }
  }

  /**
   * Extracts user ID from JWT token in Authorization header
   */
  private Long extractUserIdFromRequest(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new IllegalArgumentException("Token de autorización requerido");
    }

    String token = authHeader.substring(7);
    Long userId = jwtService.extractUserId(token);

    if (userId == null || userId <= 0) {
      throw new IllegalArgumentException("Token de autorización inválido");
    }

    return userId;
  }
}