package com.loschicharros.api.standings;

import com.loschicharros.api.common.audit.Auditable;
import com.loschicharros.api.competitions.Competition;
import com.loschicharros.api.teams.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Fila de la clasificación de una competición: un equipo con sus puntos, partidos y sets. Las
 * columnas son SMALLINT (los puntos pueden ser negativos por sanciones).
 */
@Entity
@Table(name = "standings")
public class Standing extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "competition_id")
  private Competition competition;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "team_id")
  private Team team;

  @JdbcTypeCode(SqlTypes.SMALLINT)
  @Column(nullable = false)
  private int points;

  @JdbcTypeCode(SqlTypes.SMALLINT)
  @Column(nullable = false)
  private int played;

  @JdbcTypeCode(SqlTypes.SMALLINT)
  @Column(nullable = false)
  private int won;

  @JdbcTypeCode(SqlTypes.SMALLINT)
  @Column(nullable = false)
  private int lost;

  @JdbcTypeCode(SqlTypes.SMALLINT)
  @Column(name = "sets_won", nullable = false)
  private int setsWon;

  @JdbcTypeCode(SqlTypes.SMALLINT)
  @Column(name = "sets_lost", nullable = false)
  private int setsLost;

  @JdbcTypeCode(SqlTypes.SMALLINT)
  @Column(name = "penalty_points", nullable = false)
  private int penaltyPoints;

  protected Standing() {}

  public Standing(
      Competition competition,
      Team team,
      int points,
      int played,
      int won,
      int lost,
      int setsWon,
      int setsLost,
      int penaltyPoints) {
    this.competition = competition;
    this.team = team;
    this.points = points;
    this.played = played;
    this.won = won;
    this.lost = lost;
    this.setsWon = setsWon;
    this.setsLost = setsLost;
    this.penaltyPoints = penaltyPoints;
  }

  public Long getId() {
    return id;
  }

  public Competition getCompetition() {
    return competition;
  }

  public Team getTeam() {
    return team;
  }

  public int getPoints() {
    return points;
  }

  public int getPlayed() {
    return played;
  }

  public int getWon() {
    return won;
  }

  public int getLost() {
    return lost;
  }

  public int getSetsWon() {
    return setsWon;
  }

  public int getSetsLost() {
    return setsLost;
  }

  public int getPenaltyPoints() {
    return penaltyPoints;
  }
}
