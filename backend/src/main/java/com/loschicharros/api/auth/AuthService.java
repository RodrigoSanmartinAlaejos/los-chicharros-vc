package com.loschicharros.api.auth;

import com.loschicharros.api.auth.dto.AccessTokenResponse;
import com.loschicharros.api.auth.dto.LoginRequest;
import com.loschicharros.api.security.Roles;
import com.loschicharros.api.security.TokenService;
import com.loschicharros.api.users.User;
import com.loschicharros.api.users.UserRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Inicio, renovación y cierre de la sesión. */
@Service
public class AuthService {

  private static final Logger log = LoggerFactory.getLogger(AuthService.class);

  private final UserRepository users;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokens;
  private final RefreshTokenService refreshTokens;

  /**
   * Hash con el que se compara cuando el email no existe, para que la respuesta tarde lo mismo y no
   * delate qué emails hay.
   */
  private final String dummyHash;

  public AuthService(
      UserRepository users,
      PasswordEncoder passwordEncoder,
      TokenService tokens,
      RefreshTokenService refreshTokens) {
    this.users = users;
    this.passwordEncoder = passwordEncoder;
    this.tokens = tokens;
    this.refreshTokens = refreshTokens;
    this.dummyHash = passwordEncoder.encode("contraseña-que-nunca-coincide");
  }

  /**
   * Comprueba las credenciales y abre una sesión: access token y refresh token.
   *
   * @throws InvalidCredentialsException si el email no existe, la contraseña no coincide o la
   *     cuenta no puede entrar; siempre la misma
   */
  @Transactional
  public IssuedSession login(LoginRequest request) {
    var user = users.findByEmail(User.normalizeEmail(request.email()));
    var hash = user.map(User::getPasswordHash).orElse(dummyHash);
    var matches = passwordEncoder.matches(request.password(), hash);
    if (user.isEmpty() || !matches || !user.get().canLogIn()) {
      log.info("Login fallido");
      throw new InvalidCredentialsException();
    }
    log.info("Login correcto del usuario {}", user.get().getId());
    var refresh = refreshTokens.startSession(user.get().getId());
    return new IssuedSession(accessTokenFor(user.get()), refresh.value(), refresh.expiresAt());
  }

  /**
   * Renueva la sesión: rota el refresh token y emite un access token nuevo.
   *
   * @throws InvalidSessionException si el refresh token no vale o la cuenta ya no puede entrar
   */
  @Transactional(noRollbackFor = InvalidSessionException.class)
  public IssuedSession refresh(String rawRefreshToken) {
    var rotation = refreshTokens.rotate(rawRefreshToken);
    var user = users.findById(rotation.userId()).filter(User::canLogIn);
    if (user.isEmpty()) {
      refreshTokens.endAllSessions(rotation.userId());
      throw new InvalidSessionException();
    }
    var refresh = rotation.token();
    return new IssuedSession(accessTokenFor(user.get()), refresh.value(), refresh.expiresAt());
  }

  /**
   * Cierra la sesión del refresh token, si lo hay. El access token caduca solo en 15 minutos como
   * mucho; el frontend lo borra de memoria.
   */
  public void logout(String rawRefreshToken) {
    refreshTokens.endSession(rawRefreshToken);
  }

  private AccessTokenResponse accessTokenFor(User user) {
    var token = tokens.issueAccessToken(user.getId(), user.getEmail(), List.of(Roles.ADMIN));
    return new AccessTokenResponse(token, tokens.accessTokenSeconds());
  }
}
