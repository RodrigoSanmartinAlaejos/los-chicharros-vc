package com.loschicharros.api.teams.dto;

import com.loschicharros.api.categories.dto.CategoryResponse;
import com.loschicharros.api.teams.MembershipType;
import com.loschicharros.api.teams.Team;
import com.loschicharros.api.teams.TeamMembership;
import java.util.List;

/**
 * Equipo con su plantilla (contrato OpenAPI, {@code TeamResponse}): jugadores por dorsal y
 * entrenadores, con los datos públicos de cada persona.
 */
public record TeamResponse(
    Long id,
    String name,
    boolean ownClub,
    String crestUrl,
    CategoryResponse category,
    Long seasonId,
    List<MembershipResponse> players,
    List<MembershipResponse> coaches) {

  public static TeamResponse from(Team team, List<TeamMembership> roster) {
    return new TeamResponse(
        team.getId(),
        team.getName(),
        team.isOwnClub(),
        team.getCrestUrl(),
        CategoryResponse.from(team.getCategory()),
        team.getSeason().getId(),
        of(roster, MembershipType.PLAYER),
        of(roster, MembershipType.COACH));
  }

  private static List<MembershipResponse> of(List<TeamMembership> roster, MembershipType type) {
    return roster.stream()
        .filter(member -> member.getType() == type)
        .map(MembershipResponse::from)
        .toList();
  }
}
