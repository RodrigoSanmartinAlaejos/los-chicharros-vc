package com.loschicharros.api.matches.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/**
 * Datos para crear o sustituir un partido (contrato OpenAPI, {@code MatchRequest}). El pabellón es
 * opcional: solo se indica si es uno de los del club.
 */
public record MatchRequest(
    @NotNull Long matchdayId,
    @NotNull Long homeTeamId,
    @NotNull Long awayTeamId,
    Long venueId,
    @NotNull Instant startsAt) {}
