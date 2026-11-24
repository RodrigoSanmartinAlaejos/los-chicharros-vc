package com.loschicharros.api.security;

import java.time.Instant;
import java.util.List;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/** Emite los access tokens JWT, firmados con HS256 y con el id del usuario como {@code sub}. */
@Service
public class TokenService {

  private final JwtEncoder encoder;
  private final JwtProperties properties;

  public TokenService(JwtEncoder encoder, JwtProperties properties) {
    this.encoder = encoder;
    this.properties = properties;
  }

  /**
   * Crea un access token para un usuario: el id, el email (para que el panel muestre quién tiene la
   * sesión tras recargar) y los roles. El token solo vive en la memoria del navegador.
   *
   * @param userId id del usuario, que va en la claim {@code sub}
   * @param email email del usuario, en la claim {@code email}
   * @param roles roles sin el prefijo {@code ROLE_}, por ejemplo {@code ADMIN}
   * @return el token firmado
   */
  public String issueAccessToken(long userId, String email, List<String> roles) {
    var now = Instant.now();
    var claims =
        JwtClaimsSet.builder()
            .issuer(properties.issuer())
            .subject(Long.toString(userId))
            .issuedAt(now)
            .expiresAt(now.plus(properties.accessTokenTtl()))
            .claim("email", email)
            .claim(SecurityConfig.ROLES_CLAIM, roles)
            .build();
    var header = JwsHeader.with(MacAlgorithm.HS256).build();
    return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
  }

  /**
   * Segundos que dura un access token, para el campo {@code expiresIn} de la respuesta.
   *
   * @return la validez en segundos
   */
  public long accessTokenSeconds() {
    return properties.accessTokenTtl().toSeconds();
  }
}
