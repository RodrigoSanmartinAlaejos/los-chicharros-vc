package com.loschicharros.api.auth;

import com.loschicharros.api.common.error.UnauthorizedException;

/**
 * Email o contraseña incorrectos, o cuenta que no puede entrar: siempre el mismo 401, para no
 * revelar si un email existe (reglas técnicas, regla 6 de autenticación).
 */
public class InvalidCredentialsException extends UnauthorizedException {

  public static final String MESSAGE = "Email o contraseña incorrectos";

  public InvalidCredentialsException() {
    super(MESSAGE);
  }
}
