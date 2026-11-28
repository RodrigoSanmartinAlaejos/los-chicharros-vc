package com.loschicharros.api.matches.dto;

import com.loschicharros.api.competitions.Matchday;
import java.time.LocalDate;
import java.util.List;

/** Jornada con sus partidos (contrato OpenAPI, {@code MatchdayResponse} con {@code matches}). */
public record MatchdayWithMatches(
    Long id, Long competitionId, int number, LocalDate scheduledDate, List<MatchResponse> matches) {

  public static MatchdayWithMatches from(Matchday matchday, List<MatchResponse> matches) {
    return new MatchdayWithMatches(
        matchday.getId(),
        matchday.getCompetition().getId(),
        matchday.getNumber(),
        matchday.getScheduledDate(),
        matches);
  }
}
