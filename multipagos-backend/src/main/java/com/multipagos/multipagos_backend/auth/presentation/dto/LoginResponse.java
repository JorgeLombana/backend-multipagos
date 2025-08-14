package com.multipagos.multipagos_backend.auth.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

  private String token;
  private String type = "Bearer";
  private UserInfo user;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UserInfo {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
  }

  public LoginResponse(String token, UserInfo user) {
    this.token = token;
    this.user = user;
  }
}
