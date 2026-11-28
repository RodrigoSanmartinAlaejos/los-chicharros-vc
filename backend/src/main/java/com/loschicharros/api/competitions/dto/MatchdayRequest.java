package com.loschicharros.api.competitions.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Datos para crear o editar una jornada (contrato OpenAPI, {@code MatchdayRequest}). La fecha es
 * opcional, pero si llega tiene que caer dentro de la temporada de la competición.
 */
public record MatchdayRequest(
    @NotNull Long competitionId,
    @NotNull @Min(1) @Max(99) Integer number,
    LocalDate scheduledDate) {}
