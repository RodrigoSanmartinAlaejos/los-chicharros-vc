package com.loschicharros.api.venues;

import com.loschicharros.api.common.audit.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/** Instalación donde el club juega o entrena. Las coordenadas son opcionales. */
@Entity
@Table(name = "venues")
public class Venue extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, length = 200)
  private String address;

  @Column(precision = 9, scale = 6)
  private BigDecimal latitude;

  @Column(precision = 9, scale = 6)
  private BigDecimal longitude;

  protected Venue() {}

  public Venue(String name, String address, BigDecimal latitude, BigDecimal longitude) {
    update(name, address, latitude, longitude);
  }

  /** Cambia todos los datos editables de la instalación. */
  public void update(String name, String address, BigDecimal latitude, BigDecimal longitude) {
    this.name = name;
    this.address = address;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getAddress() {
    return address;
  }

  public BigDecimal getLatitude() {
    return latitude;
  }

  public BigDecimal getLongitude() {
    return longitude;
  }
}
