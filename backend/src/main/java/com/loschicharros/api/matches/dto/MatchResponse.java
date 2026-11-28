package com.loschicharros.api.matches.dto;

import com.loschicharros.api.matches.Match;
import com.loschicharros.api.matches.MatchStatus;
import com.loschicharros.api.teams.dto.TeamSummary;
import com.loschicharros.api.venues.dto.VenueResponse;
import java.time.Instant;

/** Partido tal como lo devuelve la API (contrato OpenAPI, {@code MatchResponse}). */
public record MatchResponse(
    Long id,
    Long matchdayId,
    int matchdayNumber,
    Long competitionId,
    TeamSummary homeTeam,
    TeamSummary awayTeam,
    VenueResponse venue,
    Instant startsAt,
    MatchStatus status,
    Integer homeSets,
    Integer awaySets) {

  public static MatchResponse from(Match match) {
    var matchday = match.getMatchday();
    return new MatchResponse(
        match.getId(),
        matchday.getId(),
        matchday.getNumber(),
        matchday.getCompetition().getId(),
        TeamSummary.from(match.getHomeTeam()),
        TeamSummary.from(match.getAwayTeam()),
        match.getVenue() == null ? null : VenueResponse.from(match.getVenue()),
        match.getStartsAt(),
        match.getStatus(),
        match.getHomeSets(),
        match.getAwaySets());
  }
}
