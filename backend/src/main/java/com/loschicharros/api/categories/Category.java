package com.loschicharros.api.categories;

import com.loschicharros.api.common.audit.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Categoría por edad y sexo, por ejemplo «Cadete femenina». */
@Entity
@Table(name = "categories")
public class Category extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 30)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private Gender gender;

  @Column(name = "sort_order", nullable = false)
  private short sortOrder;

  protected Category() {}

  public Category(String name, Gender gender, int sortOrder) {
    update(name, gender, sortOrder);
  }

  /** Cambia todos los datos editables de la categoría. */
  public void update(String name, Gender gender, int sortOrder) {
    this.name = name;
    this.gender = gender;
    this.sortOrder = (short) sortOrder;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Gender getGender() {
    return gender;
  }

  public int getSortOrder() {
    return sortOrder;
  }
}
