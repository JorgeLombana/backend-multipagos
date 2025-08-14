package com.multipagos.multipagos_backend.shared.application.util;

import com.multipagos.multipagos_backend.shared.domain.response.ApiErrorResponse;
import com.multipagos.multipagos_backend.shared.domain.response.ApiResponse;
import com.multipagos.multipagos_backend.shared.domain.response.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory class for creating standardized API responses
 * Ensures consistent response format across all endpoints
 */
public class ResponseFactory {

  private static final String API_VERSION = "v1";
  private static final String SUCCESS_STATUS = "success";

  public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
    ApiResponse<T> response = ApiResponse.<T>builder()
        .status(SUCCESS_STATUS)
        .message(message)
        .data(data)
        .apiVersion(API_VERSION)
        .build();

    return ResponseEntity.ok(response);
  }

  public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
    return success(data, "Operación completada exitosamente");
  }

  public static <T> ResponseEntity<ApiResponse<PagedResponse<T>>> success(Page<T> page, String message) {
    PagedResponse<T> pagedResponse = PagedResponse.from(page);

    ApiResponse<PagedResponse<T>> response = ApiResponse.<PagedResponse<T>>builder()
        .status(SUCCESS_STATUS)
        .message(message)
        .data(pagedResponse)
        .apiVersion(API_VERSION)
        .build();

    return ResponseEntity.ok(response);
  }

  public static <T> ResponseEntity<ApiResponse<PagedResponse<T>>> success(Page<T> page) {
    return success(page, "Datos obtenidos exitosamente");
  }

  public static ResponseEntity<ApiErrorResponse> error(HttpStatus status, String error, String message, String path) {
    ApiErrorResponse response = ApiErrorResponse.builder()
        .error(error)
        .message(message)
        .path(path)
        .apiVersion(API_VERSION)
        .build();

    return ResponseEntity.status(status).body(response);
  }

  public static ResponseEntity<ApiErrorResponse> validationError(List<FieldError> fieldErrors, String path) {
    List<ApiErrorResponse.ValidationError> validationErrors = fieldErrors.stream()
        .map(fieldError -> ApiErrorResponse.ValidationError.builder()
            .field(fieldError.getField())
            .rejectedValue(fieldError.getRejectedValue())
            .message(fieldError.getDefaultMessage())
            .build())
        .collect(Collectors.toList());

    ApiErrorResponse response = ApiErrorResponse.builder()
        .error("Error de Validación")
        .message("La validación de la solicitud falló")
        .path(path)
        .apiVersion(API_VERSION)
        .validationErrors(validationErrors)
        .build();

    return ResponseEntity.badRequest().body(response);
  }

  public static ResponseEntity<ApiErrorResponse> badRequest(String message, String path) {
    return error(HttpStatus.BAD_REQUEST, "Solicitud Incorrecta", message, path);
  }

  public static ResponseEntity<ApiErrorResponse> internalServerError(String path) {
    return error(HttpStatus.INTERNAL_SERVER_ERROR, "Error Interno del Servidor",
        "Ocurrió un error inesperado", path);
  }

  public static ResponseEntity<ApiErrorResponse> notFound(String message, String path) {
    return error(HttpStatus.NOT_FOUND, "No Encontrado", message, path);
  }

  public static ResponseEntity<ApiErrorResponse> unauthorized(String message, String path) {
    return error(HttpStatus.UNAUTHORIZED, "No Autorizado", message, path);
  }

  public static ResponseEntity<ApiErrorResponse> forbidden(String message, String path) {
    return error(HttpStatus.FORBIDDEN, "Prohibido", message, path);
  }
}