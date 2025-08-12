package com.multipagos.multipagos_backend.topup.application.service;

import com.multipagos.multipagos_backend.topup.domain.model.Supplier;
import com.multipagos.multipagos_backend.topup.domain.port.SupplierPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

  private final SupplierPort supplierPort;

  public List<Supplier> getAllSuppliers() {
    return supplierPort.getSuppliers();
  }
}
