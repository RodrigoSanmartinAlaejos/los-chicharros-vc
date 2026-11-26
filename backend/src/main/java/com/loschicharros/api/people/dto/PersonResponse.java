package com.loschicharros.api.people.dto;

import com.loschicharros.api.people.Person;
import java.time.LocalDate;

/**
 * Persona con todos sus datos (contrato OpenAPI, {@code PersonResponse}): solo la ve el
 * administrador. La web pública usa {@code PublicPerson}, con el nombre y la inicial del apellido.
 */
public record PersonResponse(
    Long id,
    String firstName,
    String lastName,
    LocalDate birthDate,
    String photoUrl,
    boolean imageConsent) {

  public static PersonResponse from(Person person) {
    return new PersonResponse(
        person.getId(),
        person.getFirstName(),
        person.getLastName(),
        person.getBirthDate(),
        person.getPhotoUrl(),
        person.hasImageConsent());
  }
}
