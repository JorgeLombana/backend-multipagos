package com.multipagos.multipagos_backend.topup.presentation.controller;

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
class TopUpValidationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void shouldReturnValidationErrorsForEmptyJson() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(post("/api/v1/topup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.validationErrors").isArray())
                .andExpect(jsonPath("$.validationErrors.length()").value(3))
                .andExpect(jsonPath("$.validationErrors[?(@.field == 'cellPhone')].message")
                        .value("cellPhone is required"))
                .andExpect(jsonPath("$.validationErrors[?(@.field == 'value')].message").value("value is required"))
                .andExpect(jsonPath("$.validationErrors[?(@.field == 'supplierId')].message")
                        .value("supplierId is required"));
    }

    @Test
    void shouldReturnValidationErrorsForInvalidValues() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String invalidRequest = """
                {
                    "cellPhone": "2001234567",
                    "value": 500,
                    "supplierId": "9999"
                }
                """;

        mockMvc.perform(post("/api/v1/topup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors").isArray())
                .andExpect(jsonPath("$.validationErrors.length()").value(3));
    }
}