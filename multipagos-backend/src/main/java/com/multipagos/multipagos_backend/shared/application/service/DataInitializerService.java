package com.multipagos.multipagos_backend.shared.application.service;

import com.multipagos.multipagos_backend.auth.application.service.UserService;
import com.multipagos.multipagos_backend.auth.domain.model.User;
import com.multipagos.multipagos_backend.auth.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializerService implements CommandLineRunner {

  private final UserService userService;
  private final UserRepository userRepository;

  @Override
  public void run(String... args) throws Exception {
    log.info("[DATA INITIALIZER] Starting data initialization");
    initializeTestUsers();
    log.info("[DATA INITIALIZER] Data initialization completed");
  }

  private void initializeTestUsers() {
    try {
      if (!userRepository.existsByEmail("test@multipagos.com")) {
        User testUser = new User();
        testUser.setName("Usuario Prueba");
        testUser.setEmail("test@multipagos.com");
        testUser.setPassword("password123");
        testUser.setPhoneNumber("3001234567");

        userService.createUser(testUser);
        log.info("[DATA INITIALIZER] Test user created: test@multipagos.com");
      } else {
        log.info("[DATA INITIALIZER] Test user already exists: test@multipagos.com");
      }

      if (!userRepository.existsByEmail("admin@multipagos.com")) {
        User adminUser = new User();
        adminUser.setName("Admin Sistema");
        adminUser.setEmail("admin@multipagos.com");
        adminUser.setPassword("admin123");
        adminUser.setPhoneNumber("3007654321");

        userService.createUser(adminUser);
        log.info("[DATA INITIALIZER] Admin user created: admin@multipagos.com");
      } else {
        log.info("[DATA INITIALIZER] Admin user already exists: admin@multipagos.com");
      }

    } catch (Exception e) {
      log.error("[DATA INITIALIZER] Error initializing test users | error: {}", e.getMessage(), e);
    }
  }
}
