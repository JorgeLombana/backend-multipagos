package com.multipagos.multipagos_backend.auth.infrastructure.adapter;

import com.multipagos.multipagos_backend.auth.domain.port.out.PasswordEncoderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Infrastructure adapter implementing password encoding operations.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordEncoderAdapter implements PasswordEncoderPort {

  private final PasswordEncoder passwordEncoder;

  @Override
  public String encode(String rawPassword) {
    log.debug("Encoding password");
    return passwordEncoder.encode(rawPassword);
  }

  @Override
  public boolean matches(String rawPassword, String encodedPassword) {
    log.debug("Verifying password");
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  @Override
  public boolean upgradeEncoding(String encodedPassword) {
    log.debug("Checking if password encoding should be upgraded");
    return passwordEncoder.upgradeEncoding(encodedPassword);
  }
}
