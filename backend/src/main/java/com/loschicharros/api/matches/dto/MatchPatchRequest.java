package com.loschicharros.api.matches.dto;

import com.loschicharros.api.matches.MatchStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.Instant;

/**
 * Cambio de un partido (contrato OpenAPI, {@code MatchPatchRequest}): aplazarlo, cancelarlo o
 * volver a programarlo (estado y hora), o registrar su resultado (sets de cada equipo, que lo dejan
 * finalizado). Lo que no llega no cambia.
 */
public record MatchPatchRequest(
    MatchStatus status,
    Instant startsAt,
    @Min(0) @Max(3) Integer homeSets,
    @Min(0) @Max(3) Integer awaySets) {

  /** Si trae marcador (alguno de los dos sets). */
  public boolean hasScore() {
    return homeSets != null || awaySets != null;
  }
}
