package com.loschicharros.api.common.error;

/**
 * La petición choca con el estado actual de los datos (por ejemplo, borrar una categoría que tiene
 * equipos): se traduce a 409. El mensaje se muestra al usuario, así que va en español y sin datos
 * internos.
 */
public class ConflictException extends RuntimeException {

  public ConflictException(String message) {
    super(message);
  }
}
