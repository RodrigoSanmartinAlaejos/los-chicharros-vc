package com.loschicharros.api.competitions;

import com.loschicharros.api.common.error.ConflictException;
import com.loschicharros.api.common.error.InvalidFieldException;
import com.loschicharros.api.common.error.ResourceNotFoundException;
import com.loschicharros.api.competitions.dto.MatchdayRequest;
import com.loschicharros.api.competitions.dto.MatchdayResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Alta, edición y borrado de las jornadas de una competición (H18). */
@Service
@Transactional(readOnly = true)
public class MatchdayService {

  private static final String RESOURCE = "jornada";
  private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  private final MatchdayRepository matchdays;
  private final CompetitionRepository competitions;

  public MatchdayService(MatchdayRepository matchdays, CompetitionRepository competitions) {
    this.matchdays = matchdays;
    this.competitions = competitions;
  }

  /**
   * Crea una jornada.
   *
   * @throws InvalidFieldException si la competición no existe o la fecha cae fuera de su temporada
   * @throws ConflictException si la competición ya tiene una jornada con ese número
   */
  @Transactional
  public MatchdayResponse create(MatchdayRequest request) {
    var competition = competition(request.competitionId());
    checkDate(competition, request.scheduledDate());
    if (matchdays.existsByCompetitionIdAndNumber(competition.getId(), request.number())) {
      throw duplicated(request.number());
    }
    var matchday =
        matchdays.save(new Matchday(competition, request.number(), request.scheduledDate()));
    return MatchdayResponse.from(matchday);
  }

  /**
   * Edita una jornada.
   *
   * @throws ResourceNotFoundException si no existe
   * @throws InvalidFieldException si la competición no existe o la fecha cae fuera de su temporada
   * @throws ConflictException si otra jornada de la competición ya tiene ese número
   */
  @Transactional
  public MatchdayResponse update(Long id, MatchdayRequest request) {
    var matchday = get(id);
    var competition = competition(request.competitionId());
    checkDate(competition, request.scheduledDate());
    if (matchdays.existsByCompetitionIdAndNumberAndIdNot(
        competition.getId(), request.number(), id)) {
      throw duplicated(request.number());
    }
    matchday.update(competition, request.number(), request.scheduledDate());
    return MatchdayResponse.from(matchday);
  }

  /**
   * Borra una jornada.
   *
   * @throws ResourceNotFoundException si no existe
   * @throws ConflictException si tiene partidos
   */
  @Transactional
  public void delete(Long id) {
    var matchday = get(id);
    try {
      matchdays.delete(matchday);
      matchdays.flush();
    } catch (DataIntegrityViolationException e) {
      throw new ConflictException(
          "No se puede borrar la jornada " + matchday.getNumber() + ": tiene partidos");
    }
  }

  private Matchday get(Long id) {
    return matchdays.findById(id).orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
  }

  private Competition competition(Long id) {
    return competitions
        .findById(id)
        .orElseThrow(() ->
            new InvalidFieldException("competitionId", "No existe la competición con id " + id));
  }

  /** La fecha prevista, si llega, tiene que caer dentro de la temporada de la competición. */
  private static void checkDate(Competition competition, LocalDate date) {
    var season = competition.getSeason();
    if (date != null
        && (date.isBefore(season.getStartDate()) || date.isAfter(season.getEndDate()))) {
      throw new InvalidFieldException(
          "scheduledDate",
          "La fecha tiene que estar dentro de la temporada "
              + season.getName()
              + " (del "
              + DAY.format(season.getStartDate())
              + " al "
              + DAY.format(season.getEndDate())
              + ")");
    }
  }

  private static ConflictException duplicated(int number) {
    return new ConflictException("Ya existe la jornada " + number + " en esta competición");
  }
}
