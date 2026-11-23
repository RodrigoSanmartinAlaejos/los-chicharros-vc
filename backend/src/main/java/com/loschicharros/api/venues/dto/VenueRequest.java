package com.loschicharros.api.venues.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Datos para crear o editar una instalación (contrato OpenAPI, {@code VenueRequest}). Las
 * coordenadas admiten hasta seis decimales, como la columna DECIMAL(9, 6).
 */
public record VenueRequest(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Size(max = 200) String address,
    @DecimalMin("-90") @DecimalMax("90") @Digits(integer = 2, fraction = 6) BigDecimal latitude,
    @DecimalMin("-180") @DecimalMax("180") @Digits(integer = 3, fraction = 6)
        BigDecimal longitude) {}
