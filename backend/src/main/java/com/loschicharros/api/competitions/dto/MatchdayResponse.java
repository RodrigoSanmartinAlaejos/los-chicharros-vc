package com.loschicharros.api.competitions.dto;

import com.loschicharros.api.competitions.Matchday;
import java.time.LocalDate;

/**
 * Jornada tal como la devuelven el alta y la edición (contrato OpenAPI, {@code MatchdayResponse}
 * sin {@code matches}). Con sus partidos, la lee {@code MatchdayWithMatches}.
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
