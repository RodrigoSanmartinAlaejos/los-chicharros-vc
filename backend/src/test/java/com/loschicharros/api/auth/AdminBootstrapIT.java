package com.loschicharros.api.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.loschicharros.api.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Administrador inicial desde {@code ADMIN_EMAIL} y {@code ADMIN_PASSWORD} (issue #53). En los
 * tests valen lo que pone {@code application-test.yml}.
 */
class AdminBootstrapIT extends AbstractIntegrationTest {

  @Autowired private AdminBootstrap bootstrap;
  @Autowired private JdbcTemplate jdbc;

  @Test
  void shouldCreateAdminWhenThereIsNone() throws Exception {
    // Dado: la base no tiene usuarios (la limpia cada test)
    assertThat(userCount()).isZero();

    // Cuando
    bootstrap.createAdminIfMissing();

    // Entonces: existe, con la contraseña cifrada con BCrypt, y puede entrar
    var hash =
        jdbc.queryForObject(
            "SELECT password_hash FROM users WHERE email = 'admin@loschicharros.test'",
            String.class);
    assertThat(hash).startsWith("$2").doesNotContain("contrasena-de-los-tests");
    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"email\":\"admin@loschicharros.test\","
                        + "\"password\":\"contrasena-de-los-tests\"}"))
        .andExpect(status().isOk());
  }

  @Test
  void shouldNotCreateAnotherAdminWhenOneExists() {
    authTokens.createUser("otro@loschicharros.test", "una-contrasena-larga", true);

    bootstrap.createAdminIfMissing();

    assertThat(userCount()).isOne();
  }

  private Integer userCount() {
    return jdbc.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
  }
}
