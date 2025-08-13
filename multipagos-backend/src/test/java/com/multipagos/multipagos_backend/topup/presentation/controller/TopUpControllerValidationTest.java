package com.multipagos.multipagos_backend.topup.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class TopUpControllerValidationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnBadRequestWhenNoBodyProvided() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(post("/api/v1/topup")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.error").value("Solicitud Incorrecta"))
                .andExpect(jsonPath("$.message").value("El cuerpo de la solicitud es requerido"))
                .andExpect(jsonPath("$.path").value("/api/v1/topup"))
                .andExpect(jsonPath("$.apiVersion").value("v1"));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidJsonProvided() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(post("/api/v1/topup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.error").value("Solicitud Incorrecta"))
                .andExpect(jsonPath("$.message").value("Formato JSON inválido"))
                .andExpect(jsonPath("$.path").value("/api/v1/topup"))
                .andExpect(jsonPath("$.apiVersion").value("v1"));
    }

    @Test
    void shouldReturnValidationErrorWhenEmptyBodyProvided() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(post("/api/v1/topup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.error").value("Error de Validación"))
                .andExpect(jsonPath("$.message").value("La validación de la solicitud falló"))
                .andExpect(jsonPath("$.path").value("/api/v1/topup"))
                .andExpect(jsonPath("$.apiVersion").value("v1"))
                .andExpect(jsonPath("$.validationErrors").isArray())
                .andExpect(jsonPath("$.validationErrors.length()").value(3)); // cellPhone, value, supplierId
    }
}