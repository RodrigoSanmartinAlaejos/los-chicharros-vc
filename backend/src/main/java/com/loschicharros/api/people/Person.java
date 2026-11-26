package com.loschicharros.api.people;

import com.loschicharros.api.common.audit.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import org.hibernate.annotations.SQLRestriction;

/**
 * Jugador o entrenador del club. El borrado es lógico: una persona borrada tiene {@code deletedAt}
 * y ninguna consulta de la aplicación la ve, pero su fila sigue ahí para las plantillas y los
 * resultados de temporadas pasadas.
 */
@Entity
@Table(name = "people")
@SQLRestriction("deleted_at IS NULL")
public class Person extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "first_name", nullable = false, length = 60)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;

  @Column(name = "birth_date")
  private LocalDate birthDate;

  @Column(name = "photo_url", length = 500)
  private String photoUrl;

  @Column(name = "image_consent", nullable = false)
  private boolean imageConsent;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  protected Person() {}

  public Person(String firstName, String lastName, LocalDate birthDate, boolean imageConsent) {
    update(firstName, lastName, birthDate, imageConsent);
  }

  /** Cambia los datos editables desde el panel (la foto se sube aparte, en H14). */
  public void update(String firstName, String lastName, LocalDate birthDate, boolean imageConsent) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.birthDate = birthDate;
    this.imageConsent = imageConsent;
  }

  /** Borrado lógico: desde ahora la persona no aparece en ninguna consulta. */
  public void markDeleted(Instant when) {
    this.deletedAt = when;
  }

  public Long getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public String getPhotoUrl() {
    return photoUrl;
  }

  public boolean hasImageConsent() {
    return imageConsent;
  }
}
