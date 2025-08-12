package com.multipagos.multipagos_backend.topup.presentation.controller;

import com.multipagos.multipagos_backend.topup.application.service.SupplierService;
import com.multipagos.multipagos_backend.topup.domain.model.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SupplierController {

  private final SupplierService supplierService;

  @GetMapping
  public ResponseEntity<List<Supplier>> getSuppliers() {
    try {
      log.info("Getting all suppliers");
      List<Supplier> suppliers = supplierService.getAllSuppliers();
      log.info("Successfully retrieved {} suppliers", suppliers.size());
      return ResponseEntity.ok(suppliers);
    } catch (Exception e) {
      log.error("Error getting suppliers", e);
      return ResponseEntity.internalServerError().build();
    }
  }
}
