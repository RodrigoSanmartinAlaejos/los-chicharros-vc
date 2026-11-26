package com.loschicharros.api.people.dto;

import com.loschicharros.api.people.Person;

/**
 * Persona tal como se ve en la web pública (contrato OpenAPI, {@code PublicPerson}): nombre e
 * inicial del apellido, y la foto solo con consentimiento de imagen (reglas técnicas, «Formato»).
 */
public record PublicPerson(Long id, String displayName, String photoUrl) {

  public static PublicPerson from(Person person) {
    var lastName = person.getLastName().strip();
    var initial = lastName.isEmpty() ? "" : " " + lastName.substring(0, 1) + ".";
    return new PublicPerson(
        person.getId(),
        person.getFirstName() + initial,
        person.hasImageConsent() ? person.getPhotoUrl() : null);
  }
}
