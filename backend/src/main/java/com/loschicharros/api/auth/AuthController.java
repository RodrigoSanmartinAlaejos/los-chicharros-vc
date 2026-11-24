package com.loschicharros.api.auth;

import com.loschicharros.api.auth.dto.AccessTokenResponse;
import com.loschicharros.api.auth.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Autenticación (contrato OpenAPI, etiqueta «Autenticación»). El access token va en el cuerpo; el
 * refresh token, solo en una cookie {@code HttpOnly}, {@code Secure}, {@code SameSite=Strict} y
 * limitada a {@code /api/v1/auth} (reglas técnicas, regla 2 de autenticación).
 */
@RestController
@RequestMapping(AuthController.PATH)
@EnableConfigurationProperties(SessionProperties.class)
class AuthController {

  static final String PATH = "/api/v1/auth";
  static final String COOKIE = "refresh_token";

  private final AuthService service;
  private final SessionProperties session;

  AuthController(AuthService service, SessionProperties session) {
    this.service = service;
    this.session = session;
  }

  @PostMapping("/login")
  ResponseEntity<AccessTokenResponse> login(@Valid @RequestBody LoginRequest request) {
    return withCookie(service.login(request));
  }

  @PostMapping("/refresh")
  ResponseEntity<AccessTokenResponse> refresh(
      @CookieValue(name = COOKIE, required = false) String refreshToken) {
    return withCookie(service.refresh(refreshToken));
  }

  @PostMapping("/logout")
  ResponseEntity<Void> logout(@CookieValue(name = COOKIE, required = false) String refreshToken) {
    service.logout(refreshToken);
    return ResponseEntity.noContent()
        .header(HttpHeaders.SET_COOKIE, clearedCookie().toString())
        .build();
  }

  /** La sesión no vale: 401 y la cookie se borra, para que el navegador no la vuelva a mandar. */
  @ExceptionHandler(InvalidSessionException.class)
  ResponseEntity<ProblemDetail> handleInvalidSession(
      InvalidSessionException ex, HttpServletRequest request) {
    var problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    problem.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .header(HttpHeaders.SET_COOKIE, clearedCookie().toString())
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problem);
  }

  private ResponseEntity<AccessTokenResponse> withCookie(IssuedSession issued) {
    // Redondeado hacia arriba al segundo: el token caduca justo cuando la cookie
    var millis = Duration.between(Instant.now(), issued.refreshExpiresAt()).toMillis();
    var maxAge = Duration.ofSeconds(Math.max(0, (millis + 999) / 1000));
    var cookie = cookie(issued.refreshToken(), maxAge);
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(issued.accessToken());
  }

  private ResponseCookie clearedCookie() {
    return cookie("", Duration.ZERO);
  }

  private ResponseCookie cookie(String value, Duration maxAge) {
    return ResponseCookie.from(COOKIE, value)
        .httpOnly(true)
        .secure(session.cookieSecure())
        .sameSite("Strict")
        .path(PATH)
        .maxAge(maxAge)
        .build();
  }
}
