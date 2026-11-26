package com.loschicharros.api.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.loschicharros.api.support.AbstractIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/** Bloqueo de intentos repetidos de login y límite de peticiones (issue #55, H11). */
class LoginProtectionIT extends AbstractIntegrationTest {

  private static final String EMAIL = "admin@loschicharros.test";
  private static final String PASSWORD = "una-contrasena-larga";
  private static final String ATTACKER = "203.0.113.7";
  private static final String OFFICE = "198.51.100.20";

  @BeforeEach
  void createAdmin() {
    authTokens.createUser(EMAIL, PASSWORD, true);
  }

  @Test
  void shouldBlockEmailFromIpAfterFiveFailures() throws Exception {
    // Dado: 5 fallos seguidos con mi email desde una IP
    for (var i = 0; i < 5; i++) {
      mockMvc
          .perform(login(EMAIL, "contrasena-mala", ATTACKER))
          .andExpect(status().isUnauthorized());
    }

    // Cuando lo intenta una sexta vez, aunque sea con la contraseña buena
    mockMvc
        .perform(login(EMAIL, PASSWORD, ATTACKER))
        // Entonces recibe el mismo 401 genérico
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.detail").value("Email o contraseña incorrectos"))
        .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));
  }

  @Test
  void shouldLetMeInFromAnotherIpWhileBlocked() throws Exception {
    for (var i = 0; i < 5; i++) {
      mockMvc.perform(login(EMAIL, "contrasena-mala", ATTACKER));
    }

    mockMvc
        .perform(login(EMAIL, PASSWORD, OFFICE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").isString());
  }

  @Test
  void shouldNotBlockWhenFailuresAreNotConsecutive() throws Exception {
    // Mayúsculas y espacios no cambian el email: cuentan para la misma combinación
    for (var i = 0; i < 4; i++) {
      mockMvc.perform(login(" Admin@LosChicharros.test", "contrasena-mala", ATTACKER));
    }
    mockMvc.perform(login(EMAIL, PASSWORD, ATTACKER)).andExpect(status().isOk());
    for (var i = 0; i < 4; i++) {
      mockMvc.perform(login(EMAIL, "contrasena-mala", ATTACKER));
    }

    mockMvc.perform(login(EMAIL, PASSWORD, ATTACKER)).andExpect(status().isOk());
  }

  @Test
  void shouldReturnTooManyRequestsAfterTwentyRequestsPerMinute() throws Exception {
    // Dado: 20 peticiones en un minuto a /api/v1/auth/** desde una IP
    for (var i = 0; i < 20; i++) {
      mockMvc
          .perform(from(post("/api/v1/auth/logout"), ATTACKER))
          .andExpect(status().isNoContent());
    }

    // Cuando hace la siguiente, recibe 429
    mockMvc
        .perform(login(EMAIL, PASSWORD, ATTACKER))
        .andExpect(status().isTooManyRequests())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(header().string(HttpHeaders.RETRY_AFTER, Matchers.matchesPattern("\\d+")))
        .andExpect(jsonPath("$.status").value(429))
        .andExpect(
            jsonPath("$.detail").value(Matchers.startsWith("Demasiadas peticiones. Espera ")))
        .andExpect(jsonPath("$.instance").value("/api/v1/auth/login"));
    // Las demás IP siguen entrando
    mockMvc.perform(login(EMAIL, PASSWORD, OFFICE)).andExpect(status().isOk());
  }

  @Test
  void shouldNotLimitOtherRoutes() throws Exception {
    for (var i = 0; i < 25; i++) {
      mockMvc.perform(from(get("/api/v1/seasons"), ATTACKER)).andExpect(status().isOk());
    }
  }

  private static MockHttpServletRequestBuilder login(String email, String password, String ip) {
    return from(post("/api/v1/auth/login"), ip)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"email\":\"%s\",\"password\":\"%s\"}".formatted(email, password));
  }

  /** La IP del cliente, como la deja Tomcat tras leer X-Forwarded-For del proxy del SSR. */
  private static MockHttpServletRequestBuilder from(
      MockHttpServletRequestBuilder request, String ip) {
    return request.with(servletRequest -> {
      servletRequest.setRemoteAddr(ip);
      return servletRequest;
    });
  }
}
