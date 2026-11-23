package com.loschicharros.api.common.audit;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Base de las entidades de negocio: añade a {@link CreationAuditable} quién y cuándo modificó la
 * fila por última vez. Corresponde a las columnas de auditoría del diccionario de datos.
 */
@MappedSuperclass
public abstract class Auditable extends CreationAuditable {

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  /** Id del usuario que hizo el último cambio, o {@code null} si lo hizo el sistema. */
  @LastModifiedBy
  @Column(name = "updated_by")
  private Long updatedBy;

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public Long getUpdatedBy() {
    return updatedBy;
  }
}
