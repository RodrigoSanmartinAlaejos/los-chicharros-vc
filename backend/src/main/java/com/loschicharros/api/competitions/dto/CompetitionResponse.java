package com.loschicharros.api.competitions.dto;

import com.loschicharros.api.competitions.Competition;

/** Competición tal como la devuelve la API (contrato OpenAPI, {@code CompetitionResponse}). */
public record CompetitionResponse(
    Long id,
    Long seasonId,
    Long categoryId,
    String name,
    String federation,
    int forfeitPenaltyPoints) {

  public static CompetitionResponse from(Competition competition) {
    return new CompetitionResponse(
        competition.getId(),
        competition.getSeason().getId(),
        competition.getCategory().getId(),
        competition.getName(),
        competition.getFederation(),
        competition.getForfeitPenaltyPoints());
  }
}
