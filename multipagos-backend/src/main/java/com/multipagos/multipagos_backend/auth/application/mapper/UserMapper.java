package com.multipagos.multipagos_backend.auth.application.mapper;

import com.multipagos.multipagos_backend.auth.domain.model.User;
import com.multipagos.multipagos_backend.auth.presentation.dto.RegisterRequest;
import org.springframework.stereotype.Component;

/**
 * Maps between presentation DTOs and domain entities
 * Following hexagonal architecture principles
 */
@Component
public class UserMapper {

  public User toDomain(RegisterRequest registerRequest) {
    User user = new User();
    user.setName(registerRequest.getName());
    user.setEmail(registerRequest.getEmail());
    user.setPassword(registerRequest.getPassword());
    user.setPhoneNumber(registerRequest.getPhoneNumber());
    return user;
  }
}
