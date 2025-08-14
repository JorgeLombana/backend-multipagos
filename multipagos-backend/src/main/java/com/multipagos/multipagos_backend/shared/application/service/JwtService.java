package com.multipagos.multipagos_backend.shared.application.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.expiration}")
  private Long jwtExpirationMs;

  public String generateToken(String email, Long userId, String name) {
    log.info("[JWT SERVICE] Generating token for user: {} | ID: {}", email, userId);

    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", userId);
    claims.put("name", name);
    claims.put("email", email);

    String token = createToken(claims, email);
    log.info("[JWT SERVICE] Token generated successfully for user: {}", email);

    return token;
  }

  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .claims(claims)
        .subject(subject)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(getSigningKey())
        .compact();
  }

  public Boolean validateToken(String token, String email) {
    try {
      log.info("[JWT SERVICE] Validating token for user: {}", email);
      final String tokenEmail = extractEmail(token);
      boolean isValid = tokenEmail.equals(email) && !isTokenExpired(token);
      log.info("[JWT SERVICE] Token validation result for user: {} | valid: {}", email, isValid);
      return isValid;
    } catch (Exception e) {
      log.error("[JWT SERVICE] Error validating token for user: {} | error: {}", email, e.getMessage());
      return false;
    }
  }

  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Long extractUserId(String token) {
    return extractClaim(token, claims -> claims.get("userId", Long.class));
  }

  public String extractName(String token) {
    return extractClaim(token, claims -> claims.get("name", String.class));
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
