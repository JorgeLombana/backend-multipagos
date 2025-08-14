package com.multipagos.multipagos_backend.topup.presentation.controller;

import com.multipagos.multipagos_backend.shared.domain.port.TokenGeneratorPort;
import com.multipagos.multipagos_backend.shared.application.util.ResponseFactory;
import com.multipagos.multipagos_backend.shared.domain.value.PageRequest;
import com.multipagos.multipagos_backend.shared.domain.value.PagedResult;
import com.multipagos.multipagos_backend.topup.domain.port.in.TopUpServicePort;
import com.multipagos.multipagos_backend.topup.domain.port.in.TransactionServicePort;
import com.multipagos.multipagos_backend.topup.domain.model.TopUpRequest;
import com.multipagos.multipagos_backend.topup.domain.model.TransactionDomain;
import com.multipagos.multipagos_backend.topup.domain.model.valueobject.*;
import com.multipagos.multipagos_backend.topup.presentation.dto.TopUpRequestDto;
import com.multipagos.multipagos_backend.topup.presentation.dto.TopUpTransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

/**
 * TopUp REST Controller implementing Clean Code and SOLID principles
 * Follows Single Responsibility: handles only HTTP concerns for top-up
 * operations
 * Implements proper error handling, logging, and security validation
 * Uses dependency injection for loose coupling
 */
@Slf4j
@RestController
@RequestMapping("/topup")
@RequiredArgsConstructor
@Validated
public class TopUpController {

  private final TopUpServicePort topUpService;
  private final TransactionServicePort transactionService;
  private final TokenGeneratorPort tokenGenerator;

  /**
   * Processes a mobile top-up transaction
   * Validates input, authenticates user, and delegates business logic to service
   * layer
   * 
   * @param requestDto The top-up request containing cellphone, amount, and
   *                   supplier
   * @param request    HTTP request for extracting authentication token
   * @return ResponseEntity with transaction result
   */
  @PostMapping
  public ResponseEntity<?> processTopUp(
      @Valid @RequestBody TopUpRequestDto requestDto,
      HttpServletRequest request) {

    String operationId = generateOperationId("TOPUP", requestDto.getCellPhone());

    try {
      logTopUpRequest(requestDto, operationId);

      Long userId = extractUserIdFromRequest(request);
      logUserAuthentication(userId, operationId);

      TopUpRequest domainRequest = buildTopUpRequest(requestDto);
      TransactionDomain transaction = topUpService.executeTopUp(domainRequest, userId);
      TopUpTransactionResponse response = buildTransactionResponse(transaction);

      logTopUpSuccess(requestDto, transaction, operationId);

      return ResponseFactory.success(response, "Recarga procesada exitosamente");

    } catch (IllegalArgumentException e) {
      return handleValidationError(e, requestDto.getCellPhone(), operationId, request);
    } catch (Exception e) {
      return handleUnexpectedError(e, requestDto.getCellPhone(), operationId, request);
    }
  }

  /**
   * Retrieves paginated transaction history for authenticated user
   * Implements proper pagination and sorting
   * 
   * @param page          Request parameter for page number (default: 0)
   * @param size          Request parameter for page size (default: 20)
   * @param sortField     Request parameter for sort field (default: createdAt)
   * @param sortDirection Request parameter for sort direction (default: DESC)
   * @param request       HTTP request for extracting authentication token
   * @return ResponseEntity with paginated transaction history
   */
  @GetMapping("/history")
  public ResponseEntity<?> getUserTransactionHistory(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortField,
      @RequestParam(defaultValue = "DESC") String sortDirection,
      HttpServletRequest request) {

    String operationId = generateOperationId("HISTORY", null);

    try {
      PageRequest pageRequest = PageRequest.of(page, size, sortField, sortDirection);
      logHistoryRequest(pageRequest, operationId);

      Long userId = extractUserIdFromRequest(request);
      logUserAuthentication(userId, operationId);

      PagedResult<TransactionDomain> pagedTransactions = transactionService.getTransactionHistoryPaged(userId,
          pageRequest);
      
      // Map domain objects to response DTOs while preserving pagination info
      List<TopUpTransactionResponse> responseContent = pagedTransactions.getContent().stream()
          .map(this::buildTransactionResponse)
          .toList();
      
      // Create PagedResult with DTOs
      PagedResult<TopUpTransactionResponse> pagedResponse = PagedResult.<TopUpTransactionResponse>builder()
          .content(responseContent)
          .page(pagedTransactions.getPage())
          .size(pagedTransactions.getSize())
          .totalElements(pagedTransactions.getTotalElements())
          .totalPages(pagedTransactions.getTotalPages())
          .first(pagedTransactions.isFirst())
          .last(pagedTransactions.isLast())
          .hasNext(pagedTransactions.isHasNext())
          .hasPrevious(pagedTransactions.isHasPrevious())
          .build();

      logHistorySuccess(responseContent.size(), userId, operationId);

      return ResponseFactory.success(pagedResponse, "Historial de transacciones obtenido exitosamente");

    } catch (IllegalArgumentException e) {
      return handleValidationError(e, null, operationId, request);
    } catch (Exception e) {
      return handleUnexpectedError(e, null, operationId, request);
    }
  }

