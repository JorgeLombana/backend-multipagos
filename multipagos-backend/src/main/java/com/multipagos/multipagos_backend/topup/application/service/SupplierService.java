package com.multipagos.multipagos_backend.topup.application.service;

import com.multipagos.multipagos_backend.topup.domain.model.Supplier;
import com.multipagos.multipagos_backend.topup.domain.port.SupplierPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierService {

  private final SupplierPort supplierPort;

  public List<Supplier> getAllSuppliers() {
    log.info("[SUPPLIER SERVICE] Getting all suppliers");
    List<Supplier> suppliers = supplierPort.getSuppliers();
    log.info("[SUPPLIER SERVICE] Retrieved {} suppliers from port", suppliers.size());
    return suppliers;
  }
}
