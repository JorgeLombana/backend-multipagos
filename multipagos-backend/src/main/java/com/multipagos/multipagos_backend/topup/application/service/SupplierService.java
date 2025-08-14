package com.multipagos.multipagos_backend.topup.application.service;

import com.multipagos.multipagos_backend.topup.domain.model.Supplier;
import com.multipagos.multipagos_backend.topup.domain.port.out.SupplierPort;
import com.multipagos.multipagos_backend.topup.domain.port.in.SupplierServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Manages supplier operations for mobile top-up transactions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierService implements SupplierServicePort {

  private final SupplierPort supplierPort;

  @Override
  public List<Supplier> getAllSuppliers() {
    log.info("[SUPPLIER SERVICE] Getting all suppliers");
    List<Supplier> suppliers = supplierPort.getAllSuppliers();
    log.info("[SUPPLIER SERVICE] Retrieved {} suppliers from port", suppliers.size());
    return suppliers;
  }

  @Override
  public List<Supplier> getActiveSuppliers() {
    log.info("[SUPPLIER SERVICE] Getting active suppliers");
    List<Supplier> suppliers = supplierPort.getActiveSuppliers();
    log.info("[SUPPLIER SERVICE] Retrieved {} active suppliers from port", suppliers.size());
    return suppliers;
  }

  @Override
  public Optional<Supplier> getSupplierById(String supplierId) {
    log.info("[SUPPLIER SERVICE] Getting supplier by ID: {}", supplierId);

    if (supplierId == null || supplierId.trim().isEmpty()) {
      log.warn("[SUPPLIER SERVICE] Supplier ID is null or empty");
      return Optional.empty();
    }

    Optional<Supplier> supplier = supplierPort.findById(supplierId);
    log.info("[SUPPLIER SERVICE] Supplier found: {}", supplier.isPresent());
    return supplier;
  }

  @Override
  public boolean isValidSupplier(String supplierId) {
    log.info("[SUPPLIER SERVICE] Checking if supplier is valid: {}", supplierId);

    if (supplierId == null || supplierId.trim().isEmpty()) {
      return false;
    }

    boolean exists = supplierPort.existsById(supplierId);
    log.info("[SUPPLIER SERVICE] Supplier {} is valid: {}", supplierId, exists);
    return exists;
  }

  @Override
  public void refreshSupplierData() {
    log.info("[SUPPLIER SERVICE] Refreshing supplier data");
    try {
      supplierPort.refreshSuppliers();
      log.info("[SUPPLIER SERVICE] Supplier data refreshed successfully");
    } catch (IllegalStateException e) {
      log.error("[SUPPLIER SERVICE] Invalid state for supplier refresh: {}", e.getMessage(), e);
      throw new IllegalStateException("Estado inválido para actualizar proveedores: " + e.getMessage(), e);
    } catch (RuntimeException e) {
      log.error("[SUPPLIER SERVICE] Runtime error refreshing suppliers: {}", e.getMessage(), e);
      throw new RuntimeException("Error de conexión actualizando proveedores: " + e.getMessage(), e);
    } catch (Exception e) {
      log.error("[SUPPLIER SERVICE] Unexpected error refreshing supplier data: {}", e.getMessage(), e);
      throw new RuntimeException("Error inesperado actualizando proveedores: " + e.getMessage(), e);
    }
  }
}
