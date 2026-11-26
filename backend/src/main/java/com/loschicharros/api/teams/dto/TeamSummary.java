package com.loschicharros.api.teams.dto;

import com.loschicharros.api.categories.dto.CategoryResponse;
import com.loschicharros.api.teams.Team;

/** Equipo sin plantilla, para los listados (contrato OpenAPI, {@code TeamSummary}). */
public record TeamSummary(
    Long id,
    String name,
    boolean ownClub,
    String crestUrl,
    CategoryResponse category,
    Long seasonId) {

  public static TeamSummary from(Team team) {
    return new TeamSummary(
        team.getId(),
        team.getName(),
        team.isOwnClub(),
        team.getCrestUrl(),
        CategoryResponse.from(team.getCategory()),
        team.getSeason().getId());
  }
}
