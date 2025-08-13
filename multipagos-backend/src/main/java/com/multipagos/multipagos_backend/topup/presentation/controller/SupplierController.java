package com.multipagos.multipagos_backend.topup.presentation.controller;

import com.multipagos.multipagos_backend.shared.application.util.ResponseFactory;
import com.multipagos.multipagos_backend.topup.application.service.SupplierService;
import com.multipagos.multipagos_backend.topup.domain.model.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/suppliers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SupplierController {

  private final SupplierService supplierService;

  @GetMapping
  public ResponseEntity<?> getSuppliers(HttpServletRequest request) {
    try {
      log.info("[SUPPLIERS] Starting get suppliers request");
      List<Supplier> suppliers = supplierService.getAllSuppliers();
      log.info("[SUPPLIERS] Successfully retrieved {} suppliers", suppliers.size());
      return ResponseFactory.success(suppliers, "Proveedores obtenidos exitosamente");
    } catch (Exception e) {
      log.error("[SUPPLIERS] Error getting suppliers | error: {}", e.getMessage(), e);
      return ResponseFactory.internalServerError(request.getRequestURI());
    }
  }
}
