package com.loschicharros.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Credenciales del login (contrato OpenAPI, {@code LoginRequest}). */
public record LoginRequest(
    @NotBlank @Email @Size(max = 254) String email, @NotBlank @Size(max = 128) String password) {

  /** Sin la contraseña, para que nunca acabe en un log. */
  @Override
  public String toString() {
    return "LoginRequest[email=" + email + "]";
  }
}
