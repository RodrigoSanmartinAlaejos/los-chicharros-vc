package com.loschicharros.api.support.probe;

import com.loschicharros.api.common.audit.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Entidad mínima de los tests para probar la auditoría; su tabla solo existe en el perfil test. */
@Entity
@Table(name = "audit_probes")
public class AuditProbe extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String code;

  protected AuditProbe() {}

  public AuditProbe(String name, String code) {
    this.name = name;
    this.code = code;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void rename(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }
}
