package com.loschicharros.api.common.error;

/**
 * El recurso pedido no existe: se traduce a 404. Cada recurso puede tener su subclase (por ejemplo,
 * {@code MatchNotFoundException}).
 */
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }

  /** Mensaje estándar: «No existe temporada con id 5». */
  public ResourceNotFoundException(String resource, Object id) {
    this("No existe " + resource + " con id " + id);
  }
}
