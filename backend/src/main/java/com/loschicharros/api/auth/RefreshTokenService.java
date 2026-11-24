package com.loschicharros.api.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Refresh tokens de las sesiones (reglas técnicas, regla 3 de autenticación): se guardan con hash,
 * rotan en cada uso, la sesión se alarga 2 horas con cada renovación y termina como mucho 7 días
 * después del login. Si llega un token ya usado, se revocan todas las sesiones del usuario.
 */
@Service
public class RefreshTokenService {

  private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);
  private static final int TOKEN_BYTES = 32;

  private final RefreshTokenRepository tokens;
  private final SessionProperties properties;
  private final SecureRandom random = new SecureRandom();

  public RefreshTokenService(RefreshTokenRepository tokens, SessionProperties properties) {
    this.tokens = tokens;
    this.properties = properties;
  }

  /**
   * Abre una sesión nueva para un usuario recién autenticado.
   *
   * @param userId usuario que ha iniciado sesión
   * @return el token en claro y cuándo caduca
   */
  @Transactional
  public NewRefreshToken startSession(long userId) {
    var now = now();
    return save(userId, now, now.plus(properties.maxDuration()));
  }

  /**
   * Cambia un refresh token válido por otro nuevo de la misma sesión.
   *
   * @param rawToken el token de la cookie
   * @return el usuario y el token nuevo
   * @throws InvalidSessionException si falta, no existe, ha caducado o ya se había usado; en este
   *     último caso también revoca todas las sesiones del usuario, por si se lo han robado
   */
  @Transactional(noRollbackFor = InvalidSessionException.class)
  public Rotation rotate(String rawToken) {
    var token = find(rawToken);
    var now = now();
    if (token.isRevoked()) {
      tokens.revokeAllOfUser(token.getUserId(), now);
      log.warn(
          "Refresh token reutilizado: revocadas las sesiones del usuario {}", token.getUserId());
      throw new InvalidSessionException();
    }
    if (token.isExpired(now)) {
      throw new InvalidSessionException();
    }
    token.revoke(now);
    var next = save(token.getUserId(), now, token.getSessionExpiresAt());
    return new Rotation(token.getUserId(), next);
  }

  /**
   * Cierra la sesión de ese token. No falla si falta o no existe: cerrar sesión siempre funciona.
   *
   * @param rawToken el token de la cookie, o {@code null}
   */
  @Transactional
  public void endSession(String rawToken) {
    if (rawToken == null || rawToken.isBlank()) {
      return;
    }
    tokens.findByTokenHash(hash(rawToken)).ifPresent(token -> token.revoke(now()));
  }

  /**
   * Revoca todas las sesiones abiertas de un usuario (por ejemplo, si ya no puede entrar).
   *
   * @param userId usuario cuyas sesiones se cierran
   */
  @Transactional
  public void endAllSessions(long userId) {
    tokens.revokeAllOfUser(userId, now());
  }

  private RefreshToken find(String rawToken) {
    if (rawToken == null || rawToken.isBlank()) {
      throw new InvalidSessionException();
    }
    return tokens.findByTokenHash(hash(rawToken)).orElseThrow(InvalidSessionException::new);
  }

  private NewRefreshToken save(long userId, Instant now, Instant sessionEnd) {
    var raw = newRawToken();
    var expiresAt = min(now.plus(properties.refreshTokenTtl()), sessionEnd);
    tokens.save(new RefreshToken(userId, hash(raw), now, expiresAt, sessionEnd));
    return new NewRefreshToken(raw, expiresAt);
  }

  private String newRawToken() {
    var bytes = new byte[TOKEN_BYTES];
    random.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  static String hash(String rawToken) {
    try {
      var digest = MessageDigest.getInstance("SHA-256");
      return HexFormat.of().formatHex(digest.digest(rawToken.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 no está disponible", e);
    }
  }

  private static Instant now() {
    return Instant.now().truncatedTo(ChronoUnit.MICROS);
  }

  private static Instant min(Instant a, Instant b) {
    return a.isBefore(b) ? a : b;
  }

  /**
   * Refresh token recién creado.
   *
   * @param value token en claro, para la cookie
   * @param expiresAt cuándo caduca
   */
  public record NewRefreshToken(String value, Instant expiresAt) {

    @Override
    public String toString() {
      return "NewRefreshToken[expiresAt=" + expiresAt + "]";
    }
  }

  /**
   * Resultado de una renovación.
   *
   * @param userId dueño de la sesión
   * @param token refresh token nuevo
   */
  public record Rotation(long userId, NewRefreshToken token) {}
}
