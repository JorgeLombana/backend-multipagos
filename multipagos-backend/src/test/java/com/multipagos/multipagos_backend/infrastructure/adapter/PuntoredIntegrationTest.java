package com.multipagos.multipagos_backend.infrastructure.adapter;

import com.multipagos.multipagos_backend.topup.domain.model.PuntoredToken;
import com.multipagos.multipagos_backend.topup.domain.model.Supplier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PuntoredIntegrationTest {

    @Test
    void testDomainModels() {
        // Test PuntoredToken
        PuntoredToken token = new PuntoredToken("Bearer test-token");
        assertEquals("Bearer test-token", token.getBearerToken());
        assertEquals("Bearer test-token", token.getToken());

        // Test Supplier
        Supplier supplier = new Supplier("8753", "Claro");
        assertEquals("8753", supplier.getId());
        assertEquals("Claro", supplier.getName());
    }
}