package com.loschicharros.api.common.error;

/**
 * Demasiadas peticiones en poco tiempo: se traduce a 429 con la cabecera {@code Retry-After}
 * (docs/05-api/README.md, «Métodos y códigos»).
 */
public class TooManyRequestsException extends RuntimeException {

  private final long retryAfterSeconds;

  public TooManyRequestsException(long retryAfterSeconds) {
    super(
        "Demasiadas peticiones. Espera "
            + retryAfterSeconds
            + (retryAfterSeconds == 1 ? " segundo" : " segundos")
            + " y vuelve a probar");
    this.retryAfterSeconds = retryAfterSeconds;
  }

  public long getRetryAfterSeconds() {
    return retryAfterSeconds;
  }
}
