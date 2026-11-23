package com.loschicharros.api.common.audit;

import java.util.Optional;
import org.springframework.stereotype.Component;

/** Implementación provisional hasta el login: ninguna petición tiene usuario. */
@Component
class AnonymousCurrentUser implements CurrentUser {

  @Override
  public Optional<Long> id() {
    return Optional.empty();
  }
}
