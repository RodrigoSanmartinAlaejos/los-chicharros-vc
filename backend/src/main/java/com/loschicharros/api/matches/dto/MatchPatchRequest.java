package com.loschicharros.api.matches.dto;

import com.loschicharros.api.matches.MatchStatus;
import java.time.Instant;

/**
 * Cambio de estado o de hora de un partido (contrato OpenAPI, {@code MatchPatchRequest}): aplazar,
 * cancelar o volver a programar. Lo que no llega no cambia.
 */
public record MatchPatchRequest(MatchStatus status, Instant startsAt) {}
