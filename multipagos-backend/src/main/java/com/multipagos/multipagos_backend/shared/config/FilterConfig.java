package com.multipagos.multipagos_backend.shared.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

  private final SecurityHeadersConfig.SecurityHeadersFilter securityHeadersFilter;
  private final RateLimitingFilter rateLimitingFilter;
  private final SecurityMonitoringFilter securityMonitoringFilter;

  @Bean
  public FilterRegistrationBean<SecurityHeadersConfig.SecurityHeadersFilter> securityHeadersFilterRegistration() {
    FilterRegistrationBean<SecurityHeadersConfig.SecurityHeadersFilter> registration = new FilterRegistrationBean<>();

    registration.setFilter(securityHeadersFilter);
    registration.addUrlPatterns("/api/*");
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
    registration.setName("SecurityHeadersFilter");

    return registration;
  }

  @Bean
  public FilterRegistrationBean<SecurityMonitoringFilter> securityMonitoringFilterRegistration() {
    FilterRegistrationBean<SecurityMonitoringFilter> registration = new FilterRegistrationBean<>();

    registration.setFilter(securityMonitoringFilter);
    registration.addUrlPatterns("/api/*");
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
    registration.setName("SecurityMonitoringFilter");

    return registration;
  }

  @Bean
  public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilterRegistration() {
    FilterRegistrationBean<RateLimitingFilter> registration = new FilterRegistrationBean<>();

    registration.setFilter(rateLimitingFilter);
    registration.addUrlPatterns("/api/*");
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
    registration.setName("RateLimitingFilter");

    return registration;
  }
}
