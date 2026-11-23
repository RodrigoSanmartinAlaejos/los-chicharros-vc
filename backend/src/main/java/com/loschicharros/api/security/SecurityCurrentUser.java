package com.loschicharros.api.security;

import com.loschicharros.api.common.audit.CurrentUser;
import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/** Usuario de la petición en curso, sacado del access token; vacío sin sesión. */
@Component
class SecurityCurrentUser implements CurrentUser {

  @Override
  public Optional<Long> id() {
    if (SecurityContextHolder.getContext().getAuthentication()
        instanceof JwtAuthenticationToken token) {
      return Optional.of(Long.valueOf(token.getToken().getSubject()));
    }
    return Optional.empty();
  }
}
