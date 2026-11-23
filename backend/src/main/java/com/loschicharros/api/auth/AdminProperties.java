package com.loschicharros.api.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Administrador inicial ({@code app.admin.*}), desde {@code ADMIN_EMAIL} y {@code ADMIN_PASSWORD}.
 *
 * @param email email con el que entra
 * @param password contraseña inicial; solo se usa si aún no hay ningún administrador
 */
@ConfigurationProperties("app.admin")
public record AdminProperties(String email, String password) {

  /** Sin la contraseña, para que nunca acabe en un log. */
  @Override
  public String toString() {
    return "AdminProperties[email=" + email + "]";
  }
}
