package com.loschicharros.api.auth;

import com.loschicharros.api.auth.dto.AccessTokenResponse;
import java.time.Instant;

/**
 * Resultado de un login o una renovación: el access token para el cuerpo y el refresh token para la
 * cookie.
 *
 * @param accessToken respuesta con el access token
 * @param refreshToken refresh token en claro; solo sale en la cookie
 * @param refreshExpiresAt cuándo caduca, para la duración de la cookie
 */
public record IssuedSession(
    AccessTokenResponse accessToken, String refreshToken, Instant refreshExpiresAt) {

  /** Sin los tokens, para que nunca acaben en un log. */
  @Override
  public String toString() {
    return "IssuedSession[refreshExpiresAt=" + refreshExpiresAt + "]";
  }
}
