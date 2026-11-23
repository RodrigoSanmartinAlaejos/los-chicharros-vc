package com.loschicharros.api.common.audit;

import java.util.Optional;

/**
 * Usuario que hace la petición actual, para la auditoría ({@code created_by} y {@code updated_by}).
 *
 * <p>Hasta que exista el login (sprint 2) solo hay una implementación anónima; la de Spring
 * Security la sustituirá leyendo el usuario del token.
 */
public interface CurrentUser {

  /** Id del usuario autenticado, o vacío si la petición no tiene sesión o la lanza el sistema. */
  Optional<Long> id();
}
