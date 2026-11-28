package com.loschicharros.api.standings.dto;

import com.loschicharros.api.standings.Standing;
import com.loschicharros.api.teams.dto.TeamSummary;

/** Fila de la clasificación con su puesto (contrato OpenAPI, {@code StandingResponse}). */
public record StandingResponse(
    int position,
    TeamSummary team,
    int points,
    int played,
    int won,
    int lost,
    int setsWon,
    int setsLost,
    int penaltyPoints) {

  public static StandingResponse from(int position, Standing standing) {
    return new StandingResponse(
        position,
        TeamSummary.from(standing.getTeam()),
        standing.getPoints(),
        standing.getPlayed(),
        standing.getWon(),
        standing.getLost(),
        standing.getSetsWon(),
        standing.getSetsLost(),
        standing.getPenaltyPoints());
  }
}
