package com.loschicharros.api.people.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Datos para dar de alta o editar una persona (contrato OpenAPI, {@code PersonRequest}). La fecha
 * de nacimiento es opcional (los entrenadores adultos no la necesitan) y el consentimiento de
 * imagen, si no llega, es {@code false}.
 */
public record PersonRequest(
    @NotBlank @Size(max = 60) String firstName,
    @NotBlank @Size(max = 100) String lastName,
    @PastOrPresent LocalDate birthDate,
    Boolean imageConsent) {

  /** El consentimiento de imagen solo está dado si llega {@code true}. */
  public boolean hasImageConsent() {
    return Boolean.TRUE.equals(imageConsent);
  }
}