  // Private helper methods following Single Responsibility Principle

  /**
   * Extracts user ID from JWT token with proper validation
   */
  private Long extractUserIdFromRequest(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (isInvalidAuthorizationHeader(authHeader)) {
      throw new IllegalArgumentException("Token de autorización requerido");
    }

    String token = authHeader.substring(7);
    Long userId = tokenGenerator.extractUserId(token);

    if (isInvalidUserId(userId)) {
      throw new IllegalArgumentException("Token de autorización inválido");
    }

    return userId;
  }

  private boolean isInvalidAuthorizationHeader(String authHeader) {
    return authHeader == null || !authHeader.startsWith("Bearer ");
  }

  private boolean isInvalidUserId(Long userId) {
    return userId == null || userId <= 0;
  }

  /**
   * Builds domain request from DTO using builder pattern
   */
  private TopUpRequest buildTopUpRequest(TopUpRequestDto requestDto) {
    return TopUpRequest.builder()
        .phoneNumber(PhoneNumber.of(requestDto.getCellPhone()))
        .amount(Amount.of(requestDto.getValue()))
        .supplierId(SupplierId.of(requestDto.getSupplierId()))
        .build();
  }

  /**
   * Maps transaction domain object to response DTO
   */
  private TopUpTransactionResponse buildTransactionResponse(TransactionDomain transaction) {
    return TopUpTransactionResponse.builder()
        .id(transaction.getId().toString())
        .cellPhone(transaction.getPhoneNumber() != null ? transaction.getPhoneNumber().getValue() : null)
        .value(transaction.getAmount() != null ? transaction.getAmount().getValue() : null)
        .supplierName(transaction.getSupplierName())
        .status(transaction.getStatus().name())
        .transactionalID(transaction.getExternalTransactionId())
        .createdAt(transaction.getCreatedAt())
        .updatedAt(transaction.getUpdatedAt()) // Always include updatedAt for consistency
        .message(transaction.getResponseMessage())
        .build();
  }

  private String generateOperationId(String operation, String identifier) {
    return String.format("%s_%s_%d", operation,
        identifier != null ? identifier : "USER",
        System.currentTimeMillis());
  }

  private void logTopUpRequest(TopUpRequestDto requestDto, String operationId) {
    log.info("[{}] Top-up request started | cellPhone: {} | value: {} | supplier: {}",
        operationId, requestDto.getCellPhone(), requestDto.getValue(), requestDto.getSupplierId());
  }

  private void logUserAuthentication(Long userId, String operationId) {
    log.info("[{}] User authenticated | userId: {}", operationId, userId);
  }

  private void logTopUpSuccess(TopUpRequestDto requestDto, TransactionDomain transaction, String operationId) {
    log.info("[{}] Top-up completed successfully | cellPhone: {} | transactionId: {} | status: {} | supplier: {}",
        operationId, requestDto.getCellPhone(), transaction.getExternalTransactionId(),
        transaction.getStatus(), transaction.getSupplierName());
  }

  private void logHistoryRequest(PageRequest pageRequest, String operationId) {
    log.info("[{}] Transaction history requested | page: {} | size: {}",
        operationId, pageRequest.getPage(), pageRequest.getSize());
  }

  private void logHistorySuccess(int transactionCount, Long userId, String operationId) {
    log.info("[{}] Transaction history retrieved successfully | count: {} | userId: {}",
        operationId, transactionCount, userId);
  }

  private ResponseEntity<?> handleValidationError(IllegalArgumentException e, String identifier,
      String operationId, HttpServletRequest request) {
    log.error("[{}] Validation error | identifier: {} | error: {}", operationId, identifier, e.getMessage());
    return ResponseFactory.badRequest(e.getMessage(), request.getRequestURI());
  }

  private ResponseEntity<?> handleUnexpectedError(Exception e, String identifier,
      String operationId, HttpServletRequest request) {
    log.error("[{}] Unexpected error | identifier: {} | error: {}", operationId, identifier, e.getMessage(), e);
    return ResponseFactory.internalServerError(request.getRequestURI());
  }
}