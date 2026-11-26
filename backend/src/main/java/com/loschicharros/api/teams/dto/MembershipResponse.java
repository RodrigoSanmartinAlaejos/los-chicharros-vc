package com.loschicharros.api.teams.dto;

import com.loschicharros.api.people.dto.PublicPerson;
import com.loschicharros.api.teams.MembershipType;
import com.loschicharros.api.teams.TeamMembership;

/** Una persona de la plantilla (contrato OpenAPI, {@code MembershipResponse}). */
public record MembershipResponse(
    Long id, PublicPerson person, MembershipType type, Integer shirtNumber, String position) {

  public static MembershipResponse from(TeamMembership membership) {
    return new MembershipResponse(
        membership.getId(),
        PublicPerson.from(membership.getPerson()),
        membership.getType(),
        membership.getShirtNumber(),
        membership.getPosition());
  }
}
