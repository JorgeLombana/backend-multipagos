package com.multipagos.multipagos_backend.shared.infrastructure.response;

import com.multipagos.multipagos_backend.shared.domain.value.PagedResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Infrastructure response wrapper for paginated data following Spring Boot standards
 * This belongs to infrastructure layer as it has Spring dependencies
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

  private List<T> content;
  private PageableInfo pageable;
  private long totalElements;
  private int totalPages;
  private boolean first;
  private boolean last;
  private int numberOfElements;
  private boolean empty;

  /**
   * Convert from Spring Page (infrastructure) to PagedResponse (infrastructure)
   */
  public static <T> PagedResponse<T> from(Page<T> page) {
    return PagedResponse.<T>builder()
        .content(page.getContent())
        .pageable(PageableInfo.from(page.getPageable()))
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .first(page.isFirst())
        .last(page.isLast())
        .numberOfElements(page.getNumberOfElements())
        .empty(page.isEmpty())
        .build();
  }

  /**
   * Convert from domain PagedResult to infrastructure PagedResponse
   */
  public static <T> PagedResponse<T> from(PagedResult<T> pagedResult) {
    return PagedResponse.<T>builder()
        .content(pagedResult.getContent())
        .pageable(PageableInfo.builder()
            .pageNumber(pagedResult.getPage())
            .pageSize(pagedResult.getSize())
            .offset((long) pagedResult.getPage() * pagedResult.getSize())
            .paged(true)
            .unpaged(false)
            .sort(SortInfo.builder()
                .empty(true)
                .sorted(false)
                .unsorted(true)
                .build())
            .build())
        .totalElements(pagedResult.getTotalElements())
        .totalPages(pagedResult.getTotalPages())
        .first(pagedResult.isFirst())
        .last(pagedResult.isLast())
        .numberOfElements(pagedResult.getNumberOfElements())
        .empty(!pagedResult.hasContent())
        .build();
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PageableInfo {
    private int pageNumber;
    private int pageSize;
    private long offset;
    private boolean paged;
    private boolean unpaged;
    private SortInfo sort;

    public static PageableInfo from(org.springframework.data.domain.Pageable pageable) {
      return PageableInfo.builder()
          .pageNumber(pageable.getPageNumber())
          .pageSize(pageable.getPageSize())
          .offset(pageable.getOffset())
          .paged(pageable.isPaged())
          .unpaged(pageable.isUnpaged())
          .sort(SortInfo.from(pageable.getSort()))
          .build();
    }
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SortInfo {
    private boolean empty;
    private boolean sorted;
    private boolean unsorted;

    public static SortInfo from(org.springframework.data.domain.Sort sort) {
      return SortInfo.builder()
          .empty(sort.isEmpty())
          .sorted(sort.isSorted())
          .unsorted(sort.isUnsorted())
          .build();
    }
  }
}
