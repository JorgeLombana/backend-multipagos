package com.multipagos.multipagos_backend.shared.domain.value;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Domain Value Object for paginated responses
 * Pure domain model without Spring Data dependencies
 * Part of hexagonal architecture to abstract infrastructure concerns
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResult<T> {

  private List<T> content;
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;
  private boolean first;
  private boolean last;
  private boolean hasNext;
  private boolean hasPrevious;

  public static <T> PagedResult<T> of(List<T> content, PageRequest pageRequest, long totalElements) {
    int totalPages = (int) Math.ceil((double) totalElements / pageRequest.getSize());
    int currentPage = pageRequest.getPage();

    return PagedResult.<T>builder()
        .content(content)
        .page(currentPage)
        .size(pageRequest.getSize())
        .totalElements(totalElements)
        .totalPages(totalPages)
        .first(currentPage == 0)
        .last(currentPage >= totalPages - 1)
        .hasNext(currentPage < totalPages - 1)
        .hasPrevious(currentPage > 0)
        .build();
  }

  /**
   * Domain business rule: check if page has content
   */
  public boolean hasContent() {
    return content != null && !content.isEmpty();
  }

  /**
   * Domain business rule: get number of elements in current page
   */
  public int getNumberOfElements() {
    return content != null ? content.size() : 0;
  }
}
