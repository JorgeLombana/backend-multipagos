package com.multipagos.multipagos_backend.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityHeadersConfig {

  @Bean
  public SecurityHeadersFilter securityHeadersFilter() {
    return new SecurityHeadersFilter();
  }

  public static class SecurityHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {

      response.setHeader("X-Content-Type-Options", "nosniff");
      response.setHeader("X-Frame-Options", "DENY");
      response.setHeader("X-XSS-Protection", "1; mode=block");
      response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");

      response.setHeader("Content-Security-Policy", 
          "default-src 'self'; " +
          "script-src 'self'; " +
          "style-src 'self' 'unsafe-inline'; " +
          "img-src 'self' data: https:; " +
          "connect-src 'self'; " +
          "font-src 'self'; " +
          "object-src 'none'; " +
          "media-src 'self'; " +
          "frame-src 'none';");

      response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

      response.setHeader("Permissions-Policy", 
          "camera=(), " +
          "microphone=(), " +
          "geolocation=(), " +
          "interest-cohort=()");

      response.setHeader("X-Permitted-Cross-Domain-Policies", "none");

      if (request.getRequestURI().contains("/auth") || 
          request.getRequestURI().contains("/topup")) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
      }

      response.setHeader("Server", "");

      filterChain.doFilter(request, response);
    }
  }
}
