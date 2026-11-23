package com.loschicharros.api.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.loschicharros.api.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/** Inicio de sesión (issue #53, H09). */
class AuthControllerIT extends AbstractIntegrationTest {

  private static final String LOGIN = "/api/v1/auth/login";
  private static final String GENERIC = "Email o contraseña incorrectos";

  @Autowired private JdbcTemplate jdbc;

  @Test
  void shouldReturnAccessTokenWhenCredentialsAreRight() throws Exception {
    // Dado
    var id = authTokens.createUser("admin@loschicharros.test", "una-contrasena-larga", true);

    // Cuando
    var response =
        mockMvc
            .perform(login("admin@loschicharros.test", "una-contrasena-larga"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.expiresIn").value(900))
            .andExpect(jsonPath("$.accessToken").isString())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Entonces: el token abre las rutas del administrador y queda como autor de los cambios
    String token = JsonPath.read(response, "$.accessToken");
    mockMvc
        .perform(
            post("/api/v1/venues")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Pabellón del Puerto\",\"address\":\"Avenida Marítima, 12\"}"))
        .andExpect(status().isCreated());
    assertThat(jdbc.queryForObject("SELECT created_by FROM venues", Long.class)).isEqualTo(id);
  }

  @Test
  void shouldIgnoreCaseInEmail() throws Exception {
    authTokens.createUser("admin@loschicharros.test", "una-contrasena-larga", true);

    mockMvc
        .perform(login("Admin@LosChicharros.TEST", "una-contrasena-larga"))
        .andExpect(status().isOk());
  }

  @Test
  void shouldReturnSameGenericErrorForWrongPasswordAndUnknownEmail() throws Exception {
    // Dado
    authTokens.createUser("admin@loschicharros.test", "una-contrasena-larga", true);

    // Cuando / Entonces: mismo estado y mismo mensaje en los dos casos
    for (var request :
        new Object[][] {
          {"admin@loschicharros.test", "otra-contrasena"}, {"nadie@loschicharros.test", "x"}
        }) {
      mockMvc
          .perform(login((String) request[0], (String) request[1]))
          .andExpect(status().isUnauthorized())
          .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
          .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"))
          .andExpect(jsonPath("$.detail").value(GENERIC))
          .andExpect(jsonPath("$.accessToken").doesNotExist());
    }
  }

  @Test
  void shouldRejectInactiveAccountWithGenericError() throws Exception {
    authTokens.createUser("baja@loschicharros.test", "una-contrasena-larga", false);

    mockMvc
        .perform(login("baja@loschicharros.test", "una-contrasena-larga"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.detail").value(GENERIC));
  }

  @Test
  void shouldValidateBody() throws Exception {
    mockMvc
        .perform(login("no-es-un-email", ""))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors", hasSize(2)))
        .andExpect(jsonPath("$.errors[0].field").value("email"))
        .andExpect(jsonPath("$.errors[1].field").value("password"));
  }

  @Test
  void shouldNotNeedTokenToLogIn() throws Exception {
    // Un token caducado o roto que siga en memoria no impide volver a entrar
    authTokens.createUser("admin@loschicharros.test", "una-contrasena-larga", true);

    mockMvc
        .perform(
            login("admin@loschicharros.test", "una-contrasena-larga")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token-roto"))
        .andExpect(status().isOk());
  }

  private static MockHttpServletRequestBuilder login(String email, String password) {
    return post(LOGIN)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"email\":\"%s\",\"password\":\"%s\"}".formatted(email, password));
  }
}
