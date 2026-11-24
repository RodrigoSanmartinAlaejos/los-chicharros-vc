package com.loschicharros.api.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.loschicharros.api.support.AbstractIntegrationTest;
import jakarta.servlet.http.Cookie;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MvcResult;

/** Renovación y cierre de la sesión con la cookie del refresh token (issue #54, H10). */
class SessionIT extends AbstractIntegrationTest {

  private static final String REFRESH = "/api/v1/auth/refresh";
  private static final String LOGOUT = "/api/v1/auth/logout";
  private static final String EMAIL = "admin@loschicharros.test";
  private static final String PASSWORD = "una-contrasena-larga";

  @Autowired private JdbcTemplate jdbc;
  private long userId;

  @BeforeEach
  void createAdmin() {
    userId = authTokens.createUser(EMAIL, PASSWORD, true);
  }

  @Test
  void shouldSetSecureRefreshCookieOnLogin() throws Exception {
    // Cuando
    var result = login();

    // Entonces: cookie que JavaScript no puede leer, solo para /api/v1/auth y de 2 horas
    var setCookie = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
    assertThat(setCookie)
        .startsWith("refresh_token=")
        .contains("Path=/api/v1/auth", "Max-Age=7200", "HttpOnly", "Secure", "SameSite=Strict")
        .doesNotContain("Domain");
    assertThat(result.getResponse().getContentAsString()).doesNotContain(cookieValue(result));

    // y en la base solo está el hash, con la sesión de 7 días
    var row =
        jdbc.queryForMap(
            "SELECT token_hash, expires_at, session_expires_at FROM refresh_tokens WHERE user_id = ?",
            userId);
    assertThat(row.get("token_hash")).isEqualTo(RefreshTokenService.hash(cookieValue(result)));
    assertThat(((LocalDateTime) row.get("session_expires_at")))
        .isAfter(((LocalDateTime) row.get("expires_at")).plusDays(6));
  }

