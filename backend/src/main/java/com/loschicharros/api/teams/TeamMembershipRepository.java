package com.loschicharros.api.teams;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface TeamMembershipRepository extends JpaRepository<TeamMembership, Long> {

  /** Plantilla de un equipo con sus personas: jugadores por dorsal y después por nombre. */
  @Query(
      "SELECT m FROM TeamMembership m JOIN FETCH m.person p WHERE m.team.id = :teamId"
          + " ORDER BY m.shirtNumber NULLS LAST, p.firstName, p.lastName")
  List<TeamMembership> findRoster(@Param("teamId") Long teamId);

  Optional<TeamMembership> findByIdAndTeamId(Long id, Long teamId);

  boolean existsByTeamIdAndPersonIdAndType(Long teamId, Long personId, MembershipType type);

  Optional<TeamMembership> findByTeamIdAndShirtNumber(Long teamId, Integer shirtNumber);

  @Modifying
  @Query("DELETE FROM TeamMembership m WHERE m.team.id = :teamId")
  void deleteByTeamId(@Param("teamId") Long teamId);

  /** Quita a una persona de las plantillas de las temporadas que aún no han terminado. */
  @Modifying
  @Query(
      "DELETE FROM TeamMembership m WHERE m.person.id = :personId AND m.team.id IN"
          + " (SELECT t.id FROM Team t WHERE t.season.endDate >= :today)")
  int deleteCurrentOfPerson(@Param("personId") Long personId, @Param("today") LocalDate today);
}
