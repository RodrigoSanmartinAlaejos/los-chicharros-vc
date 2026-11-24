package com.loschicharros.api.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * Refresh token de una sesión. Solo se guarda el hash SHA-256: el token en claro solo existe en la
 * cookie del navegador. Sin columnas de auditoría: tiene sus propias fechas (reglas técnicas, regla
 * 12 de Java).
 */
@Entity
@Table(name = "refresh_tokens")
class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "token_hash", nullable = false, length = 64)
  private String tokenHash;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "session_expires_at", nullable = false)
  private Instant sessionExpiresAt;

  @Column(name = "revoked_at")
  private Instant revokedAt;

  protected RefreshToken() {}

  RefreshToken(
      Long userId, String tokenHash, Instant createdAt, Instant expiresAt, Instant sessionEnd) {
    this.userId = userId;
    this.tokenHash = tokenHash;
    this.createdAt = createdAt;
    this.expiresAt = expiresAt;
    this.sessionExpiresAt = sessionEnd;
  }

  boolean isRevoked() {
    return revokedAt != null;
  }

  /** Caducado por inactividad o por haber llegado al final de la sesión. */
  boolean isExpired(Instant now) {
    return !now.isBefore(expiresAt) || !now.isBefore(sessionExpiresAt);
  }

  void revoke(Instant now) {
    if (revokedAt == null) {
      revokedAt = now;
    }
  }

  Long getUserId() {
    return userId;
  }

  Instant getExpiresAt() {
    return expiresAt;
  }

  Instant getSessionExpiresAt() {
    return sessionExpiresAt;
  }
}
