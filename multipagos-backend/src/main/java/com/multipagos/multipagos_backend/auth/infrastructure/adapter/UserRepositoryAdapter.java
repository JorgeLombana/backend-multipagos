package com.multipagos.multipagos_backend.auth.infrastructure.adapter;

import com.multipagos.multipagos_backend.auth.domain.model.UserDomain;
import com.multipagos.multipagos_backend.auth.domain.port.out.UserRepositoryPort;
import com.multipagos.multipagos_backend.auth.infrastructure.entity.UserEntity;
import com.multipagos.multipagos_backend.auth.infrastructure.repository.UserEntityRepository;
import com.multipagos.multipagos_backend.auth.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Infrastructure adapter implementing user repository port.
 * Bridges domain and JPA layers, handles entity-domain mapping.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryAdapter implements UserRepositoryPort {

  private final UserEntityRepository jpaRepository;
  private final UserMapper mapper;

  @Override
  public UserDomain save(UserDomain user) {
    log.debug("Saving user domain to database");
    UserEntity entity = mapper.toEntity(user);
    UserEntity savedEntity = jpaRepository.save(entity);
    UserDomain savedDomain = mapper.toDomain(savedEntity);
    log.debug("User saved with ID: {}", savedDomain.getId());
    return savedDomain;
  }

  @Override
  public Optional<UserDomain> findById(Long id) {
    log.debug("Finding user by ID: {}", id);
    return jpaRepository.findById(id)
        .map(mapper::toDomain);
  }

  @Override
  public Optional<UserDomain> findByEmail(String email) {
    log.debug("Finding user by email: {}", email);
    return jpaRepository.findByEmail(email)
        .map(mapper::toDomain);
  }

  @Override
  public boolean existsByEmail(String email) {
    log.debug("Checking if user exists with email: {}", email);
    return jpaRepository.existsByEmail(email);
  }

  @Override
  public boolean existsById(Long id) {
    log.debug("Checking if user exists with ID: {}", id);
    return jpaRepository.existsById(id);
  }

  @Override
  public void deleteById(Long id) {
    log.debug("Soft deleting user with ID: {}", id);
    jpaRepository.findById(id).ifPresent(entity -> {
      entity.setActive(false);
      jpaRepository.save(entity);
    });
  }

  @Override
  public Long countActiveUsers() {
    log.debug("Counting active users");
    return jpaRepository.countActiveUsers();
  }
}
