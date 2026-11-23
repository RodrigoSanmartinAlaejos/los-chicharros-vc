package com.loschicharros.api.common.error;

/**
 * Falta una sesión válida o las credenciales no son correctas: se traduce a 401. El mensaje se
 * muestra al usuario, así que va en español y sin datos internos.
 */
public class UnauthorizedException extends RuntimeException {

  public UnauthorizedException(String message) {
    super(message);
  }
}
