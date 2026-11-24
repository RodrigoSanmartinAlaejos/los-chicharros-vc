package com.loschicharros.api.auth;

import com.loschicharros.api.common.error.UnauthorizedException;

/**
 * El refresh token no vale: no llega, no existe, ha caducado o ya se usó. Siempre el mismo 401, y
 * la respuesta borra la cookie.
 */
public class InvalidSessionException extends UnauthorizedException {

  public static final String MESSAGE = "La sesión ha caducado. Vuelve a iniciar sesión";

  public InvalidSessionException() {
    super(MESSAGE);
  }
}
