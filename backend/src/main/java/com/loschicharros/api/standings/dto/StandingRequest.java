package com.loschicharros.api.standings.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Una fila de la clasificación (contrato OpenAPI, {@code StandingRequest}). Los puntos pueden ser
 * negativos por sanciones; lo demás, no. Los máximos son los de las columnas SMALLINT.
 */
public record StandingRequest(
    @NotNull Long teamId,
    @NotNull @Min(-999) @Max(999) Integer points,
    @NotNull @Min(0) @Max(999) Integer played,
    @NotNull @Min(0) @Max(999) Integer won,
    @NotNull @Min(0) @Max(999) Integer lost,
    @NotNull @Min(0) @Max(9999) Integer setsWon,
    @NotNull @Min(0) @Max(9999) Integer setsLost,
    @NotNull @Min(0) @Max(999) Integer penaltyPoints) {}
