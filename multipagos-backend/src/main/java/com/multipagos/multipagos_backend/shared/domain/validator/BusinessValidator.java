package com.multipagos.multipagos_backend.shared.domain.validator;

import com.multipagos.multipagos_backend.shared.domain.valueobject.ValidationResult;
import java.util.regex.Pattern;

/**
 * Pure Domain Validator for business rules
 * Contains only business validation logic without security concerns  
 * Part of hexagonal architecture - domain layer validation
 * Note: Phone number and transaction amount validations are handled by Value Objects
 */
public final class BusinessValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_EMAIL_LENGTH = 100;

    private BusinessValidator() {
        // Utility class - prevent instantiation
    }

    public static ValidationResult validateEmail(String email) {
        if (isNullOrEmpty(email)) {
            return ValidationResult.invalid("Email es requerido");
        }

        String trimmedEmail = email.trim();

        if (trimmedEmail.length() > MAX_EMAIL_LENGTH) {
            return ValidationResult.invalid("Email no puede exceder " + MAX_EMAIL_LENGTH + " caracteres");
        }

        String lowerCaseEmail = trimmedEmail.toLowerCase();
        if (!EMAIL_PATTERN.matcher(lowerCaseEmail).matches()) {
            return ValidationResult.invalid("Formato de email no válido");
        }

        return ValidationResult.valid(lowerCaseEmail);
    }

    public static ValidationResult validateUserName(String name) {
        if (isNullOrEmpty(name)) {
            return ValidationResult.invalid("Nombre es requerido");
        }

        String trimmedName = name.trim();

        if (trimmedName.length() < MIN_NAME_LENGTH) {
            return ValidationResult.invalid("Nombre debe tener al menos " + MIN_NAME_LENGTH + " caracteres");
        }

        if (trimmedName.length() > MAX_NAME_LENGTH) {
            return ValidationResult.invalid("Nombre no puede exceder " + MAX_NAME_LENGTH + " caracteres");
        }

        return ValidationResult.valid(trimmedName);
    }

    private static boolean isNullOrEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }
}
