package com.multipagos.multipagos_backend.topup.domain.port;

import com.multipagos.multipagos_backend.topup.domain.model.TopUpRequest;
import com.multipagos.multipagos_backend.topup.domain.model.TopUpTransaction;

public interface TopUpPort {
  TopUpTransaction processTopUp(TopUpRequest request, String authToken);
}