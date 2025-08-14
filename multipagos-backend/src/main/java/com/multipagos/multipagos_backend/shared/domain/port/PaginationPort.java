package com.multipagos.multipagos_backend.shared.domain.port;

import com.multipagos.multipagos_backend.shared.domain.value.PageRequest;
import com.multipagos.multipagos_backend.shared.domain.value.PagedResult;

/**
 * Domain Port for Pagination Operations
 * Abstracts pagination functionality from infrastructure details
 * Allows domain to work with pagination without knowing Spring Data specifics
 */
public interface PaginationPort {

    /**
     * Convert domain PageRequest to infrastructure-specific pageable object
     * @param pageRequest Domain page request
     * @return Infrastructure-specific pageable object (implementation detail hidden)
     */
    <T> Object toInfrastructurePageable(PageRequest pageRequest);

    /**
     * Convert infrastructure page result to domain PagedResult
     * @param infrastructurePage Infrastructure-specific page result
     * @return Domain PagedResult
     */
    <T> PagedResult<T> toDomainPagedResult(Object infrastructurePage);

    /**
     * Create empty paged result
     * @return Empty paged result
     */
    <T> PagedResult<T> empty();

    /**
     * Create paged result from content and page information
     * @param content List of content items
     * @param pageRequest Original page request
     * @param totalElements Total number of elements
     * @return Domain PagedResult
     */
    <T> PagedResult<T> create(java.util.List<T> content, PageRequest pageRequest, long totalElements);
}
