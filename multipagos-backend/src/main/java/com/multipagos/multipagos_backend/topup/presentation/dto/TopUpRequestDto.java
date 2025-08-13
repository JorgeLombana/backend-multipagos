package com.multipagos.multipagos_backend.topup.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopUpRequestDto {

  @NotBlank(message = "El número de celular es requerido")
  @Pattern(regexp = "^3\\d{9}$", message = "El número de celular debe empezar con 3 y tener exactamente 10 dígitos")
  private String cellPhone;

  @NotNull(message = "El valor es requerido")
  @DecimalMin(value = "1000", message = "El valor debe ser mayor o igual a 1000")
  @DecimalMax(value = "100000", message = "El valor debe ser menor o igual a 100000")
  private BigDecimal value;

  @NotBlank(message = "El ID del proveedor es requerido")
  @Pattern(regexp = "^(8753|9773|3398|4689)$", message = "El ID del proveedor debe ser uno de: 8753 (Claro), 9773 (Movistar), 3398 (Tigo), 4689 (ETB)")
  private String supplierId;
}
