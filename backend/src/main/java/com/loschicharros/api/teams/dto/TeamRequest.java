package com.loschicharros.api.teams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Datos para crear o editar un equipo (contrato OpenAPI, {@code TeamRequest}). {@code ownClub} es
 * opcional y vale {@code false}: lo normal es dar de alta rivales.
 */
public record TeamRequest(
    @NotNull Long seasonId,
    @NotNull Long categoryId,
    @NotBlank @Size(max = 100) String name,
    Boolean ownClub) {

  /** El equipo es del club solo si llega {@code true}. */
  public boolean isOwnClub() {
    return Boolean.TRUE.equals(ownClub);
  }
}
