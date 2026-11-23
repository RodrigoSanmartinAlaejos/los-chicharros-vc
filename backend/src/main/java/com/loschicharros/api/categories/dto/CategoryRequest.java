package com.loschicharros.api.categories.dto;

import com.loschicharros.api.categories.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Datos para crear o editar una categoría (contrato OpenAPI, {@code CategoryRequest}). El máximo de
 * {@code sortOrder} es el de la columna SMALLINT.
 */
public record CategoryRequest(
    @NotBlank @Size(max = 30) String name,
    @NotNull Gender gender,
    @NotNull @PositiveOrZero @Max(Short.MAX_VALUE) Integer sortOrder) {}
