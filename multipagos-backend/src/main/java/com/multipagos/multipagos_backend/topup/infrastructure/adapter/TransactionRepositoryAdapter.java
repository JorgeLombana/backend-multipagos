package com.multipagos.multipagos_backend.topup.infrastructure.adapter;

import com.multipagos.multipagos_backend.shared.domain.port.PaginationPort;
import com.multipagos.multipagos_backend.shared.domain.value.PageRequest;
import com.multipagos.multipagos_backend.shared.domain.value.PagedResult;
import com.multipagos.multipagos_backend.topup.domain.model.TransactionDomain;
import com.multipagos.multipagos_backend.topup.domain.model.TransactionStatus;
import com.multipagos.multipagos_backend.topup.domain.port.out.TransactionRepositoryPort;
import com.multipagos.multipagos_backend.topup.infrastructure.entity.TransactionEntity;
import com.multipagos.multipagos_backend.topup.infrastructure.repository.TransactionEntityRepository;
import com.multipagos.multipagos_backend.topup.infrastructure.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Infrastructure adapter that bridges domain TransactionRepositoryPort with JPA repository
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

  private final TransactionEntityRepository jpaRepository;
  private final TransactionMapper mapper;
  private final PaginationPort paginationPort;

  @Override
  public TransactionDomain save(TransactionDomain transaction) {
    log.debug("[TRANSACTION ADAPTER] Saving transaction domain to database");
    TransactionEntity entity = mapper.toEntity(transaction);
    TransactionEntity savedEntity = jpaRepository.save(entity);
    TransactionDomain savedDomain = mapper.toDomain(savedEntity);
    log.debug("[TRANSACTION ADAPTER] Transaction saved with ID: {}", savedDomain.getId());
    return savedDomain;
  }

  @Override
  public Optional<TransactionDomain> findById(Long id) {
    log.debug("[TRANSACTION ADAPTER] Finding transaction by ID: {}", id);
    return jpaRepository.findById(id)
        .map(mapper::toDomain);
  }

  @Override
  public PagedResult<TransactionDomain> findByUserIdAndActiveTrue(Long userId, PageRequest pageRequest) {
    log.debug("[TRANSACTION ADAPTER] Finding transactions for user: {}", userId);

    // Convert domain PageRequest to Spring Pageable
    Pageable pageable = (Pageable) paginationPort.toInfrastructurePageable(pageRequest);

    // Execute query
    Page<TransactionEntity> entityPage = jpaRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId,
        pageable);

    // Convert entity page to domain page
    Page<TransactionDomain> domainPage = entityPage.map(mapper::toDomain);

    // Convert to domain PagedResult
    return paginationPort.toDomainPagedResult(domainPage);
  }

  @Override
  public List<TransactionDomain> findByUserIdAndStatus(Long userId, TransactionStatus status) {
    log.debug("[TRANSACTION ADAPTER] Finding transactions for user: {} with status: {}", userId, status);
    return jpaRepository.findByUserIdAndStatusAndActiveTrue(userId, status)
        .stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<TransactionDomain> findByPhoneNumberAndActiveTrue(String phoneNumber) {
    log.debug("[TRANSACTION ADAPTER] Finding transactions for phone: {}", phoneNumber);
    return jpaRepository.findByPhoneNumberAndActiveTrueOrderByCreatedAtDesc(phoneNumber)
        .stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<TransactionDomain> findByExternalTransactionId(String externalTransactionId) {
    log.debug("[TRANSACTION ADAPTER] Finding transaction by external ID: {}", externalTransactionId);
    return jpaRepository.findByExternalTransactionIdAndActiveTrue(externalTransactionId)
        .map(mapper::toDomain);
  }

  @Override
  public List<TransactionDomain> findByUserAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
    log.debug("[TRANSACTION ADAPTER] Finding transactions for user: {} between {} and {}", userId, startDate,
        endDate);
    return jpaRepository.findByUserAndDateRange(userId, startDate, endDate)
        .stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public BigDecimal getTotalAmountByUser(Long userId) {
    log.debug("[TRANSACTION ADAPTER] Calculating total amount for user: {}", userId);
    return jpaRepository.getTotalAmountByUser(userId);
  }

  @Override
  public Long countByStatus(TransactionStatus status) {
    log.debug("[TRANSACTION ADAPTER] Counting transactions with status: {}", status);
    return jpaRepository.countByStatusAndActiveTrue(status);
  }

  @Override
  public Long countByUserId(Long userId) {
    log.debug("[TRANSACTION ADAPTER] Counting transactions for user: {}", userId);
    return jpaRepository.countByUserIdAndActiveTrue(userId);
  }

  @Override
  public List<TransactionDomain> findLatestByUserId(Long userId, int limit) {
    log.debug("[TRANSACTION ADAPTER] Finding latest {} transactions for user: {}", limit, userId);
    return jpaRepository.findTop10ByUserIdAndActiveTrueOrderByCreatedAtDesc(userId)
        .stream()
        .limit(limit)
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public boolean existsById(Long id) {
    log.debug("[TRANSACTION ADAPTER] Checking if transaction exists with ID: {}", id);
    return jpaRepository.existsById(id);
  }

  @Override
  public void deleteById(Long id) {
    log.debug("[TRANSACTION ADAPTER] Soft deleting transaction with ID: {}", id);
    jpaRepository.findById(id).ifPresent(entity -> {
      entity.setActive(false);
      jpaRepository.save(entity);
    });
  }

  @Override
  public List<TransactionDomain> findByUserId(Long userId) {
    log.debug("[TRANSACTION ADAPTER] Finding all transactions for user: {}", userId);
    return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId)
        .stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<TransactionDomain> findByUserIdAndActiveTrue(Long userId) {
    log.debug("[TRANSACTION ADAPTER] Finding active transactions for user: {}", userId);
    return jpaRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId)
        .stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<TransactionDomain> updateStatus(Long id, TransactionStatus status) {
    log.debug("[TRANSACTION ADAPTER] Updating transaction {} to status: {}", id, status);
    return jpaRepository.findById(id)
        .map(entity -> {
          entity.setStatus(status);
          entity.setUpdatedAt(LocalDateTime.now());
          TransactionEntity saved = jpaRepository.save(entity);
          return mapper.toDomain(saved);
        });
  }
}
