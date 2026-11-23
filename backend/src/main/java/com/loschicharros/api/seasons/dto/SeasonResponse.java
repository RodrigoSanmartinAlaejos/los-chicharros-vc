package com.loschicharros.api.seasons.dto;

import com.loschicharros.api.seasons.Season;
import java.time.LocalDate;

/** Temporada tal como la devuelve la API (contrato OpenAPI, {@code SeasonResponse}). */
public record SeasonResponse(
    Long id, String name, LocalDate startDate, LocalDate endDate, boolean active) {

  public static SeasonResponse from(Season season) {
    return new SeasonResponse(
        season.getId(),
        season.getName(),
        season.getStartDate(),
        season.getEndDate(),
        season.isActive());
  }
}
