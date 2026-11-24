package com.loschicharros.api.support;

import com.loschicharros.api.security.Roles;
import com.loschicharros.api.security.TokenService;
import java.util.List;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Usuarios y access tokens para los tests de endpoints. Pide el token al servicio que firma los de
 * verdad, no al endpoint de login, que tendrá límite de peticiones (guía de tests). Los usuarios se
 * crean en cada test: {@code DatabaseCleaner} vacía {@code users} antes de cada uno.
 */
@TestComponent
public class AuthTokens {

  public static final String ADMIN_EMAIL = "admin@loschicharros.test";
  public static final String PASSWORD = "contrasena-de-los-tests";

  private final JdbcTemplate jdbc;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokens;

  public AuthTokens(JdbcTemplate jdbc, PasswordEncoder passwordEncoder, TokenService tokens) {
    this.jdbc = jdbc;
    this.passwordEncoder = passwordEncoder;
    this.tokens = tokens;
  }

  /** Cabecera {@code Authorization} de un administrador, que se crea si aún no existe. */
  public String admin() {
    return bearer(adminId(), List.of(Roles.ADMIN));
  }

  /** Id del administrador de los tests ({@link #ADMIN_EMAIL}, contraseña {@link #PASSWORD}). */
  public long adminId() {
    var ids = jdbc.queryForList("SELECT id FROM users WHERE email = ?", Long.class, ADMIN_EMAIL);
    return ids.isEmpty() ? createUser(ADMIN_EMAIL, PASSWORD, true) : ids.getFirst();
  }

  /** Crea una cuenta con la contraseña cifrada como la guarda la API y devuelve su id. */
  public long createUser(String email, String password, boolean active) {
    jdbc.update(
        "INSERT INTO users (email, password_hash, active, created_at, updated_at)"
            + " VALUES (?, ?, ?, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6))",
        email,
        passwordEncoder.encode(password),
        active);
    return jdbc.queryForObject("SELECT id FROM users WHERE email = ?", Long.class, email);
  }

  /** Cabecera {@code Authorization} con un token para ese usuario y esos roles. */
  public String bearer(long userId, List<String> roles) {
    var email = jdbc.queryForObject("SELECT email FROM users WHERE id = ?", String.class, userId);
    return "Bearer " + tokens.issueAccessToken(userId, email, roles);
  }
}
