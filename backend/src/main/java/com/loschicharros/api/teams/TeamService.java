package com.loschicharros.api.teams;

import com.loschicharros.api.categories.Category;
import com.loschicharros.api.common.error.ConflictException;
import com.loschicharros.api.common.error.InvalidFieldException;
import com.loschicharros.api.common.error.ResourceNotFoundException;
import com.loschicharros.api.seasons.Season;
import com.loschicharros.api.teams.dto.TeamRequest;
import com.loschicharros.api.teams.dto.TeamResponse;
import com.loschicharros.api.teams.dto.TeamSummary;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Alta, edición, consulta y borrado de equipos, del club y rivales (H17). */
@Service
@Transactional(readOnly = true)
public class TeamService {

  private static final String RESOURCE = "equipo";

  private final TeamRepository teams;
  private final TeamMembershipRepository memberships;
  private final EntityManager entityManager;

  public TeamService(
      TeamRepository teams, TeamMembershipRepository memberships, EntityManager entityManager) {
    this.teams = teams;
    this.memberships = memberships;
    this.entityManager = entityManager;
  }

  /**
   * Equipos que cumplen los filtros que lleguen (cualquiera puede ser {@code null}), por orden de
   * categoría y con los del club primero.
   */
  public List<TeamSummary> search(Long seasonId, Long categoryId, Boolean ownClub) {
    return teams.search(seasonId, categoryId, ownClub).stream().map(TeamSummary::from).toList();
  }

  /**
   * Un equipo con su plantilla.
   *
   * @throws ResourceNotFoundException si no existe
   */
  public TeamResponse findById(Long id) {
    return TeamResponse.from(get(id), memberships.findRoster(id));
  }

  /**
   * Crea un equipo.
   *
   * @throws InvalidFieldException si la temporada o la categoría no existen
   * @throws ConflictException si ya hay un equipo con ese nombre en esa temporada y categoría
   */
  @Transactional
  public TeamResponse create(TeamRequest request) {
    var season = season(request.seasonId());
    var category = category(request.categoryId());
    var name = request.name().strip();
    if (teams.existsBySeasonIdAndCategoryIdAndName(season.getId(), category.getId(), name)) {
      throw duplicated(name);
    }
    var team = teams.save(new Team(season, category, name, request.isOwnClub()));
    return TeamResponse.from(team, List.of());
  }

  /**
   * Edita un equipo.
   *
   * @throws ResourceNotFoundException si no existe
   * @throws InvalidFieldException si la temporada o la categoría no existen
   * @throws ConflictException si otro equipo ya tiene ese nombre en esa temporada y categoría
   */
  @Transactional
  public TeamResponse update(Long id, TeamRequest request) {
    var team = get(id);
    var season = season(request.seasonId());
    var category = category(request.categoryId());
    var name = request.name().strip();
    if (teams.existsBySeasonIdAndCategoryIdAndNameAndIdNot(
        season.getId(), category.getId(), name, id)) {
      throw duplicated(name);
    }
    team.update(season, category, name, request.isOwnClub());
    return TeamResponse.from(team, memberships.findRoster(id));
  }

  /**
   * Borra un equipo y su plantilla.
   *
   * @throws ResourceNotFoundException si no existe
   * @throws ConflictException si tiene partidos o clasificaciones
   */
  @Transactional
  public void delete(Long id) {
    var team = get(id);
    try {
      memberships.deleteByTeamId(id);
      teams.delete(team);
      teams.flush();
    } catch (DataIntegrityViolationException e) {
      throw new ConflictException(
          "No se puede borrar el equipo " + team.getName() + ": tiene partidos o clasificaciones");
    }
  }

  Team get(Long id) {
    return teams.findById(id).orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
  }

  private Season season(Long id) {
    var season = entityManager.find(Season.class, id);
    if (season == null) {
      throw new InvalidFieldException("seasonId", "No existe la temporada con id " + id);
    }
    return season;
  }

  private Category category(Long id) {
    var category = entityManager.find(Category.class, id);
    if (category == null) {
      throw new InvalidFieldException("categoryId", "No existe la categoría con id " + id);
    }
    return category;
  }

  private static ConflictException duplicated(String name) {
    return new ConflictException("Ya existe el equipo " + name + " en esa temporada y categoría");
  }
}
