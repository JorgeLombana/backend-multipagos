package com.multipagos.multipagos_backend.topup.presentation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidSupplierValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSupplier {

  String message() default "El ID del proveedor no es válido o no está activo";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
