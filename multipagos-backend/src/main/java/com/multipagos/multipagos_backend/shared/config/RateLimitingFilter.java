package com.multipagos.multipagos_backend.shared.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

  private static final int AUTH_REQUESTS_PER_MINUTE = 5;
  private static final int TOPUP_REQUESTS_PER_MINUTE = 10;
  private static final int READ_REQUESTS_PER_MINUTE = 60;
  private static final int DEFAULT_REQUESTS_PER_MINUTE = 30;
  
  private static final String RATE_LIMIT_RESPONSE = """
      {
        "error": "Rate limit exceeded",
        "message": "Too many requests. Please try again later.",
        "code": "RATE_LIMIT_EXCEEDED"
      }
      """;

  private final Map<String, Bucket> clientBuckets = new ConcurrentHashMap<>();

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain) throws ServletException, IOException {

    String clientIdentifier = extractClientIdentifier(request);
    String endpoint = request.getRequestURI();
    Bucket bucket = getOrCreateBucket(clientIdentifier, endpoint);

    if (bucket.tryConsume(1)) {
      logAllowedRequest(clientIdentifier, endpoint);
      filterChain.doFilter(request, response);
    } else {
      handleRateLimitExceeded(clientIdentifier, endpoint, response);
    }
  }

  private String extractClientIdentifier(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
    }

    return extractClientIP(request);
  }

  private String extractClientIP(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (isValidHeader(xForwardedFor)) {
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIP = request.getHeader("X-Real-IP");
    if (isValidHeader(xRealIP)) {
      return xRealIP;
    }

    return request.getRemoteAddr();
  }

  private boolean isValidHeader(String headerValue) {
    return headerValue != null && !headerValue.trim().isEmpty();
  }

  private Bucket getOrCreateBucket(String clientId, String endpoint) {
    String bucketKey = buildBucketKey(clientId, endpoint);
    return clientBuckets.computeIfAbsent(bucketKey, key -> createBucketForEndpoint(endpoint));
  }

  private String buildBucketKey(String clientId, String endpoint) {
    return clientId + ":" + categorizeEndpoint(endpoint);
  }

  private Bucket createBucketForEndpoint(String endpoint) {
    EndpointCategory category = categorizeEndpoint(endpoint);
    Bandwidth bandwidth = createBandwidthForCategory(category);
    
    return Bucket.builder()
        .addLimit(bandwidth)
        .build();
  }

  private EndpointCategory categorizeEndpoint(String endpoint) {
    if (endpoint.contains("/auth")) {
      return EndpointCategory.AUTH;
    }
    if (endpoint.contains("/topup")) {
      return EndpointCategory.FINANCIAL;
    }
    if (endpoint.contains("/suppliers") || endpoint.contains("/health")) {
      return EndpointCategory.READ_ONLY;
    }
    return EndpointCategory.GENERAL;
  }

  private Bandwidth createBandwidthForCategory(EndpointCategory category) {
    return switch (category) {
      case AUTH -> Bandwidth.classic(AUTH_REQUESTS_PER_MINUTE, 
                                   Refill.intervally(AUTH_REQUESTS_PER_MINUTE, Duration.ofMinutes(1)));
      case FINANCIAL -> Bandwidth.classic(TOPUP_REQUESTS_PER_MINUTE, 
                                        Refill.intervally(TOPUP_REQUESTS_PER_MINUTE, Duration.ofMinutes(1)));
      case READ_ONLY -> Bandwidth.classic(READ_REQUESTS_PER_MINUTE, 
                                        Refill.intervally(READ_REQUESTS_PER_MINUTE, Duration.ofMinutes(1)));
      case GENERAL -> Bandwidth.classic(DEFAULT_REQUESTS_PER_MINUTE, 
                                      Refill.intervally(DEFAULT_REQUESTS_PER_MINUTE, Duration.ofMinutes(1)));
    };
  }

  private void logAllowedRequest(String clientId, String endpoint) {
    log.debug("[RATE LIMIT] Request allowed for client: {} | endpoint: {}", clientId, endpoint);
  }

  private void handleRateLimitExceeded(String clientId, String endpoint, HttpServletResponse response) 
      throws IOException {
    
    log.warn("[RATE LIMIT] Rate limit exceeded for client: {} | endpoint: {}", clientId, endpoint);
    
    response.setStatus(429); // Too Many Requests
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(RATE_LIMIT_RESPONSE);
  }

  private enum EndpointCategory {
    AUTH, FINANCIAL, READ_ONLY, GENERAL
  }
}
