package com.multipagos.multipagos_backend.topup.application.service;

import com.multipagos.multipagos_backend.topup.domain.model.PuntoredToken;
import com.multipagos.multipagos_backend.topup.domain.model.TopUpRequest;
import com.multipagos.multipagos_backend.topup.domain.model.TopUpTransaction;
import com.multipagos.multipagos_backend.topup.domain.port.PuntoredAuthPort;
import com.multipagos.multipagos_backend.topup.domain.port.TopUpPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopUpServiceTest {

    @Mock
    private TopUpPort topUpPort;

    @Mock
    private PuntoredAuthPort authPort;

    @InjectMocks
    private TopUpService topUpService;

    @Test
    void shouldAuthenticateAndProcessTopUpSuccessfully() {
        // Given
        TopUpRequest request = TopUpRequest.builder()
                .cellPhone("3001234567")
                .value(new BigDecimal("5000"))
                .supplierId("8753")
                .build();

        PuntoredToken token = new PuntoredToken("Bearer test-token-123");

        TopUpTransaction expectedTransaction = TopUpTransaction.builder()
                .cellPhone("3001234567")
                .value(new BigDecimal("5000"))
                .supplierId("8753")
                .supplierName("Claro")
                .status(TopUpTransaction.TransactionStatus.COMPLETED)
                .transactionalID("TXN123456")
                .message("Transaction successful")
                .createdAt(LocalDateTime.now())
                .build();

        when(authPort.authenticate()).thenReturn(token);
        when(topUpPort.processTopUp(eq(request), eq("Bearer test-token-123")))
                .thenReturn(expectedTransaction);

        // When
        TopUpTransaction result = topUpService.processTopUp(request);

        // Then
        assertNotNull(result);
        assertEquals("3001234567", result.getCellPhone());
        assertEquals(new BigDecimal("5000"), result.getValue());
        assertEquals("8753", result.getSupplierId());
        assertEquals("TXN123456", result.getTransactionalID());
        assertEquals(TopUpTransaction.TransactionStatus.COMPLETED, result.getStatus());

        verify(authPort, times(1)).authenticate();
        verify(topUpPort, times(1)).processTopUp(eq(request), eq("Bearer test-token-123"));
    }

    @Test
    void shouldHandleAuthenticationFailure() {
        // Given
        TopUpRequest request = TopUpRequest.builder()
                .cellPhone("3001234567")
                .value(new BigDecimal("5000"))
                .supplierId("8753")
                .build();

        when(authPort.authenticate()).thenThrow(new RuntimeException("Authentication failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            topUpService.processTopUp(request);
        });

        assertEquals("Authentication failed", exception.getMessage());
        verify(authPort, times(1)).authenticate();
        verify(topUpPort, never()).processTopUp(any(), any());
    }
}