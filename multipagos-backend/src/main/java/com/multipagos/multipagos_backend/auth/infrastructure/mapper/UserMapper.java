package com.multipagos.multipagos_backend.auth.infrastructure.mapper;

import com.multipagos.multipagos_backend.auth.domain.model.UserDomain;
import com.multipagos.multipagos_backend.auth.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between user domain and entity models.
 */
@Component
public class UserMapper {

  public UserEntity toEntity(UserDomain domain) {
    UserEntity entity = new UserEntity();
    entity.setId(domain.getId());
    entity.setName(domain.getName());
    entity.setEmail(domain.getEmail());
    entity.setPassword(domain.getPassword());
    entity.setCreatedAt(domain.getCreatedAt());
    entity.setUpdatedAt(domain.getUpdatedAt());
    entity.setActive(domain.getActive());
    return entity;
  }

  public UserDomain toDomain(UserEntity entity) {
    return UserDomain.builder()
        .id(entity.getId())
        .name(entity.getName())
        .email(entity.getEmail())
        .password(entity.getPassword())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .active(entity.getActive())
        .build();
  }
}
