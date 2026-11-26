package com.loschicharros.api.auth;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Clock;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Intentos de login fallidos por email e IP, en memoria (Caffeine). Tras {@code maxFailures} fallos
 * seguidos, esa combinación queda bloqueada {@code blockDuration}: el login responde el mismo 401
 * genérico aunque la contraseña sea buena. Desde otra IP se puede seguir entrando, así que nadie
 * puede dejar fuera al administrador probando contraseñas con su email.
 *
 * <p>Los contadores se pierden al reiniciar la API; es aceptable para un club pequeño y evita una
 * tabla más (reglas técnicas, «Decisiones»).
 */
@Component
@EnableConfigurationProperties(LoginProtectionProperties.class)
public class LoginAttempts {

  private record Attempts(int failures, Instant blockedUntil) {}

  private final LoginProtectionProperties properties;
  private final Clock clock;
  private final Cache<String, Attempts> attempts;

  @Autowired
  public LoginAttempts(LoginProtectionProperties properties) {
    this(properties, Clock.systemUTC());
  }

  LoginAttempts(LoginProtectionProperties properties, Clock clock) {
    this.properties = properties;
    this.clock = clock;
    // Un fallo se olvida si no hay otro en el tiempo de bloqueo; un bloqueo dura justo ese tiempo
    this.attempts =
        Caffeine.newBuilder()
            .expireAfterWrite(properties.blockDuration())
            .ticker(() -> clock.millis() * 1_000_000)
            .maximumSize(100_000)
            .build();
  }

  /** Si ese email está bloqueado desde esa IP. */
  public boolean isBlocked(String email, String ip) {
    var current = attempts.getIfPresent(key(email, ip));
    return current != null
        && current.blockedUntil() != null
        && clock.instant().isBefore(current.blockedUntil());
  }

  /** Cuenta un fallo; al llegar al máximo, bloquea la combinación. */
  public void recordFailure(String email, String ip) {
    attempts
        .asMap()
        .compute(
            key(email, ip),
            (key, current) -> {
              var failures = (current == null ? 0 : current.failures()) + 1;
              var blockedUntil =
                  failures >= properties.maxFailures()
                      ? clock.instant().plus(properties.blockDuration())
                      : null;
              return new Attempts(failures, blockedUntil);
            });
  }

  /** Un login correcto pone el contador a cero: los fallos tienen que ser seguidos. */
  public void recordSuccess(String email, String ip) {
    attempts.invalidate(key(email, ip));
  }

  /** Olvida todos los intentos (lo usan los tests para empezar de cero). */
  public void clear() {
    attempts.invalidateAll();
  }

  private static String key(String email, String ip) {
    return email + "|" + ip;
  }
}
