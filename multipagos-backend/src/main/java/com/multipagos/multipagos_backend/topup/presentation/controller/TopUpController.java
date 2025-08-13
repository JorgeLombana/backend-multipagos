package com.multipagos.multipagos_backend.topup.presentation.controller;

import com.multipagos.multipagos_backend.shared.application.util.ResponseFactory;
import com.multipagos.multipagos_backend.topup.application.service.TopUpService;
import com.multipagos.multipagos_backend.topup.domain.model.TopUpRequest;
import com.multipagos.multipagos_backend.topup.domain.model.TopUpTransaction;
import com.multipagos.multipagos_backend.topup.presentation.dto.TopUpRequestDto;
import com.multipagos.multipagos_backend.topup.presentation.dto.TopUpTransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/topup")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Validated
public class TopUpController {

  private final TopUpService topUpService;

  @PostMapping
  public ResponseEntity<?> processTopUp(
      @Valid @RequestBody TopUpRequestDto requestDto,
      HttpServletRequest request) {

    try {
      log.info("[TOP-UP REQUEST] Starting top-up process for cellPhone: {} | value: {} | supplier: {}",
          requestDto.getCellPhone(), requestDto.getValue(), requestDto.getSupplierId());

      TopUpRequest domainRequest = TopUpRequest.builder()
          .cellPhone(requestDto.getCellPhone())
          .value(requestDto.getValue())
          .supplierId(requestDto.getSupplierId())
          .build();

      TopUpTransaction transaction = topUpService.processTopUp(domainRequest);

      TopUpTransactionResponse response = TopUpTransactionResponse.builder()
          .id(transaction.getId())
          .cellPhone(transaction.getCellPhone())
          .value(transaction.getValue())
          .supplierName(transaction.getSupplierName())
          .status(transaction.getStatus().name())
          .transactionalID(transaction.getTransactionalID())
          .createdAt(transaction.getCreatedAt())
          .build();

      log.info("[TOP-UP SUCCESS] Transaction completed | cellPhone: {} | transactionId: {} | status: {} | supplier: {}",
          requestDto.getCellPhone(), transaction.getTransactionalID(), transaction.getStatus(),
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
}