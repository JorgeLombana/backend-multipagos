package com.multipagos.multipagos_backend.shared.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class SecurityMonitoringFilter extends OncePerRequestFilter {

  private static final List<String> SENSITIVE_ENDPOINTS = Arrays.asList(
      "/auth", "/topup", "/transactions"
  );

  private static final List<String> SUSPICIOUS_PATTERNS = Arrays.asList(
      "admin", "root", "test", "debug", "config", "backup", 
      ".env", "wp-admin", "phpMyAdmin", "console"
  );

  @Override
  protected void doFilterInternal(HttpServletRequest request, 
                                HttpServletResponse response, 
                                FilterChain filterChain) throws ServletException, IOException {

    String requestId = UUID.randomUUID().toString().substring(0, 8);
    long startTime = System.currentTimeMillis();

    response.setHeader("X-Request-ID", requestId);

    try {
      logIncomingRequest(request, requestId);
      detectSuspiciousActivity(request, requestId);
      
      filterChain.doFilter(request, response);
      
    } catch (Exception e) {
      logSecurityException(request, requestId, e);
      throw e;
    } finally {
      long executionTime = System.currentTimeMillis() - startTime;
      logResponse(request, response, requestId, executionTime);
    }
  }

  private void logIncomingRequest(HttpServletRequest request, String requestId) {
    String clientIP = getClientIP(request);
    String userAgent = request.getHeader("User-Agent");
    String method = request.getMethod();
    String uri = request.getRequestURI();
    String referer = request.getHeader("Referer");

    if (isSensitiveEndpoint(uri)) {
      log.info("[SECURITY REQUEST] ID: {} | IP: {} | Method: {} | URI: {} | UserAgent: {} | Referer: {} | Time: {}", 
               requestId, clientIP, method, uri, userAgent, referer, LocalDateTime.now());
    } else {
      log.debug("[REQUEST] ID: {} | IP: {} | Method: {} | URI: {}", 
                requestId, clientIP, method, uri);
    }
  }

  private void logResponse(HttpServletRequest request, HttpServletResponse response, 
                          String requestId, long executionTime) {
    String uri = request.getRequestURI();
    int status = response.getStatus();

    if (isSensitiveEndpoint(uri) || status >= 400) {
      log.info("[SECURITY RESPONSE] ID: {} | URI: {} | Status: {} | Time: {}ms", 
               requestId, uri, status, executionTime);
    }

    if (executionTime > 5000) {
      log.warn("[SLOW REQUEST] ID: {} | URI: {} | Time: {}ms | IP: {}", 
               requestId, uri, executionTime, getClientIP(request));
    }
  }

  private void detectSuspiciousActivity(HttpServletRequest request, String requestId) {
    String uri = request.getRequestURI().toLowerCase();
    String queryString = request.getQueryString();
    String clientIP = getClientIP(request);

    for (String pattern : SUSPICIOUS_PATTERNS) {
      if (uri.contains(pattern)) {
        log.warn("[SUSPICIOUS ACTIVITY] ID: {} | IP: {} | URI: {} | Pattern: {} | UserAgent: {}", 
                 requestId, clientIP, uri, pattern, request.getHeader("User-Agent"));
        break;
      }
    }

    if (queryString != null && containsSqlInjectionPattern(queryString)) {
      log.error("[SQL INJECTION ATTEMPT] ID: {} | IP: {} | Query: {} | UserAgent: {}", 
                requestId, clientIP, queryString, request.getHeader("User-Agent"));
    }

    if (queryString != null && containsXSSPattern(queryString)) {
      log.error("[XSS ATTEMPT] ID: {} | IP: {} | Query: {} | UserAgent: {}", 
                requestId, clientIP, queryString, request.getHeader("User-Agent"));
    }

    if (request.getHeader("User-Agent") == null) {
      log.warn("[NO USER AGENT] ID: {} | IP: {} | URI: {}", 
               requestId, clientIP, uri);
    }

    String serverName = request.getServerName();
    if (serverName != null && (serverName.contains("localhost") || serverName.contains("127.0.0.1"))) {
      log.debug("[LOCALHOST REQUEST] ID: {} | IP: {} | Server: {} | URI: {}", 
                requestId, clientIP, serverName, uri);
    }
  }

  private void logSecurityException(HttpServletRequest request, String requestId, Exception e) {
    log.error("[SECURITY EXCEPTION] ID: {} | IP: {} | URI: {} | Exception: {} | Message: {}", 
              requestId, getClientIP(request), request.getRequestURI(), 
              e.getClass().getSimpleName(), e.getMessage());
  }

  private boolean isSensitiveEndpoint(String uri) {
    return SENSITIVE_ENDPOINTS.stream().anyMatch(uri::contains);
  }

  private boolean containsSqlInjectionPattern(String input) {
    String lowerInput = input.toLowerCase();
    return lowerInput.contains("union") || lowerInput.contains("select") || 
           lowerInput.contains("insert") || lowerInput.contains("delete") || 
           lowerInput.contains("update") || lowerInput.contains("drop") ||
           lowerInput.contains("'") || lowerInput.contains("--") ||
           lowerInput.contains(";");
  }

  private boolean containsXSSPattern(String input) {
    String lowerInput = input.toLowerCase();
    return lowerInput.contains("<script") || lowerInput.contains("javascript:") || 
           lowerInput.contains("onload=") || lowerInput.contains("onerror=") ||
           lowerInput.contains("onclick=") || lowerInput.contains("eval(");
  }

  private String getClientIP(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }
    
    String xRealIP = request.getHeader("X-Real-IP");
    if (xRealIP != null && !xRealIP.isEmpty()) {
      return xRealIP;
    }
    
    return request.getRemoteAddr();
  }
}
