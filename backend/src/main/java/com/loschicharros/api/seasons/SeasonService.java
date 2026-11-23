package com.loschicharros.api.seasons;

import com.loschicharros.api.common.error.ConflictException;
import com.loschicharros.api.common.error.InvalidFieldException;
import com.loschicharros.api.common.error.ResourceNotFoundException;
import com.loschicharros.api.seasons.dto.SeasonRequest;
import com.loschicharros.api.seasons.dto.SeasonResponse;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Alta, edición y borrado de temporadas; garantiza que solo haya una activa. */
@Service
@Transactional(readOnly = true)
public class SeasonService {

  private static final String RESOURCE = "temporada";

  private final SeasonRepository seasons;

  public SeasonService(SeasonRepository seasons) {
    this.seasons = seasons;
  }

  /** Todas las temporadas, de la más reciente a la más antigua. */
  public List<SeasonResponse> findAll() {
    return seasons.findAllByOrderByStartDateDesc().stream().map(SeasonResponse::from).toList();
  }

  /**
   * Crea una temporada. Si llega activa, desactiva la que lo estuviera.
   *
   * @throws ConflictException si ya hay una temporada con ese nombre
   * @throws InvalidFieldException si la fecha de fin no es posterior a la de inicio
   */
  @Transactional
  public SeasonResponse create(SeasonRequest request) {
    checkDates(request);
    if (seasons.existsByName(request.name())) {
      throw duplicated(request.name());
    }
    var season =
        new Season(
            request.name(), request.startDate(), request.endDate(), request.activeOrDefault());
    if (season.isActive()) {
      deactivateOthers(null);
    }
    return SeasonResponse.from(seasons.save(season));
  }

  /**
   * Edita una temporada. Si queda activa, desactiva la que lo estuviera.
   *
   * @throws ResourceNotFoundException si no existe
   * @throws ConflictException si otra temporada ya tiene ese nombre
   * @throws InvalidFieldException si la fecha de fin no es posterior a la de inicio
   */
  @Transactional
  public SeasonResponse update(Long id, SeasonRequest request) {
    var season = get(id);
    checkDates(request);
    if (seasons.existsByNameAndIdNot(request.name(), id)) {
      throw duplicated(request.name());
    }
    season.update(
        request.name(), request.startDate(), request.endDate(), request.activeOrDefault());
    if (season.isActive()) {
      deactivateOthers(id);
    }
    return SeasonResponse.from(season);
  }

  /**
   * Borra una temporada.
   *
   * @throws ResourceNotFoundException si no existe
   * @throws ConflictException si tiene equipos o competiciones
   */
  @Transactional
  public void delete(Long id) {
    var season = get(id);
    try {
      seasons.delete(season);
      seasons.flush();
    } catch (DataIntegrityViolationException e) {
      throw new ConflictException(
          "No se puede borrar la temporada "
              + season.getName()
              + ": tiene equipos o competiciones");
    }
  }

  private Season get(Long id) {
    return seasons.findById(id).orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
  }

  /**
   * Desactiva las demás por la entidad, no con un UPDATE masivo, para que la auditoría registre
   * quién y cuándo las cambió.
   */
  private void deactivateOthers(Long keepId) {
    seasons.findByActiveTrue().stream()
        .filter(other -> !other.getId().equals(keepId))
        .forEach(Season::deactivate);
  }

  private static void checkDates(SeasonRequest request) {
    if (!request.endDate().isAfter(request.startDate())) {
      throw new InvalidFieldException("endDate", "debe ser posterior a la fecha de inicio");
    }
  }

  private static ConflictException duplicated(String name) {
    return new ConflictException("Ya existe la temporada " + name);
  }
}
