package com.loschicharros.api.security;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuración de los access tokens ({@code app.jwt.*}). El secreto sale de la variable {@code
 * JWT_SECRET} y tiene que tener al menos 32 bytes, el mínimo para firmar con HS256.
 *
 * @param secret clave con la que se firman y se comprueban los tokens
 * @param accessTokenTtl validez del access token (15 minutos, reglas técnicas)
 * @param issuer valor de la claim {@code iss}
 */
@ConfigurationProperties("app.jwt")
public record JwtProperties(String secret, Duration accessTokenTtl, String issuer) {

  static final int MIN_SECRET_BYTES = 32;

  public JwtProperties {
    if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_BYTES) {
      throw new IllegalStateException(
          "JWT_SECRET tiene que tener al menos "
              + MIN_SECRET_BYTES
              + " caracteres. Genera uno, por ejemplo, con: openssl rand -base64 48");
    }
    if (accessTokenTtl == null) {
      accessTokenTtl = Duration.ofMinutes(15);
    }
    if (issuer == null || issuer.isBlank()) {
      issuer = "los-chicharros-api";
    }
  }
}
