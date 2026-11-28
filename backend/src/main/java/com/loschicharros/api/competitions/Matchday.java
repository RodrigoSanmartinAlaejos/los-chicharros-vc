package com.loschicharros.api.competitions;

import com.loschicharros.api.common.audit.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Jornada de una competición: un número (único en la competición) y una fecha prevista. */
@Entity
@Table(name = "matchdays")
public class Matchday extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "competition_id")
  private Competition competition;

  /** De 1 a 99: la columna es SMALLINT UNSIGNED. */
  @JdbcTypeCode(SqlTypes.SMALLINT)
  @Column(nullable = false)
  private int number;

  @Column(name = "scheduled_date")
  private LocalDate scheduledDate;

  protected Matchday() {}

  public Matchday(Competition competition, int number, LocalDate scheduledDate) {
    update(competition, number, scheduledDate);
  }

  /** Cambia los datos editables desde el panel. */
  public void update(Competition competition, int number, LocalDate scheduledDate) {
    this.competition = competition;
    this.number = number;
    this.scheduledDate = scheduledDate;
  }

  public Long getId() {
    return id;
  }

  public Competition getCompetition() {
    return competition;
  }

  public int getNumber() {
    return number;
  }

  public LocalDate getScheduledDate() {
    return scheduledDate;
  }
}
