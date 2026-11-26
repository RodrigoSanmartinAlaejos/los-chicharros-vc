package com.loschicharros.api.teams;

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

/**
 * Equipo de una temporada y una categoría: del club ({@code ownClub}) o rival. Los rivales solo
 * aparecen en partidos y clasificaciones, nunca en la página de equipos del club.
 */
@Entity
@Table(name = "teams")
public class Team extends Auditable {

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

  @Column(name = "own_club", nullable = false)
  private boolean ownClub;

  @Column(name = "crest_url", length = 500)
  private String crestUrl;

  protected Team() {}

  public Team(Season season, Category category, String name, boolean ownClub) {
    update(season, category, name, ownClub);
  }

  /** Cambia los datos editables desde el panel (el escudo se sube aparte, en H14). */
  public void update(Season season, Category category, String name, boolean ownClub) {
    this.season = season;
    this.category = category;
    this.name = name;
    this.ownClub = ownClub;
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

  public boolean isOwnClub() {
    return ownClub;
  }

  public String getCrestUrl() {
    return crestUrl;
  }
}
