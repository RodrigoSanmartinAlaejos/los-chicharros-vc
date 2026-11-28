package com.loschicharros.api.competitions;

import com.loschicharros.api.categories.Category;
import com.loschicharros.api.common.error.ConflictException;
import com.loschicharros.api.common.error.InvalidFieldException;
import com.loschicharros.api.common.error.ResourceNotFoundException;
import com.loschicharros.api.competitions.dto.CompetitionRequest;
import com.loschicharros.api.competitions.dto.CompetitionResponse;
import com.loschicharros.api.competitions.dto.MatchdayResponse;
import com.loschicharros.api.seasons.Season;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Alta, edición, consulta y borrado de competiciones (H18). */
@Service
@Transactional(readOnly = true)
public class CompetitionService {

  private static final String RESOURCE = "competición";

  private final CompetitionRepository competitions;
  private final MatchdayRepository matchdays;
  private final EntityManager entityManager;

  public CompetitionService(
      CompetitionRepository competitions,
      MatchdayRepository matchdays,
      EntityManager entityManager) {
    this.competitions = competitions;
    this.matchdays = matchdays;
    this.entityManager = entityManager;
  }

  /**
   * Competiciones de una temporada (si no llega, la activa; si no hay activa, ninguna) y de una
   * categoría si llega, por orden de categoría y nombre.
   *
   * @throws InvalidFieldException si la temporada o la categoría no existen
   */
  public List<CompetitionResponse> search(Long seasonId, Long categoryId) {
    Long season = seasonId == null ? activeSeasonId() : season(seasonId).getId();
    if (categoryId != null) {
      category(categoryId);
    }
    if (season == null) {
      return List.of();
    }
    return competitions.search(season, categoryId).stream().map(CompetitionResponse::from).toList();
  }

  /**
   * Una competición.
   *
   * @throws ResourceNotFoundException si no existe
   */
  public CompetitionResponse findById(Long id) {
    return CompetitionResponse.from(get(id));
  }

  /**
   * Jornadas de una competición, por número.
   *
   * @throws ResourceNotFoundException si la competición no existe
   */
  public List<MatchdayResponse> matchdays(Long id) {
    get(id);
    return matchdays.findByCompetitionIdOrderByNumber(id).stream()
        .map(MatchdayResponse::from)
        .toList();
  }

  /**
   * Crea una competición.
   *
   * @throws InvalidFieldException si la temporada o la categoría no existen
   * @throws ConflictException si ya hay una con ese nombre en esa temporada y categoría
   */
  @Transactional
  public CompetitionResponse create(CompetitionRequest request) {
    var season = season(request.seasonId());
    var category = category(request.categoryId());
    var name = request.name().strip();
    if (competitions.existsBySeasonIdAndCategoryIdAndName(season.getId(), category.getId(), name)) {
      throw duplicated(name);
    }
    var competition =
        competitions.save(
            new Competition(
                season,
                category,
                name,
                federation(request),
                request.forfeitPenaltyPointsOrDefault()));
    return CompetitionResponse.from(competition);
  }

  /**
   * Edita una competición.
   *
   * @throws ResourceNotFoundException si no existe
   * @throws InvalidFieldException si la temporada o la categoría no existen
   * @throws ConflictException si otra ya tiene ese nombre en esa temporada y categoría
   */
  @Transactional
  public CompetitionResponse update(Long id, CompetitionRequest request) {
    var competition = get(id);
    var season = season(request.seasonId());
    var category = category(request.categoryId());
    var name = request.name().strip();
    if (competitions.existsBySeasonIdAndCategoryIdAndNameAndIdNot(
        season.getId(), category.getId(), name, id)) {
      throw duplicated(name);
    }
    competition.update(
        season, category, name, federation(request), request.forfeitPenaltyPointsOrDefault());
    return CompetitionResponse.from(competition);
  }

  /**
   * Borra una competición con sus jornadas y su clasificación.
   *
   * @throws ResourceNotFoundException si no existe
   * @throws ConflictException si tiene partidos
   */
  @Transactional
  public void delete(Long id) {
    var competition = get(id);
    try {
      competitions.delete(competition);
      competitions.flush();
    } catch (DataIntegrityViolationException e) {
      throw new ConflictException(
          "No se puede borrar la competición " + competition.getName() + ": tiene partidos");
    }
  }

  Competition get(Long id) {
    return competitions.findById(id).orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
  }

  private Long activeSeasonId() {
    return entityManager
        .createQuery("SELECT s.id FROM Season s WHERE s.active = true", Long.class)
        .getResultStream()
        .findFirst()
        .orElse(null);
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

  private static String federation(CompetitionRequest request) {
    var federation = request.federation();
    return federation == null || federation.isBlank() ? null : federation.strip();
  }

  private static ConflictException duplicated(String name) {
    return new ConflictException(
        "Ya existe la competición " + name + " en esa temporada y categoría");
  }
}
