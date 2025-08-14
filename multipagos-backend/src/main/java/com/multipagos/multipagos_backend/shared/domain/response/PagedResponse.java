package com.multipagos.multipagos_backend.shared.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Response wrapper for paginated data following Spring Boot standards
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
