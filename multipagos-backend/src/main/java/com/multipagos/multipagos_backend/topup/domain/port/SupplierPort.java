package com.multipagos.multipagos_backend.topup.domain.port;

import com.multipagos.multipagos_backend.topup.domain.model.Supplier;
import java.util.List;

public interface SupplierPort {
  List<Supplier> getSuppliers();
}
