package com.loschicharros.api.users;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a las cuentas. */
public interface UserRepository extends JpaRepository<User, Long> {

  /** Busca una cuenta por email ya normalizado (ver {@link User#normalizeEmail}). */
  Optional<User> findByEmail(String email);

  /** En la fase 1 toda cuenta activa y sin borrar es de administrador. */
  boolean existsByActiveTrueAndDeletedAtIsNull();
}
