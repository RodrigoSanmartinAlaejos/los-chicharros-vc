package com.loschicharros.api.matches;

import com.loschicharros.api.common.error.ConflictException;
import com.loschicharros.api.common.error.InvalidFieldException;
import com.loschicharros.api.common.error.ResourceNotFoundException;
import com.loschicharros.api.common.time.ClubTime;
import com.loschicharros.api.common.web.PageResponse;
import com.loschicharros.api.competitions.CompetitionService;
import com.loschicharros.api.competitions.Matchday;
import com.loschicharros.api.matches.dto.MatchPatchRequest;
import com.loschicharros.api.matches.dto.MatchRequest;
import com.loschicharros.api.matches.dto.MatchResponse;
import com.loschicharros.api.matches.dto.MatchdayWithMatches;
import com.loschicharros.api.teams.Team;
import com.loschicharros.api.venues.Venue;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Calendario: alta, edición, aplazamiento, cancelación, consulta y borrado de partidos (H19). */
@Service
@Transactional(readOnly = true)
public class MatchService {

  private static final String RESOURCE = "partido";
  private static final Set<String> SORTABLE = Set.of("startsAt");
  private static final Sort DEFAULT_SORT = Sort.by("startsAt", "id");
  private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  /** Estados que se ponen a mano; {@code FINISHED} llega con el marcador (H20). */
  private static final Set<MatchStatus> MANUAL_STATUSES =
      EnumSet.of(MatchStatus.SCHEDULED, MatchStatus.POSTPONED, MatchStatus.CANCELLED);

  private final MatchRepository matches;
  private final CompetitionService competitions;
  private final EntityManager entityManager;

  public MatchService(
      MatchRepository matches, CompetitionService competitions, EntityManager entityManager) {
    this.matches = matches;
    this.competitions = competitions;
    this.entityManager = entityManager;
  }

  /**
   * Página de partidos con los filtros que lleguen: equipo (de local o de visitante), pabellón,
   * días del club de {@code from} a {@code to} (incluidos) y estado. Por defecto, por fecha y hora.
   *
   * @throws InvalidFieldException si {@code to} es anterior a {@code from} o se pide ordenar por un
   *     campo que no es ordenable
   */
  public PageResponse<MatchResponse> search(
      Long teamId,
      Long venueId,
      LocalDate from,
      LocalDate to,
      MatchStatus status,
      Pageable pageable) {
    if (from != null && to != null && to.isBefore(from)) {
      throw new InvalidFieldException("to", "La fecha final no puede ser anterior a la inicial");
    }
    var page =
        matches.search(
            teamId,
            venueId,
            from == null ? null : ClubTime.startOf(from),
            to == null ? null : ClubTime.startOf(to.plusDays(1)),
            status,
            withDefaultSort(pageable));
    return PageResponse.from(page, MatchResponse::from);
  }

  /**
   * Un partido.
   *
   * @throws ResourceNotFoundException si no existe
   */
  public MatchResponse findById(Long id) {
    return MatchResponse.from(get(id));
  }

  /**
   * Jornadas de una competición, por número, cada una con sus partidos por fecha y hora.
   *
   * @throws ResourceNotFoundException si la competición no existe
   */
  public List<MatchdayWithMatches> matchdaysOf(Long competitionId) {
    var matchdays = competitions.matchdaysOf(competitionId);
    var byMatchday =
        matchdays.isEmpty()
            ? Map.<Long, List<MatchResponse>>of()
            : matches.findByMatchdays(matchdays.stream().map(Matchday::getId).toList()).stream()
                .collect(
                    Collectors.groupingBy(
                        match -> match.getMatchday().getId(),
                        Collectors.mapping(MatchResponse::from, Collectors.toList())));
    return matchdays.stream()
        .map(matchday ->
            MatchdayWithMatches.from(
                matchday, byMatchday.getOrDefault(matchday.getId(), List.of())))
        .toList();
  }

  /**
   * Crea un partido programado.
   *
   * @throws InvalidFieldException si la jornada, los equipos o el pabellón no existen, los equipos
   *     no son de la temporada y la categoría de la competición, son el mismo o la fecha cae fuera
   *     de la temporada
   * @throws ConflictException si alguno de los equipos ya juega en esa jornada
   */
  @Transactional
  public MatchResponse create(MatchRequest request) {
    var data = check(request, 0L);
    var match =
        matches.save(
            new Match(data.matchday(), data.home(), data.away(), data.venue(), request.startsAt()));
    return MatchResponse.from(match);
  }

  /**
   * Sustituye la jornada, los equipos, el pabellón y la hora de un partido; el estado y el marcador
   * no cambian.
   *
   * @throws ResourceNotFoundException si no existe
   * @throws InvalidFieldException en los mismos casos que al crearlo
   * @throws ConflictException si alguno de los equipos ya juega otro partido en esa jornada
   */
  @Transactional
  public MatchResponse update(Long id, MatchRequest request) {
    var match = get(id);
    var data = check(request, id);
    match.update(data.matchday(), data.home(), data.away(), data.venue(), request.startsAt());
    return MatchResponse.from(match);
  }

