package com.loschicharros.api.common.error;

/**
 * Un campo es válido por sí solo pero no encaja con el resto de la petición (por ejemplo, una
 * temporada que termina antes de empezar): se traduce a 400 con el mismo formato que los errores de
 * Bean Validation, para que el formulario lo muestre junto al campo.
 */
public class InvalidFieldException extends RuntimeException {

  private final String field;

  public InvalidFieldException(String field, String message) {
    super(message);
    this.field = field;
  }

  public String getField() {
    return field;
  }
}
