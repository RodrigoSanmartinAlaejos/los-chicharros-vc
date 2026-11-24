package com.loschicharros.api.auth;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Duración de la sesión ({@code app.session.*}, reglas técnicas, regla 3 de autenticación).
 *
 * @param refreshTokenTtl cuánto vale cada refresh token sin renovarse (2 horas): es el tiempo de
 *     inactividad tras el que hay que volver a entrar
 * @param maxDuration máximo de una sesión desde el login, aunque se renueve (7 días)
 * @param cookieSecure si la cookie lleva {@code Secure}; los navegadores la aceptan también en
 *     {@code http://localhost}, así que solo se desactiva para casos raros de pruebas
 */
@ConfigurationProperties("app.session")
public record SessionProperties(
    Duration refreshTokenTtl, Duration maxDuration, Boolean cookieSecure) {

  public SessionProperties {
    refreshTokenTtl = refreshTokenTtl == null ? Duration.ofHours(2) : refreshTokenTtl;
    maxDuration = maxDuration == null ? Duration.ofDays(7) : maxDuration;
    cookieSecure = cookieSecure == null || cookieSecure;
  }
}
