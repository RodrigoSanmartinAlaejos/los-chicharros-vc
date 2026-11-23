package com.loschicharros.api.support;

import com.loschicharros.api.common.audit.CurrentUser;
import java.util.Optional;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Usuario actual para la auditoría: el que fije el propio test con {@link #set} o, si no fija
 * ninguno, el del access token de la petición, como en la aplicación. Los tests de endpoints usan
 * un token ({@link AuthTokens}); {@code set} queda para probar la auditoría sin login.
 */
@TestComponent
@Primary
public class TestCurrentUser implements CurrentUser {

  private volatile Long id;

  public void set(long userId) {
    this.id = userId;
  }

  public void clear() {
    this.id = null;
  }

  @Override
  public Optional<Long> id() {
    if (id != null) {
      return Optional.of(id);
    }
    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof JwtAuthenticationToken token) {
      return Optional.of(Long.valueOf(token.getToken().getSubject()));
    }
    return Optional.empty();
  }
}
