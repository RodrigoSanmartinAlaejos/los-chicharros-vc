package com.loschicharros.api.auth;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Protección del login contra fuerza bruta ({@code app.login-protection.*}, reglas técnicas, regla
 * 5 de autenticación).
 *
 * @param maxFailures fallos seguidos para un email desde una IP que bloquean esa combinación (5)
 * @param blockDuration cuánto dura el bloqueo (15 minutos)
 * @param requestsPerMinute peticiones por minuto que admite cada IP en {@code /api/v1/auth/**} (20)
 */
@ConfigurationProperties("app.login-protection")
public record LoginProtectionProperties(
    Integer maxFailures, Duration blockDuration, Integer requestsPerMinute) {

  public LoginProtectionProperties {
    maxFailures = maxFailures == null ? 5 : maxFailures;
    blockDuration = blockDuration == null ? Duration.ofMinutes(15) : blockDuration;
    requestsPerMinute = requestsPerMinute == null ? 20 : requestsPerMinute;
  }
}
