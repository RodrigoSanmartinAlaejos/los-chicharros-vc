package com.loschicharros.api.teams;

import com.loschicharros.api.common.audit.Auditable;
import com.loschicharros.api.people.Person;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Pertenencia de una persona a la plantilla de un equipo, como jugador (con dorsal y posición
 * opcionales) o como entrenador (sin ninguno de los dos).
 */
@Entity
@Table(name = "team_memberships")
public class TeamMembership extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "team_id")
  private Team team;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "person_id")
  private Person person;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private MembershipType type;

  /** Dorsal de 0 a 99: la columna es TINYINT UNSIGNED (diccionario de datos). */
  @JdbcTypeCode(SqlTypes.TINYINT)
  @Column(name = "shirt_number")
  private Integer shirtNumber;

  @Column(length = 30)
  private String position;

  protected TeamMembership() {}

  public TeamMembership(
      Team team, Person person, MembershipType type, Integer shirtNumber, String position) {
    this.team = team;
    this.person = person;
    this.type = type;
    change(shirtNumber, position);
  }

  /** Cambia el dorsal y la posición; {@code null} los quita. */
  public void change(Integer shirtNumber, String position) {
    this.shirtNumber = shirtNumber;
    this.position = position;
  }

  public Long getId() {
    return id;
  }

  public Team getTeam() {
    return team;
  }

  public Person getPerson() {
    return person;
  }

  public MembershipType getType() {
    return type;
  }

  public Integer getShirtNumber() {
    return shirtNumber;
  }

  public String getPosition() {
    return position;
  }
}
