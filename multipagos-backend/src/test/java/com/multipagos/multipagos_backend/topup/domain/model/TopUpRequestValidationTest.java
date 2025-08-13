package com.multipagos.multipagos_backend.topup.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TopUpRequestValidationTest {

    @Test
    void shouldReturnCorrectCellPhoneErrorMessage() {
        TopUpRequest request = TopUpRequest.builder()
                .cellPhone("200123456") // Invalid: doesn't start with 3
                .value(new BigDecimal("5000"))
                .supplierId("8753")
                .build();

        String error = request.getCellPhoneValidationError();
        assertEquals("El número de celular debe empezar con 3", error);
    }

    @Test
    void shouldReturnCorrectCellPhoneLengthErrorMessage() {
        TopUpRequest request = TopUpRequest.builder()
                .cellPhone("319782127") // Invalid: only 9 digits
                .value(new BigDecimal("5000"))
                .supplierId("8753")
                .build();

        String error = request.getCellPhoneValidationError();
        assertEquals("El número de celular debe tener exactamente 10 dígitos", error);
    }

    @Test
    void shouldReturnCorrectValueErrorMessage() {
        TopUpRequest request = TopUpRequest.builder()
                .cellPhone("3197821272")
                .value(new BigDecimal("500")) // Invalid: below minimum
                .supplierId("8753")
                .build();

        String error = request.getValueValidationError();
        assertEquals("El valor debe ser mayor o igual a 1000", error);
    }

    @Test
    void shouldReturnCorrectSupplierIdErrorMessage() {
        TopUpRequest request = TopUpRequest.builder()
                .cellPhone("3197821272")
                .value(new BigDecimal("10000"))
                .supplierId("9999") // Invalid supplier
                .build();

        String error = request.getSupplierIdValidationError();
        assertEquals("El ID del proveedor debe ser uno de: 8753 (Claro), 9773 (Movistar), 3398 (Tigo), 4689 (ETB)", error);
    }

    @Test
    void shouldValidateSuccessfullyWithCorrectData() {
        TopUpRequest request = TopUpRequest.builder()
                .cellPhone("3197821272")
                .value(new BigDecimal("10000"))
                .supplierId("8753")
                .build();

        assertTrue(request.isValid());
        assertNull(request.getCellPhoneValidationError());
        assertNull(request.getValueValidationError());
        assertNull(request.getSupplierIdValidationError());
    }
}