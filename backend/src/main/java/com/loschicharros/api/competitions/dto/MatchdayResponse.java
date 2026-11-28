package com.loschicharros.api.competitions.dto;

import com.loschicharros.api.competitions.Matchday;
import java.time.LocalDate;

/**
 * Jornada tal como la devuelve la API (contrato OpenAPI, {@code MatchdayResponse}). Los partidos de
 * cada jornada se añaden con H19.
 */
public record MatchdayResponse(Long id, Long competitionId, int number, LocalDate scheduledDate) {

  public static MatchdayResponse from(Matchday matchday) {
    return new MatchdayResponse(
        matchday.getId(),
        matchday.getCompetition().getId(),
        matchday.getNumber(),
        matchday.getScheduledDate());
  }
}
