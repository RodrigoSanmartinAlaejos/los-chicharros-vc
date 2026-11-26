package com.loschicharros.api.teams;

import com.loschicharros.api.common.error.ConflictException;
import com.loschicharros.api.common.error.InvalidFieldException;
import com.loschicharros.api.common.error.ResourceNotFoundException;
import com.loschicharros.api.people.Person;
import com.loschicharros.api.people.PersonDeletedEvent;
import com.loschicharros.api.people.dto.PublicPerson;
import com.loschicharros.api.teams.dto.MembershipPatchRequest;
import com.loschicharros.api.teams.dto.MembershipRequest;
import com.loschicharros.api.teams.dto.MembershipResponse;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Plantillas de los equipos (H17): añadir jugadores con dorsal y posición, y entrenadores; cambiar
 * el dorsal o la posición; y quitar a alguien. El dorsal es único dentro de cada equipo.
 */
@Service
@Transactional(readOnly = true)
public class RosterService {

  private final TeamService teams;
  private final TeamMembershipRepository memberships;
  private final EntityManager entityManager;

  public RosterService(
      TeamService teams, TeamMembershipRepository memberships, EntityManager entityManager) {
    this.teams = teams;
    this.memberships = memberships;
    this.entityManager = entityManager;
  }

  /**
   * Añade una persona a la plantilla de un equipo.
   *
   * @throws ResourceNotFoundException si el equipo no existe
   * @throws InvalidFieldException si la persona no existe o es un entrenador con dorsal o posición
   * @throws ConflictException si la persona ya está con ese papel o el dorsal está ocupado
   */
  @Transactional
  public MembershipResponse add(Long teamId, MembershipRequest request) {
    var team = teams.get(teamId);
    var person = entityManager.find(Person.class, request.personId());
    if (person == null || person.isDeleted()) {
      throw new InvalidFieldException(
          "personId", "No existe la persona con id " + request.personId());
    }
    var position = clean(request.position());
    checkCoachFields(request.type(), request.shirtNumber(), position);
    if (memberships.existsByTeamIdAndPersonIdAndType(teamId, person.getId(), request.type())) {
      throw new ConflictException(
          PublicPerson.from(person).displayName()
              + " ya está en la plantilla como "
              + (request.type() == MembershipType.PLAYER ? "jugador" : "entrenador"));
    }
    checkShirtNumberIsFree(teamId, request.shirtNumber(), null);
    var membership =
        memberships.save(
            new TeamMembership(team, person, request.type(), request.shirtNumber(), position));
    return MembershipResponse.from(membership);
  }

  /**
   * Cambia el dorsal y la posición de alguien de la plantilla.
   *
   * @throws ResourceNotFoundException si el equipo o la pertenencia no existen
   * @throws InvalidFieldException si es un entrenador y se le pone dorsal o posición
   * @throws ConflictException si el dorsal lo lleva otra persona del equipo
   */
  @Transactional
  public MembershipResponse change(Long teamId, Long membershipId, MembershipPatchRequest request) {
    var membership = get(teamId, membershipId);
    var position = clean(request.position());
    checkCoachFields(membership.getType(), request.shirtNumber(), position);
    checkShirtNumberIsFree(teamId, request.shirtNumber(), membershipId);
    membership.change(request.shirtNumber(), position);
    return MembershipResponse.from(membership);
  }

  /**
   * Quita a alguien de la plantilla.
   *
   * @throws ResourceNotFoundException si el equipo o la pertenencia no existen
   */
  @Transactional
  public void remove(Long teamId, Long membershipId) {
    memberships.delete(get(teamId, membershipId));
  }

  /**
   * Al borrar una persona, deja de estar en las plantillas de las temporadas que no han terminado
   * (así su dorsal queda libre); en las de temporadas pasadas se conserva.
   */
  @EventListener
  @Transactional
  public void onPersonDeleted(PersonDeletedEvent event) {
    memberships.deleteCurrentOfPerson(event.personId(), LocalDate.now());
  }

  private TeamMembership get(Long teamId, Long membershipId) {
    teams.get(teamId);
    return memberships
        .findByIdAndTeamId(membershipId, teamId)
        .orElseThrow(() ->
            new ResourceNotFoundException(
                "No existe esa persona en la plantilla del equipo con id " + teamId));
  }

  private static void checkCoachFields(MembershipType type, Integer shirtNumber, String position) {
    if (type != MembershipType.COACH) {
      return;
    }
    if (shirtNumber != null) {
      throw new InvalidFieldException("shirtNumber", "Los entrenadores no llevan dorsal");
    }
    if (position != null) {
      throw new InvalidFieldException("position", "Los entrenadores no tienen posición");
    }
  }

  private void checkShirtNumberIsFree(Long teamId, Integer shirtNumber, Long exceptMembershipId) {
    if (shirtNumber == null) {
      return;
    }
    memberships
        .findByTeamIdAndShirtNumber(teamId, shirtNumber)
        .filter(other -> !other.getId().equals(exceptMembershipId))
        .ifPresent(other -> {
          throw new ConflictException(
              "El dorsal "
                  + shirtNumber
                  + " ya lo lleva "
                  + PublicPerson.from(other.getPerson()).displayName());
        });
  }

  private static String clean(String text) {
    return text == null || text.isBlank() ? null : text.strip();
  }
}