  /**
   * Cambia un partido. Con marcador (H20), registra el resultado y lo deja finalizado. Sin él,
   * aplaza, cancela o vuelve a programar el partido, y cambia su hora si llega; al dejar de estar
   * finalizado pierde el marcador.
   *
   * @throws ResourceNotFoundException si no existe
   * @throws InvalidFieldException si el marcador es imposible o el partido aún no se ha jugado, si
   *     el estado no se puede poner a mano o si la nueva hora cae fuera de la temporada
   */
  @Transactional
  public MatchResponse change(Long id, MatchPatchRequest request) {
    var match = get(id);
    if (request.hasScore()) {
      finish(match, request);
      return MatchResponse.from(match);
    }
    if (request.status() != null) {
      if (request.status() == MatchStatus.FINISHED) {
        throw new InvalidFieldException(
            "status", "Para finalizar un partido, indica su marcador en sets");
      }
      if (!MANUAL_STATUSES.contains(request.status())) {
        throw new InvalidFieldException(
            "status", "Un partido solo se puede programar, aplazar o cancelar desde aquí");
      }
      match.changeStatus(request.status());
    }
    if (request.startsAt() != null) {
      checkSeason(match.getMatchday(), request.startsAt());
      match.reschedule(request.startsAt());
    }
    return MatchResponse.from(match);
  }

  /** Registra el marcador: uno de los dos llega a 3 sets y el otro se queda por debajo. */
  private void finish(Match match, MatchPatchRequest request) {
    if (request.status() != null && request.status() != MatchStatus.FINISHED) {
      throw new InvalidFieldException(
          "status", "Con el marcador, el partido queda finalizado: no se puede poner otro estado");
    }
    if (request.startsAt() != null) {
      throw new InvalidFieldException(
          "startsAt", "La hora se cambia aparte, no a la vez que el marcador");
    }
    var home = request.homeSets();
    var away = request.awaySets();
    if (home == null || away == null) {
      throw new InvalidFieldException(
          home == null ? "homeSets" : "awaySets", "Faltan los sets de uno de los dos equipos");
    }
    if (!(home == 3 && away < 3) && !(away == 3 && home < 3)) {
      throw new InvalidFieldException(
          "homeSets", "Uno de los dos tiene que llegar a 3 sets y el otro quedarse en menos de 3");
    }
    if (match.getStartsAt().isAfter(Instant.now())) {
      throw new InvalidFieldException(
          "homeSets", "El partido todavía no se ha jugado: no puede tener resultado");
    }
    match.finish(home, away);
  }

  /**
   * Borra un partido.
   *
   * @throws ResourceNotFoundException si no existe
   */
  @Transactional
  public void delete(Long id) {
    matches.delete(get(id));
  }

  private Match get(Long id) {
    return matches.findById(id).orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
  }

  private record Checked(Matchday matchday, Team home, Team away, Venue venue) {}

  /** Comprueba los datos de un partido; {@code id} es 0 al crearlo. */
  private Checked check(MatchRequest request, Long id) {
    var matchday = find(Matchday.class, request.matchdayId(), "matchdayId", "la jornada");
    var home = find(Team.class, request.homeTeamId(), "homeTeamId", "el equipo");
    var away = find(Team.class, request.awayTeamId(), "awayTeamId", "el equipo");
    var venue =
        request.venueId() == null
            ? null
            : find(Venue.class, request.venueId(), "venueId", "la instalación");
    if (home.getId().equals(away.getId())) {
      throw new InvalidFieldException(
          "awayTeamId", "El visitante tiene que ser un equipo distinto del local");
    }
    var competition = matchday.getCompetition();
    for (var team : List.of(home, away)) {
      if (!team.getSeason().getId().equals(competition.getSeason().getId())
          || !team.getCategory().getId().equals(competition.getCategory().getId())) {
        throw new InvalidFieldException(
            team == home ? "homeTeamId" : "awayTeamId",
            team.getName() + " no es de la categoría y la temporada de " + competition.getName());
      }
    }
    checkSeason(matchday, request.startsAt());
    var busy =
        matches.findInMatchday(matchday.getId(), List.of(home.getId(), away.getId()), id).stream()
            .flatMap(match -> Stream.of(match.getHomeTeam(), match.getAwayTeam()))
            .filter(team -> team.getId().equals(home.getId()) || team.getId().equals(away.getId()))
            .map(Team::getName)
            .filter(Objects::nonNull)
            .findFirst();
    if (busy.isPresent()) {
      throw new ConflictException(
          busy.get() + " ya juega otro partido en la jornada " + matchday.getNumber());
    }
    return new Checked(matchday, home, away, venue);
  }

  /** El partido tiene que jugarse dentro de la temporada de su competición (en días del club). */
  private static void checkSeason(Matchday matchday, Instant startsAt) {
    var season = matchday.getCompetition().getSeason();
    var day = ClubTime.dayOf(startsAt);
    if (day.isBefore(season.getStartDate()) || day.isAfter(season.getEndDate())) {
      throw new InvalidFieldException(
          "startsAt",
          "La fecha tiene que estar dentro de la temporada "
              + season.getName()
              + " (del "
              + DAY.format(season.getStartDate())
              + " al "
              + DAY.format(season.getEndDate())
              + ")");
    }
  }

  private <T> T find(Class<T> type, Long id, String field, String what) {
    var entity = entityManager.find(type, id);
    if (entity == null) {
      throw new InvalidFieldException(field, "No existe " + what + " con id " + id);
    }
    return entity;
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
}
