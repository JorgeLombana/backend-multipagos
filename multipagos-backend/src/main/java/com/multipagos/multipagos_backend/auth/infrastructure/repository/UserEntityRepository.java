package com.multipagos.multipagos_backend.auth.infrastructure.repository;

import com.multipagos.multipagos_backend.auth.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA Repository for UserEntity
 * Handles database operations for user entities
 */
@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmailAndActiveTrue(String email);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.active = true")
    Long countActiveUsers();
}
