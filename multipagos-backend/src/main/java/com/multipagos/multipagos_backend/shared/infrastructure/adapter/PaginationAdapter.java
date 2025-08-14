package com.multipagos.multipagos_backend.shared.infrastructure.adapter;

import com.multipagos.multipagos_backend.shared.domain.port.PaginationPort;
import com.multipagos.multipagos_backend.shared.domain.value.PageRequest;
import com.multipagos.multipagos_backend.shared.domain.value.PagedResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Infrastructure Adapter implementing PaginationPort
 * Bridges between domain pagination and Spring Data pagination
 * Encapsulates Spring Data pagination details from domain layer
 */
@Component
@Slf4j
public class PaginationAdapter implements PaginationPort {

    @Override
    public <T> Object toInfrastructurePageable(PageRequest pageRequest) {
        log.debug("[PAGINATION ADAPTER] Converting domain PageRequest to Spring Pageable");
        
        Sort sort = createSort(pageRequest.getSortField(), pageRequest.getSortDirection());
        return org.springframework.data.domain.PageRequest.of(
            pageRequest.getPage(), 
            pageRequest.getSize(), 
            sort
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> PagedResult<T> toDomainPagedResult(Object infrastructurePage) {
        log.debug("[PAGINATION ADAPTER] Converting Spring Page to domain PagedResult");
        
        if (!(infrastructurePage instanceof Page)) {
            throw new IllegalArgumentException("Expected Spring Data Page object");
        }

        Page<T> springPage = (Page<T>) infrastructurePage;

        return PagedResult.<T>builder()
            .content(springPage.getContent())
            .page(springPage.getNumber())
            .size(springPage.getSize())
            .totalElements(springPage.getTotalElements())
            .totalPages(springPage.getTotalPages())
            .first(springPage.isFirst())
            .last(springPage.isLast())
            .hasNext(springPage.hasNext())
            .hasPrevious(springPage.hasPrevious())
            .build();
    }

    @Override
    public <T> PagedResult<T> empty() {
        log.debug("[PAGINATION ADAPTER] Creating empty PagedResult");
        
        return PagedResult.<T>builder()
            .content(Collections.emptyList())
            .page(0)
            .size(0)
            .totalElements(0L)
            .totalPages(0)
            .first(true)
            .last(true)
            .hasNext(false)
            .hasPrevious(false)
            .build();
    }

    @Override
    public <T> PagedResult<T> create(List<T> content, PageRequest pageRequest, long totalElements) {
        log.debug("[PAGINATION ADAPTER] Creating PagedResult from content and page info");
        
        int totalPages = (int) Math.ceil((double) totalElements / pageRequest.getSize());
        boolean hasNext = pageRequest.getPage() + 1 < totalPages;
        boolean hasPrevious = pageRequest.getPage() > 0;
        boolean isFirst = pageRequest.getPage() == 0;
        boolean isLast = pageRequest.getPage() + 1 >= totalPages;

        return PagedResult.<T>builder()
            .content(content != null ? content : Collections.emptyList())
            .page(pageRequest.getPage())
            .size(pageRequest.getSize())
            .totalElements(totalElements)
            .totalPages(totalPages)
            .first(isFirst)
            .last(isLast)
            .hasNext(hasNext)
            .hasPrevious(hasPrevious)
            .build();
    }

    // Helper method to convert domain sort to Spring sort
    private Sort createSort(String sortField, String sortDirection) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection) 
            ? Sort.Direction.ASC 
            : Sort.Direction.DESC;
        return Sort.by(direction, sortField);
    }

    // Legacy methods for backward compatibility (will be removed after refactoring)
    @Deprecated
    public Pageable toSpringPageable(PageRequest pageRequest) {
        return (Pageable) toInfrastructurePageable(pageRequest);
    }

    @Deprecated
    public <T> PagedResult<T> toDomainPagedResult(Page<T> springPage) {
        return toDomainPagedResult((Object) springPage);
    }
}
