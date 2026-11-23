package com.loschicharros.api.seasons;

import com.loschicharros.api.common.audit.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

/** Temporada deportiva, por ejemplo «2026-27». Solo una está activa a la vez. */
@Entity
@Table(name = "seasons")
public class Season extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 9)
  private String name;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @Column(nullable = false)
  private boolean active;

  protected Season() {}

  public Season(String name, LocalDate startDate, LocalDate endDate, boolean active) {
    update(name, startDate, endDate, active);
  }

  /** Cambia todos los datos editables de la temporada. */
  public void update(String name, LocalDate startDate, LocalDate endDate, boolean active) {
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
    this.active = active;
  }

  public void deactivate() {
    this.active = false;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public boolean isActive() {
    return active;
  }
}
