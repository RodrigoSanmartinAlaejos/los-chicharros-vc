package com.loschicharros.api.competitions.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Datos para crear o editar una competición (contrato OpenAPI, {@code CompetitionRequest}). Los
 * puntos de sanción por incomparecencia son opcionales y valen 1.
 */
public record CompetitionRequest(
    @NotNull Long seasonId,
    @NotNull Long categoryId,
    @NotBlank @Size(max = 100) String name,
    @Size(max = 100) String federation,
    @Min(0) @Max(10) Integer forfeitPenaltyPoints) {

  /** Puntos de sanción que llegan, o 1 si no llegan. */
  public int forfeitPenaltyPointsOrDefault() {
    return forfeitPenaltyPoints == null ? 1 : forfeitPenaltyPoints;
  }
}
