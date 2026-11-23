package com.loschicharros.api.security;

/** Roles de la matriz de permisos. En la fase 1 solo existe el administrador. */
public final class Roles {

  /** Nombre del rol en la claim {@code roles} del token y en {@code hasRole(...)}. */
  public static final String ADMIN = "ADMIN";

  /** Expresión para {@code @PreAuthorize} de las acciones solo del administrador. */
  public static final String ONLY_ADMIN = "hasRole('" + ADMIN + "')";

  private Roles() {}
}
