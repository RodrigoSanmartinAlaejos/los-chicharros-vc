package com.loschicharros.api.matches;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface MatchRepository extends JpaRepository<Match, Long> {

  /**
   * Partidos con los filtros que lleguen (cualquiera puede ser {@code null}); {@code to} excluido.
   */
  @EntityGraph(attributePaths = {"matchday", "homeTeam.category", "awayTeam.category", "venue"})
  @Query(
      "SELECT m FROM Match m WHERE (:teamId IS NULL OR m.homeTeam.id = :teamId"
          + " OR m.awayTeam.id = :teamId)"
          + " AND (:venueId IS NULL OR m.venue.id = :venueId)"
          + " AND (:from IS NULL OR m.startsAt >= :from)"
          + " AND (:to IS NULL OR m.startsAt < :to)"
          + " AND (:status IS NULL OR m.status = :status)")
  Page<Match> search(
      @Param("teamId") Long teamId,
      @Param("venueId") Long venueId,
      @Param("from") Instant from,
      @Param("to") Instant to,
      @Param("status") MatchStatus status,
      Pageable pageable);

  /** Partidos de unas jornadas, por fecha y hora. */
  @EntityGraph(attributePaths = {"matchday", "homeTeam.category", "awayTeam.category", "venue"})
  @Query("SELECT m FROM Match m WHERE m.matchday.id IN :matchdayIds ORDER BY m.startsAt, m.id")
  List<Match> findByMatchdays(@Param("matchdayIds") Collection<Long> matchdayIds);

  /** Si alguno de los equipos ya juega en la jornada, sin contar el partido {@code excludedId}. */
  @Query(
      "SELECT m FROM Match m WHERE m.matchday.id = :matchdayId AND m.id <> :excludedId"
          + " AND (m.homeTeam.id IN :teamIds OR m.awayTeam.id IN :teamIds)")
  List<Match> findInMatchday(
      @Param("matchdayId") Long matchdayId,
      @Param("teamIds") Collection<Long> teamIds,
      @Param("excludedId") Long excludedId);
}
