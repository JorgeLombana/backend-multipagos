package com.multipagos.multipagos_backend.topup.presentation.controller;

import com.multipagos.multipagos_backend.shared.application.util.ResponseFactory;
import com.multipagos.multipagos_backend.topup.domain.port.in.SupplierServicePort;
import com.multipagos.multipagos_backend.topup.domain.model.Supplier;
import com.multipagos.multipagos_backend.topup.presentation.dto.SupplierResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Supplier REST Controller following Hexagonal Architecture
 * Depends on SupplierServicePort (domain interface) instead of concrete
 * implementation
 * Follows SOLID principles and Clean Code practices
 * 
 * Maintains the standard ApiResponse wrapper but returns simple supplier data
 * matching Puntored API specification in the data field
 */
@Slf4j
@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierController {

  private final SupplierServicePort supplierService;

  /**
   * Get all available suppliers for mobile recharges
   * Returns suppliers in standard ApiResponse wrapper with simple data format
   * 
   * @param request HTTP request for error context
   * @return ResponseEntity with standard response containing array of suppliers
   *         (id and name only)
   */
  @GetMapping
  public ResponseEntity<?> getAllSuppliers(HttpServletRequest request) {
    try {
      log.info("[SUPPLIER CONTROLLER] Getting all suppliers");

      List<Supplier> suppliers = supplierService.getAllSuppliers();

      // Convert to simple DTOs containing only id and name as required by Puntored
      // API
      List<SupplierResponseDto> suppliersData = suppliers.stream()
          .map(supplier -> new SupplierResponseDto(supplier.getId(), supplier.getName()))
          .collect(Collectors.toList());

      log.info("[SUPPLIER CONTROLLER] Successfully retrieved {} suppliers", suppliersData.size());

      return ResponseFactory.success(suppliersData, "Proveedores obtenidos exitosamente");

    } catch (Exception e) {
      log.error("[SUPPLIER CONTROLLER] Error getting suppliers: {}", e.getMessage(), e);
      return ResponseFactory.internalServerError(request.getRequestURI());
    }
  }

}
