package com.loschicharros.api.competitions;

import com.loschicharros.api.categories.Category;
import com.loschicharros.api.common.audit.Auditable;
import com.loschicharros.api.seasons.Season;
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
 * Competición (liga) de una temporada y una categoría. Organiza las jornadas y los partidos, y
 * tiene su propia clasificación.
 */
@Entity
@Table(name = "competitions")
public class Competition extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "season_id")
  private Season season;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "category_id")
  private Category category;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(length = 100)
  private String federation;

  /** Puntos que se restan por incomparecencia, de 0 a 10: la columna es TINYINT UNSIGNED. */
  @JdbcTypeCode(SqlTypes.TINYINT)
  @Column(name = "forfeit_penalty_points", nullable = false)
  private int forfeitPenaltyPoints;

  protected Competition() {}

  public Competition(
      Season season, Category category, String name, String federation, int forfeitPenaltyPoints) {
    update(season, category, name, federation, forfeitPenaltyPoints);
  }

  /** Cambia los datos editables desde el panel. */
  public void update(
      Season season, Category category, String name, String federation, int forfeitPenaltyPoints) {
    this.season = season;
    this.category = category;
    this.name = name;
    this.federation = federation;
    this.forfeitPenaltyPoints = forfeitPenaltyPoints;
  }

  public Long getId() {
    return id;
  }

  public Season getSeason() {
    return season;
  }

  public Category getCategory() {
    return category;
  }

  public String getName() {
    return name;
  }

  public String getFederation() {
    return federation;
  }

  public int getForfeitPenaltyPoints() {
    return forfeitPenaltyPoints;
  }
}
