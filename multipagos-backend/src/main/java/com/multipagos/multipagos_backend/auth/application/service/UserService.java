package com.multipagos.multipagos_backend.auth.application.service;

import com.multipagos.multipagos_backend.auth.domain.model.User;
import com.multipagos.multipagos_backend.auth.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(User user) {
        log.info("[USER SERVICE] Creating new user with email: {}", user.getEmail());

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Usuario ya existe con este email");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("[USER SERVICE] User created successfully with ID: {}", savedUser.getId());

        return savedUser;
    }

    @Transactional(readOnly = true)
    public Long countActiveUsers() {
        return userRepository.countActiveUsers();
    }

    @Transactional(readOnly = true)
    public boolean validatePassword(String email, String rawPassword) {
        log.info("[USER SERVICE] Validating password for email: {}", email);

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            boolean isValid = passwordEncoder.matches(rawPassword, userOpt.get().getPassword());
            log.info("[USER SERVICE] Password validation result for email: {} | valid: {}", email, isValid);
            return isValid;
        }

        log.warn("[USER SERVICE] User not found for email: {}", email);
        return false;
    }
}
