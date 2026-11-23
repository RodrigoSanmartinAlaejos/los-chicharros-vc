package com.loschicharros.api.support;

import com.loschicharros.api.common.audit.CurrentUser;
import java.util.Optional;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;

/**
 * Usuario actual que fija el propio test, para comprobar la auditoría sin login. Cuando exista el
 * login (sprint 2), los tests de endpoints usarán un token y esta clase solo hará falta en los que
 * prueban la auditoría sin HTTP.
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
    return Optional.ofNullable(id);
  }
}
