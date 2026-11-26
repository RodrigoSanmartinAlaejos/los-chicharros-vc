package com.loschicharros.api.teams.dto;

import com.loschicharros.api.teams.MembershipType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Persona que se añade a una plantilla (contrato OpenAPI, {@code MembershipRequest}). El dorsal y
 * la posición son opcionales y solo valen para jugadores.
 */
public record MembershipRequest(
    @NotNull Long personId,
    @NotNull MembershipType type,
    @Min(0) @Max(99) Integer shirtNumber,
    @Size(max = 30) String position) {}
