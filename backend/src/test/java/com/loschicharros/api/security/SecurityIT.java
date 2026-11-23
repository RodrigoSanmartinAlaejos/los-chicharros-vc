package com.loschicharros.api.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.loschicharros.api.support.AbstractIntegrationTest;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

/** Quién puede hacer qué sin sesión, con un token no válido y sin el rol (matriz de permisos). */
class SecurityIT extends AbstractIntegrationTest {

  private static final String VENUE =
      "{\"name\":\"Pabellón del Puerto\",\"address\":\"Avenida Marítima, 12\"}";

  @Autowired private JwtEncoder encoder;

  @ParameterizedTest
  @ValueSource(strings = {"/api/v1/seasons", "/api/v1/categories", "/api/v1/venues"})
  void shouldListConfigurationWithoutSession(String url) throws Exception {
    mockMvc.perform(get(url)).andExpect(status().isOk());
  }

  @Test
  void shouldReturnUnauthorizedWhenWritingWithoutToken() throws Exception {
    mockMvc
        .perform(post("/api/v1/venues").contentType(MediaType.APPLICATION_JSON).content(VENUE))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"))
        .andExpect(jsonPath("$.detail").value("Tienes que iniciar sesión"))
        .andExpect(jsonPath("$.instance").value("/api/v1/venues"));
  }

  @Test
  void shouldReturnUnauthorizedWhenTokenIsNotValid() throws Exception {
    mockMvc
        .perform(delete("/api/v1/venues/1").header(HttpHeaders.AUTHORIZATION, "Bearer no.es.jwt"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.detail").value("Tienes que iniciar sesión"));
  }

  @Test
  void shouldReturnUnauthorizedWhenTokenHasExpired() throws Exception {
    var past = Instant.now().minusSeconds(3600);
    var token = token(past.minusSeconds(900), past, "los-chicharros-api", List.of("ADMIN"));

    mockMvc
        .perform(delete("/api/v1/venues/1").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldReturnUnauthorizedWhenTokenComesFromAnotherIssuer() throws Exception {
    var now = Instant.now();
    var token = token(now, now.plusSeconds(900), "otra-api", List.of("ADMIN"));

    mockMvc
        .perform(delete("/api/v1/venues/1").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldReturnForbiddenWhenUserIsNotAdmin() throws Exception {
    var userId = authTokens.createUser("socio@loschicharros.test", "una-contrasena-larga", true);

    mockMvc
        .perform(
            post("/api/v1/venues")
                .header(HttpHeaders.AUTHORIZATION, authTokens.bearer(userId, List.of()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(VENUE))
        .andExpect(status().isForbidden())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.detail").value("No tienes permiso para hacer esto"));
  }

  @Test
  void shouldRequireSessionForUnknownApiRoutes() throws Exception {
    // Sin sesión no se distingue una ruta que no existe de una protegida
    mockMvc.perform(get("/api/v1/no-existe")).andExpect(status().isUnauthorized());
  }

  @Test
  void shouldLeaveHealthCheckOpen() throws Exception {
    mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
  }

  private String token(Instant issuedAt, Instant expiresAt, String issuer, List<String> roles) {
    var claims =
        JwtClaimsSet.builder()
            .issuer(issuer)
            .subject("1")
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .claim("roles", roles)
            .build();
    return encoder
        .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
        .getTokenValue();
  }
}
