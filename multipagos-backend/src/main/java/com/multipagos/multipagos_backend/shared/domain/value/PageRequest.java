package com.multipagos.multipagos_backend.shared.domain.value;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain Value Object for pagination requests
 * Pure domain model without Spring Data dependencies
 * Part of hexagonal architecture to abstract infrastructure concerns
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {

  private int page;
  private int size;
  private String sortField;
  private String sortDirection;

  public static PageRequest of(int page, int size) {
    return PageRequest.builder()
        .page(page)
        .size(size)
        .sortField("createdAt")
        .sortDirection("DESC")
        .build();
  }

  public static PageRequest of(int page, int size, String sortField, String sortDirection) {
    return PageRequest.builder()
        .page(page)
        .size(size)
        .sortField(sortField)
        .sortDirection(sortDirection)
        .build();
  }

  /**
   * Domain validation: validate page request parameters
   */
  public boolean isValid() {
    return page >= 0 && size > 0 && size <= 100;
  }

  /**
   * Domain business rule: get offset for pagination
   */
  public int getOffset() {
    return page * size;
  }
}
