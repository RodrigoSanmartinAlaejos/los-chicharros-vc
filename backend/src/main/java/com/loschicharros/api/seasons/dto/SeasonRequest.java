package com.loschicharros.api.seasons.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

/** Datos para crear o editar una temporada (contrato OpenAPI, {@code SeasonRequest}). */
public record SeasonRequest(
    @NotBlank @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "debe tener el formato 2026-27")
        String name,
    @NotNull LocalDate startDate,
    @NotNull LocalDate endDate,
    Boolean active) {

  /** {@code active} es opcional y vale {@code false} si no se envía. */
  public boolean activeOrDefault() {
    return Boolean.TRUE.equals(active);
  }
}
