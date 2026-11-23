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

/** Inicio de sesión con email y contraseña. */
@Service
@Transactional(readOnly = true)
public class AuthService {

  private static final Logger log = LoggerFactory.getLogger(AuthService.class);

  private final UserRepository users;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokens;

  /**
   * Hash con el que se compara cuando el email no existe, para que la respuesta tarde lo mismo y no
   * delate qué emails hay.
   */
  private final String dummyHash;

  public AuthService(UserRepository users, PasswordEncoder passwordEncoder, TokenService tokens) {
    this.users = users;
    this.passwordEncoder = passwordEncoder;
    this.tokens = tokens;
    this.dummyHash = passwordEncoder.encode("contraseña-que-nunca-coincide");
  }

  /**
   * Comprueba las credenciales y emite un access token.
   *
   * @throws InvalidCredentialsException si el email no existe, la contraseña no coincide o la
   *     cuenta no puede entrar; siempre la misma
   */
  public AccessTokenResponse login(LoginRequest request) {
    var user = users.findByEmail(User.normalizeEmail(request.email()));
    var hash = user.map(User::getPasswordHash).orElse(dummyHash);
    var matches = passwordEncoder.matches(request.password(), hash);
    if (user.isEmpty() || !matches || !user.get().canLogIn()) {
      log.info("Login fallido");
      throw new InvalidCredentialsException();
    }
    var id = user.get().getId();
    log.info("Login correcto del usuario {}", id);
    return new AccessTokenResponse(
        tokens.issueAccessToken(id, List.of(Roles.ADMIN)), tokens.accessTokenSeconds());
  }
}
