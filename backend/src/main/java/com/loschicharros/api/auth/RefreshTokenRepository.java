package com.loschicharros.api.auth;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  /**
   * Busca el token bloqueando su fila: si llegan dos renovaciones con el mismo token, la segunda
   * espera y ve que ya está revocado.
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<RefreshToken> findByTokenHash(String tokenHash);

  /** Revoca todas las sesiones abiertas de un usuario. */
  @Modifying
  @Query(
      "UPDATE RefreshToken t SET t.revokedAt = :now"
          + " WHERE t.userId = :userId AND t.revokedAt IS NULL")
  int revokeAllOfUser(Long userId, Instant now);
}
