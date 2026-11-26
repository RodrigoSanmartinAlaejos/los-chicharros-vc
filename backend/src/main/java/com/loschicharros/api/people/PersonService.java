package com.loschicharros.api.people;

import com.loschicharros.api.common.error.InvalidFieldException;
import com.loschicharros.api.common.error.ResourceNotFoundException;
import com.loschicharros.api.common.web.PageResponse;
import com.loschicharros.api.people.dto.PersonRequest;
import com.loschicharros.api.people.dto.PersonResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Alta, búsqueda, edición y borrado lógico de jugadores y entrenadores (H16). */
@Service
@Transactional(readOnly = true)
public class PersonService {

  private static final String RESOURCE = "persona";

  /** Campos por los que se puede ordenar el listado del panel. */
  private static final Set<String> SORTABLE = Set.of("firstName", "lastName", "birthDate");

  /** Orden por defecto, y desempate de cualquier otro: apellidos, nombre e id. */
  private static final Sort DEFAULT_SORT = Sort.by("lastName", "firstName", "id");

  private final PersonRepository people;
  private final ApplicationEventPublisher events;

  public PersonService(PersonRepository people, ApplicationEventPublisher events) {
    this.people = people;
    this.events = events;
  }

  /**
   * Página de personas cuyo nombre, apellidos o nombre completo contienen {@code search}, sin
   * distinguir mayúsculas ni tildes. Sin texto, devuelve todas. Las borradas nunca aparecen.
   *
   * @throws InvalidFieldException si se pide ordenar por un campo que no es ordenable
   */
  public PageResponse<PersonResponse> search(String search, Pageable pageable) {
    var text = search == null ? "" : search.strip();
    var page = people.search("%" + escapeLike(text) + "%", withDefaultSort(pageable));
    return PageResponse.from(page, PersonResponse::from);
  }

  /**
   * Una persona por su id.
   *
   * @throws ResourceNotFoundException si no existe o está borrada
   */
  public PersonResponse findById(Long id) {
    return PersonResponse.from(get(id));
  }

  /** Da de alta una persona. */
  @Transactional
  public PersonResponse create(PersonRequest request) {
    var person =
        new Person(
            request.firstName().strip(),
            request.lastName().strip(),
            request.birthDate(),
            request.hasImageConsent());
    return PersonResponse.from(people.save(person));
  }

  /**
   * Edita una persona.
   *
   * @throws ResourceNotFoundException si no existe o está borrada
   */
  @Transactional
  public PersonResponse update(Long id, PersonRequest request) {
    var person = get(id);
    person.update(
        request.firstName().strip(),
        request.lastName().strip(),
        request.birthDate(),
        request.hasImageConsent());
    return PersonResponse.from(person);
  }

  /**
   * Borrado lógico: la persona deja de aparecer, pero su fila se conserva para el historial. Avisa
   * con {@link PersonDeletedEvent} para que las plantillas de las temporadas en curso la quiten;
   * las de temporadas pasadas la conservan.
   *
   * @throws ResourceNotFoundException si no existe o ya está borrada
   */
  @Transactional
  public void delete(Long id) {
    get(id).markDeleted(Instant.now().truncatedTo(ChronoUnit.MICROS));
    events.publishEvent(new PersonDeletedEvent(id));
  }

  private Person get(Long id) {
    return people
        .findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
  }

  /** Añade el orden por defecto como desempate y rechaza los campos que no se pueden ordenar. */
  private static Pageable withDefaultSort(Pageable pageable) {
    for (var order : pageable.getSort()) {
      if (!SORTABLE.contains(order.getProperty())) {
        throw new InvalidFieldException("sort", "No se puede ordenar por " + order.getProperty());
      }
    }
    return PageRequest.of(
        pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort().and(DEFAULT_SORT));
  }

  /** Los comodines de LIKE ({@code %} y {@code _}) del texto se buscan tal cual. */
  private static String escapeLike(String text) {
    return text.replace("!", "!!").replace("%", "!%").replace("_", "!_");
  }
}
