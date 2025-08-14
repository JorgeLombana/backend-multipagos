package com.multipagos.multipagos_backend.topup.infrastructure.mapper;

import com.multipagos.multipagos_backend.auth.infrastructure.entity.UserEntity;
import com.multipagos.multipagos_backend.topup.domain.model.TransactionDomain;
import com.multipagos.multipagos_backend.topup.domain.model.valueobject.Amount;
import com.multipagos.multipagos_backend.topup.domain.model.valueobject.PhoneNumber;
import com.multipagos.multipagos_backend.topup.domain.model.valueobject.SupplierId;
import com.multipagos.multipagos_backend.topup.infrastructure.entity.TransactionEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Maps between TransactionDomain and TransactionEntity, handling value object conversions
 */
@Component
@RequiredArgsConstructor
public class TransactionMapper {

  private final EntityManager entityManager;

  public TransactionEntity toEntity(TransactionDomain domain) {
    if (domain == null) {
      return null;
    }

    TransactionEntity entity = new TransactionEntity();
    entity.setId(domain.getId());
    entity.setPhoneNumber(domain.getPhoneNumber() != null ? domain.getPhoneNumber().getValue() : null);
    entity.setAmount(domain.getAmount() != null ? domain.getAmount().getValue() : null);
    entity.setSupplierId(domain.getSupplierId() != null ? domain.getSupplierId().getValue() : null);
    entity.setSupplierName(domain.getSupplierName());
    entity.setStatus(domain.getStatus());
    entity.setExternalTransactionId(domain.getExternalTransactionId());
    entity.setResponseMessage(domain.getResponseMessage());
    entity.setResponseData(domain.getResponseData());
    entity.setCreatedAt(domain.getCreatedAt());
    entity.setUpdatedAt(domain.getUpdatedAt());
    entity.setActive(domain.getActive());

    if (domain.getUserId() != null) { // Handle user relationship
      UserEntity userEntity = entityManager.getReference(UserEntity.class, domain.getUserId());
      entity.setUser(userEntity);
    }

    return entity;
  }

  public TransactionDomain toDomain(TransactionEntity entity) {
    if (entity == null) {
      return null;
    }

    return TransactionDomain.builder()
        .id(entity.getId())
        .userId(entity.getUser() != null ? entity.getUser().getId() : null)
        .phoneNumber(entity.getPhoneNumber() != null ? PhoneNumber.of(entity.getPhoneNumber()) : null)
        .amount(entity.getAmount() != null ? Amount.of(entity.getAmount()) : null)
        .supplierId(entity.getSupplierId() != null ? SupplierId.of(entity.getSupplierId()) : null)
        .supplierName(entity.getSupplierName())
        .status(entity.getStatus())
        .externalTransactionId(entity.getExternalTransactionId())
        .responseMessage(entity.getResponseMessage())
        .responseData(entity.getResponseData())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .active(entity.getActive())
        .build();
  }
}
