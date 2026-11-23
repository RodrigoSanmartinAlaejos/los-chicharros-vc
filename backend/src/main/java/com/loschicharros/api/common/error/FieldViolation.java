package com.loschicharros.api.common.error;

/** Un campo no válido dentro de la propiedad {@code errors} de un error 400. */
public record FieldViolation(String field, String message) {}
