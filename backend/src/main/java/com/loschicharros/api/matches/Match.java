package com.loschicharros.api.matches;

import com.loschicharros.api.common.audit.Auditable;
import com.loschicharros.api.competitions.Matchday;
import com.loschicharros.api.teams.Team;
import com.loschicharros.api.venues.Venue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Partido de una jornada entre dos equipos de la categoría y la temporada de la competición. El
 * pabellón solo se indica si es uno de los del club.
 */
@Entity
@Table(name = "matches")
public class Match extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "matchday_id")
  private Matchday matchday;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "home_team_id")
  private Team homeTeam;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "away_team_id")
  private Team awayTeam;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "venue_id")
  private Venue venue;

  @Column(name = "starts_at", nullable = false)
  private Instant startsAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private MatchStatus status = MatchStatus.SCHEDULED;

  /** Sets de cada equipo, de 0 a 3: las columnas son TINYINT UNSIGNED. */
  @JdbcTypeCode(SqlTypes.TINYINT)
  @Column(name = "home_sets")
  private Integer homeSets;

  @JdbcTypeCode(SqlTypes.TINYINT)
  @Column(name = "away_sets")
  private Integer awaySets;

  protected Match() {}

  public Match(Matchday matchday, Team homeTeam, Team awayTeam, Venue venue, Instant startsAt) {
    update(matchday, homeTeam, awayTeam, venue, startsAt);
  }

  /** Cambia la jornada, los equipos, el pabellón y la hora; el estado y el marcador se quedan. */
  public void update(
      Matchday matchday, Team homeTeam, Team awayTeam, Venue venue, Instant startsAt) {
    this.matchday = matchday;
    this.homeTeam = homeTeam;
    this.awayTeam = awayTeam;
    this.venue = venue;
    this.startsAt = startsAt;
  }

  /** Cambia el estado. Si deja de estar finalizado, el marcador se borra. */
  public void changeStatus(MatchStatus status) {
    this.status = status;
    if (status != MatchStatus.FINISHED) {
      this.homeSets = null;
      this.awaySets = null;
    }
  }

  public void reschedule(Instant startsAt) {
    this.startsAt = startsAt;
  }

  public Long getId() {
    return id;
  }

  public Matchday getMatchday() {
    return matchday;
  }

  public Team getHomeTeam() {
    return homeTeam;
  }

  public Team getAwayTeam() {
    return awayTeam;
  }

  public Venue getVenue() {
    return venue;
  }

  public Instant getStartsAt() {
    return startsAt;
  }

  public MatchStatus getStatus() {
    return status;
  }

  public Integer getHomeSets() {
    return homeSets;
  }

  public Integer getAwaySets() {
    return awaySets;
  }
}
