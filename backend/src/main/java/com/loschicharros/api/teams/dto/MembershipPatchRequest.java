package com.loschicharros.api.teams.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Nuevo dorsal y nueva posición de un jugador (contrato OpenAPI, {@code MembershipPatchRequest}).
 * Se cambian los dos a la vez: un campo que no llega queda vacío.
 */
public record MembershipPatchRequest(
    @Min(0) @Max(99) Integer shirtNumber, @Size(max = 30) String position) {}
