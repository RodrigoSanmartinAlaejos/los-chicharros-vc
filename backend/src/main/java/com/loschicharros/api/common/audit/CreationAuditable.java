package com.loschicharros.api.common.audit;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Base de las entidades cuyas filas se crean y se borran, pero nunca se modifican (por ejemplo,
 * {@code user_roles}): solo guarda quién y cuándo las creó.
 *
 * <p>Spring Data rellena las columnas al guardar; nunca se asignan a mano (reglas técnicas, regla
 * 11 de Java).
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class CreationAuditable {

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  /** Id del usuario que creó la fila, o {@code null} si la creó el sistema. */
  @CreatedBy
  @Column(name = "created_by", updatable = false)
  private Long createdBy;

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Long getCreatedBy() {
    return createdBy;
  }
}
