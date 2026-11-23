package com.loschicharros.api.auth.dto;

/**
 * Access token recién emitido (contrato OpenAPI, {@code AccessTokenResponse}). El frontend lo
 * guarda solo en memoria.
 *
 * @param accessToken JWT para la cabecera {@code Authorization: Bearer}
 * @param expiresIn segundos de validez
 */
public record AccessTokenResponse(String accessToken, long expiresIn) {}