  @Test
  void shouldRotateTokenAndKeepSessionEndOnRefresh() throws Exception {
    // Dado
    var first = cookieValue(login());
    var sessionEnd = sessionEnd();

    // Cuando
    var result =
        mockMvc
            .perform(post(REFRESH).cookie(refreshCookie(first)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.expiresIn").value(900))
            .andReturn();

    // Entonces: token nuevo, el anterior revocado y la misma fecha de fin de sesión
    var second = cookieValue(result);
    assertThat(second).isNotEqualTo(first);
    assertThat(revokedCount()).isOne();
    assertThat(
            jdbc.queryForObject(
                "SELECT COUNT(*) FROM refresh_tokens WHERE session_expires_at = ?",
                Integer.class,
                Timestamp.from(sessionEnd)))
        .isEqualTo(2);
    // y el access token nuevo abre las rutas del administrador
    String token = JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
    mockMvc
        .perform(
            post("/api/v1/venues")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Pabellón del Puerto\",\"address\":\"Avenida Marítima, 12\"}"))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldRevokeEverySessionWhenARotatedTokenIsReused() throws Exception {
    // Dado: el token se renovó y alguien vuelve a usar el antiguo
    var stolen = cookieValue(login());
    var current =
        cookieValue(
            mockMvc
                .perform(post(REFRESH).cookie(refreshCookie(stolen)))
                .andExpect(status().isOk())
                .andReturn());

    // Cuando
    mockMvc
        .perform(post(REFRESH).cookie(refreshCookie(stolen)))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.detail").value(InvalidSessionException.MESSAGE));

    // Entonces: tampoco vale el bueno, el usuario tiene que volver a entrar
    assertThat(
            jdbc.queryForObject(
                "SELECT COUNT(*) FROM refresh_tokens WHERE revoked_at IS NULL", Integer.class))
        .isZero();
    mockMvc
        .perform(post(REFRESH).cookie(refreshCookie(current)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldRejectRefreshAfterTwoHoursWithoutActivity() throws Exception {
    var token = cookieValue(login());
    jdbc.update(
        "UPDATE refresh_tokens SET expires_at = ?", Timestamp.from(Instant.now().minusSeconds(1)));

    var result =
        mockMvc
            .perform(post(REFRESH).cookie(refreshCookie(token)))
            .andExpect(status().isUnauthorized())
            .andReturn();

    assertThat(result.getResponse().getHeader(HttpHeaders.SET_COOKIE))
        .startsWith("refresh_token=;")
        .contains("Max-Age=0");
  }

  @Test
  void shouldRejectRefreshAfterSevenDaysEvenWithActivity() throws Exception {
    var token = cookieValue(login());
    jdbc.update(
        "UPDATE refresh_tokens SET session_expires_at = ?",
        Timestamp.from(Instant.now().minusSeconds(1)));

    mockMvc
        .perform(post(REFRESH).cookie(refreshCookie(token)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldNotExtendTokenBeyondSessionEnd() throws Exception {
    // Dado: queda una hora de sesión
    var token = cookieValue(login());
    var end = Instant.now().plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MICROS);
    jdbc.update("UPDATE refresh_tokens SET session_expires_at = ?", Timestamp.from(end));

    // Cuando
    var result =
        mockMvc
            .perform(post(REFRESH).cookie(refreshCookie(token)))
            .andExpect(status().isOk())
            .andReturn();

    // Entonces: el token nuevo caduca con la sesión, no 2 horas después
    assertThat(
            jdbc.queryForObject(
                "SELECT expires_at FROM refresh_tokens WHERE revoked_at IS NULL",
                LocalDateTime.class))
        .isEqualTo(LocalDateTime.ofInstant(end, ZoneOffset.UTC));
    assertThat(result.getResponse().getHeader(HttpHeaders.SET_COOKIE))
        .doesNotContain("Max-Age=7200");
  }

  @Test
  void shouldRejectRefreshWithoutCookieOrWithUnknownToken() throws Exception {
    mockMvc.perform(post(REFRESH)).andExpect(status().isUnauthorized());
    mockMvc
        .perform(post(REFRESH).cookie(refreshCookie("no-existe")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldRejectRefreshWhenAccountWasDeactivated() throws Exception {
    var token = cookieValue(login());
    jdbc.update("UPDATE users SET active = FALSE WHERE id = ?", userId);

    mockMvc
        .perform(post(REFRESH).cookie(refreshCookie(token)))
        .andExpect(status().isUnauthorized());
    assertThat(
            jdbc.queryForObject(
                "SELECT COUNT(*) FROM refresh_tokens WHERE revoked_at IS NULL", Integer.class))
        .isZero();
  }

  @Test
  void shouldRevokeTokenAndClearCookieOnLogout() throws Exception {
    // Dado
    var token = cookieValue(login());

    // Cuando
    var result =
        mockMvc
            .perform(post(LOGOUT).cookie(refreshCookie(token)))
            .andExpect(status().isNoContent())
            .andReturn();

    // Entonces
    assertThat(result.getResponse().getHeader(HttpHeaders.SET_COOKIE))
        .startsWith("refresh_token=;")
        .contains("Max-Age=0", "Path=/api/v1/auth");
    assertThat(revokedCount()).isOne();
    mockMvc
        .perform(post(REFRESH).cookie(refreshCookie(token)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldLogOutEvenWithoutCookie() throws Exception {
    mockMvc.perform(post(LOGOUT)).andExpect(status().isNoContent());
  }

  private MvcResult login() throws Exception {
    return mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"%s\",\"password\":\"%s\"}".formatted(EMAIL, PASSWORD)))
        .andExpect(status().isOk())
        .andReturn();
  }

  private Instant sessionEnd() {
    return jdbc.queryForObject("SELECT session_expires_at FROM refresh_tokens", LocalDateTime.class)
        .toInstant(ZoneOffset.UTC);
  }

  private Integer revokedCount() {
    return jdbc.queryForObject(
        "SELECT COUNT(*) FROM refresh_tokens WHERE revoked_at IS NOT NULL", Integer.class);
  }

  private static Cookie refreshCookie(String value) {
    return new Cookie("refresh_token", value);
  }

  private static String cookieValue(MvcResult result) {
    var header = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
    return header.substring("refresh_token=".length(), header.indexOf(';'));
  }
}
