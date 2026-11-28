package com.loschicharros.api.standings;

import com.loschicharros.api.common.error.InvalidFieldException;
import com.loschicharros.api.common.error.ResourceNotFoundException;
import com.loschicharros.api.competitions.Competition;
import com.loschicharros.api.standings.dto.StandingRequest;
import com.loschicharros.api.standings.dto.StandingResponse;
import com.loschicharros.api.teams.Team;
import jakarta.persistence.EntityManager;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Clasificación de cada competición (H21). En la fase 1 la escribe el administrador a mano; la web
 * la muestra ordenada por puntos, partidos ganados y cociente de sets.
 */
@Service
@Transactional(readOnly = true)
public class StandingService {

  /**
   * Puntos, ganados y cociente de sets, de mayor a menor; a igualdad, por nombre del equipo. El
   * cociente de puntos llega en la fase 3, con los puntos de cada set.
   */
  static final Comparator<Standing> ORDER =
      Comparator.comparingInt(Standing::getPoints)
          .thenComparingInt(Standing::getWon)
          .thenComparingDouble(StandingService::setRatio)
          .reversed()
          .thenComparing(standing -> standing.getTeam().getName());

  private final StandingRepository standings;
  private final EntityManager entityManager;
  private final Validator validator;

  public StandingService(
      StandingRepository standings, EntityManager entityManager, Validator validator) {
    this.standings = standings;
    this.entityManager = entityManager;
    this.validator = validator;
  }

  /**
   * Clasificación de una competición, con el puesto de cada equipo.
   *
   * @throws ResourceNotFoundException si la competición no existe
   */
  public List<StandingResponse> findByCompetition(Long competitionId) {
    competition(competitionId);
    return ranked(standings.findByCompetitionId(competitionId));
  }

  /**
   * Sustituye la clasificación entera de una competición: los equipos que no llegan desaparecen de
   * la tabla.
   *
   * @throws ResourceNotFoundException si la competición no existe
   * @throws InvalidFieldException si una fila no es válida (el campo es {@code [i].nombre}), un
   *     equipo no es de la temporada y la categoría de la competición o se repite, o los ganados y
   *     los perdidos no suman los jugados
   */
  @Transactional
  public List<StandingResponse> replace(Long competitionId, List<StandingRequest> rows) {
    var competition = competition(competitionId);
    var seen = new HashSet<Long>();
    var replacement = new ArrayList<Standing>();
    for (var i = 0; i < rows.size(); i++) {
      var row = rows.get(i);
      var prefix = "[" + i + "].";
      if (row == null) {
        throw new InvalidFieldException("[" + i + "]", "La fila está vacía");
      }
      validator.validate(row).stream()
          .min(Comparator.comparing(violation -> violation.getPropertyPath().toString()))
          .ifPresent(violation -> {
            throw new InvalidFieldException(
                prefix + violation.getPropertyPath(), violation.getMessage());
          });
      var team = team(competition, row.teamId(), prefix + "teamId");
      if (!seen.add(team.getId())) {
        throw new InvalidFieldException(
            prefix + "teamId", team.getName() + " aparece dos veces en la clasificación");
      }
      if (row.won() + row.lost() != row.played()) {
        throw new InvalidFieldException(
            prefix + "played", "Los ganados y los perdidos tienen que sumar los partidos jugados");
      }
      replacement.add(
          new Standing(
              competition,
              team,
              row.points(),
              row.played(),
              row.won(),
              row.lost(),
              row.setsWon(),
              row.setsLost(),
              row.penaltyPoints()));
    }
    standings.deleteByCompetition(competitionId);
    return ranked(standings.saveAll(replacement));
  }

  private static List<StandingResponse> ranked(List<Standing> rows) {
    var sorted = rows.stream().sorted(ORDER).toList();
    return IntStream.range(0, sorted.size())
        .mapToObj(i -> StandingResponse.from(i + 1, sorted.get(i)))
        .toList();
  }

  /** Sets ganados entre perdidos; sin sets perdidos, cuenta como infinito si ganó alguno. */
  private static double setRatio(Standing standing) {
    if (standing.getSetsLost() == 0) {
      return standing.getSetsWon() == 0 ? 0 : Double.POSITIVE_INFINITY;
    }
    return (double) standing.getSetsWon() / standing.getSetsLost();
  }

  private Competition competition(Long id) {
    var competition = entityManager.find(Competition.class, id);
    if (competition == null) {
      throw new ResourceNotFoundException("competición", id);
    }
    return competition;
  }

  private Team team(Competition competition, Long id, String field) {
    var team = entityManager.find(Team.class, id);
    if (team == null) {
      throw new InvalidFieldException(field, "No existe el equipo con id " + id);
    }
    if (!team.getSeason().getId().equals(competition.getSeason().getId())
        || !team.getCategory().getId().equals(competition.getCategory().getId())) {
      throw new InvalidFieldException(
          field,
          team.getName() + " no es de la categoría y la temporada de " + competition.getName());
    }
    return team;
  }
}
