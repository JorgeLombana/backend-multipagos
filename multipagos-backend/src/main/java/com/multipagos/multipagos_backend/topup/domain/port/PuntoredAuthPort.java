package com.multipagos.multipagos_backend.topup.domain.port;

import com.multipagos.multipagos_backend.topup.domain.model.PuntoredToken;

public interface PuntoredAuthPort {
  PuntoredToken authenticate();
}
