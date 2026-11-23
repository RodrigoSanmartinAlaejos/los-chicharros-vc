package com.loschicharros.api.auth;

import com.loschicharros.api.users.User;
import com.loschicharros.api.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Crea el administrador inicial al arrancar si no hay ninguno (reglas técnicas, regla 8 de bases de
 * datos): no va en una migración porque la contraseña no puede estar en el repositorio. Si ya
 * existe, no lo toca: cambiar las variables después no cambia la contraseña.
 */
@Component
@EnableConfigurationProperties(AdminProperties.class)
public class AdminBootstrap implements ApplicationRunner {

  static final int MIN_PASSWORD_LENGTH = 12;
  private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

  private final UserRepository users;
  private final PasswordEncoder passwordEncoder;
  private final AdminProperties admin;

  public AdminBootstrap(
      UserRepository users, PasswordEncoder passwordEncoder, AdminProperties admin) {
    this.users = users;
    this.passwordEncoder = passwordEncoder;
    this.admin = admin;
  }

  @Override
  public void run(ApplicationArguments args) {
    createAdminIfMissing();
  }

  /**
   * Crea el administrador si no hay ninguno; falla con un mensaje claro si faltan las variables.
   */
  @Transactional
  public void createAdminIfMissing() {
    if (users.existsByActiveTrueAndDeletedAtIsNull()) {
      return;
    }
    if (admin.email() == null || admin.email().isBlank()) {
      throw new IllegalStateException(
          "No hay ningún administrador: define ADMIN_EMAIL y ADMIN_PASSWORD para crearlo");
    }
    if (admin.password() == null || admin.password().isBlank()) {
      throw new IllegalStateException(
          "No hay ningún administrador: define ADMIN_PASSWORD para crearlo");
    }
    if (admin.password().length() < MIN_PASSWORD_LENGTH) {
      throw new IllegalStateException(
          "ADMIN_PASSWORD tiene "
              + admin.password().length()
              + " caracteres y tiene que tener al menos "
              + MIN_PASSWORD_LENGTH);
    }
    var user = users.save(new User(admin.email(), passwordEncoder.encode(admin.password())));
    log.info("Administrador inicial creado con id {}", user.getId());
  }
}
