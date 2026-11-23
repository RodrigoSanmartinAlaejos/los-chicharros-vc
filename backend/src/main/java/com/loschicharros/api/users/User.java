package com.loschicharros.api.users;

import com.loschicharros.api.common.audit.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Locale;

/**
 * Cuenta con la que se inicia sesión. En la fase 1 solo existe el administrador; en la fase 2 se
 * enlaza con una persona y tiene roles.
 */
@Entity
@Table(name = "users")
public class User extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 254)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 100)
  private String passwordHash;

  @Column(nullable = false)
  private boolean active = true;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  protected User() {}

  /** Cuenta nueva y activa; el email se guarda en minúsculas. */
  public User(String email, String passwordHash) {
    this.email = normalizeEmail(email);
    this.passwordHash = passwordHash;
  }

  /** Forma en que se guardan y se buscan los emails: sin espacios alrededor y en minúsculas. */
  public static String normalizeEmail(String email) {
    return email.strip().toLowerCase(Locale.ROOT);
  }

  /** Si puede iniciar sesión: activa y no borrada. */
  public boolean canLogIn() {
    return active && deletedAt == null;
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }
}
