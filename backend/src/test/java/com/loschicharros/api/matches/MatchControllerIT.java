package com.loschicharros.api.matches;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.loschicharros.api.support.AbstractIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/** Partidos del calendario (issue #63, H19). */
class MatchControllerIT extends AbstractIntegrationTest {

  private static final String MATCHES = "/api/v1/matches";

  @Autowired private JdbcTemplate jdbc;

  private long season;
  private long cadeteFemenina;
  private long liga;
  private long jornada1;
  private long jornada2;
  private long chicharros;
  private long roque;
  private long anaga;
  private long pabellon;

  @BeforeEach
  void createCompetitionTeamsAndVenue() throws Exception {
    season =
        id(
            mockMvc.perform(
                admin(post("/api/v1/seasons"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"name\":\"2026-27\",\"startDate\":\"2026-09-01\","
                            + "\"endDate\":\"2027-06-30\",\"active\":true}")));
    cadeteFemenina =
        jdbc.queryForObject(
            "SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE'", Long.class);
    liga =
        id(
            mockMvc.perform(
                admin(post("/api/v1/competitions"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"seasonId\":"
                            + season
                            + ",\"categoryId\":"
                            + cadeteFemenina
                            + ",\"name\":\"Liga Insular\"}")));
    jornada1 = matchday(1);
    jornada2 = matchday(2);
    chicharros = team(cadeteFemenina, "Los Chicharros V.C.", true);
    roque = team(cadeteFemenina, "Roque Vóley", false);
    anaga = team(cadeteFemenina, "Anaga Vóley", false);
    pabellon =
        id(
            mockMvc.perform(
                admin(post("/api/v1/venues"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"name\":\"Pabellón del Puerto\",\"address\":\"Avenida Marítima, 12\"}")));
  }

  @Test
  void shouldCreateMatchAndShowItInCalendarWithoutSession() throws Exception {
    // Dado / Cuando
    mockMvc
        .perform(
            admin(post(MATCHES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(match(jornada1, chicharros, roque, pabellon, "2026-10-03T09:30:00Z")))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", Matchers.matchesPattern(".*/api/v1/matches/\\d+")))
        .andExpect(jsonPath("$.status").value("SCHEDULED"))
        .andExpect(jsonPath("$.matchdayNumber").value(1))
        .andExpect(jsonPath("$.competitionId").value(liga))
        .andExpect(jsonPath("$.homeTeam.name").value("Los Chicharros V.C."))
        .andExpect(jsonPath("$.awayTeam.category.name").value("Cadete"))
        .andExpect(jsonPath("$.venue.name").value("Pabellón del Puerto"))
        .andExpect(jsonPath("$.homeSets").isEmpty());
    // Un partido fuera de casa, sin pabellón
    create(jornada2, anaga, chicharros, null, "2026-10-17T16:00:00Z");

    // Entonces: aparece en el calendario, por fecha, y en la jornada
    mockMvc
        .perform(get(MATCHES))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.content[0].startsAt").value("2026-10-03T09:30:00Z"))
        .andExpect(jsonPath("$.content[1].venue").isEmpty());
    mockMvc
        .perform(get("/api/v1/competitions/" + liga + "/matchdays"))
        .andExpect(jsonPath("$[0].matches", hasSize(1)))
        .andExpect(jsonPath("$[0].matches[0].awayTeam.name").value("Roque Vóley"))
        .andExpect(jsonPath("$[1].matches[0].homeTeam.name").value("Anaga Vóley"));
  }

  @Test
  void shouldRejectSameTeamAsHomeAndAway() throws Exception {
    mockMvc
        .perform(
            admin(post(MATCHES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(match(jornada1, roque, roque, null, "2026-10-03T09:30:00Z")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("awayTeamId"))
        .andExpect(
            jsonPath("$.errors[0].message")
                .value("El visitante tiene que ser un equipo distinto del local"));
    mockMvc.perform(get(MATCHES)).andExpect(jsonPath("$.totalElements").value(0));
  }

  @Test
  void shouldRejectTeamsFromOtherCategoryDateOutsideSeasonAndTeamTwiceInMatchday()
      throws Exception {
    var juvenil =
        team(
            jdbc.queryForObject(
                "SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE'", Long.class),
            "Juvenil Rival",
            false);
    mockMvc
        .perform(
            admin(post(MATCHES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(match(jornada1, chicharros, juvenil, null, "2026-10-03T09:30:00Z")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("awayTeamId"))
        .andExpect(
            jsonPath("$.errors[0].message")
                .value("Juvenil Rival no es de la categoría y la temporada de Liga Insular"));
    // El 30 de junio a las 23:30 UTC ya es 1 de julio en Canarias (UTC+1 en verano)
    mockMvc
        .perform(
            admin(post(MATCHES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(match(jornada1, chicharros, roque, null, "2027-06-30T23:30:00Z")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("startsAt"));
    mockMvc
        .perform(
            admin(post(MATCHES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(match(999999, chicharros, roque, null, "2026-10-03T09:30:00Z")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("matchdayId"));

    create(jornada1, chicharros, roque, null, "2026-10-03T09:30:00Z");
    mockMvc
        .perform(
            admin(post(MATCHES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(match(jornada1, anaga, roque, null, "2026-10-03T11:30:00Z")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.detail").value("Roque Vóley ya juega otro partido en la jornada 1"));
  }

  @Test
  void shouldPostponeCancelAndRescheduleMatch() throws Exception {
    var match = create(jornada1, chicharros, roque, pabellon, "2026-10-03T09:30:00Z");

    // Aplazado: sale así en el calendario público
    mockMvc
        .perform(
            admin(patch(MATCHES + "/" + match))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"POSTPONED\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("POSTPONED"))
        .andExpect(jsonPath("$.startsAt").value("2026-10-03T09:30:00Z"));
    mockMvc
        .perform(get(MATCHES).param("status", "POSTPONED"))
        .andExpect(jsonPath("$.content[*].id", contains((int) match)));

    // Nueva fecha: vuelve a estar programado
    mockMvc
        .perform(
            admin(patch(MATCHES + "/" + match))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"SCHEDULED\",\"startsAt\":\"2026-10-21T18:00:00Z\"}"))
        .andExpect(jsonPath("$.status").value("SCHEDULED"))
        .andExpect(jsonPath("$.startsAt").value("2026-10-21T18:00:00Z"));
    mockMvc
        .perform(
            admin(patch(MATCHES + "/" + match))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"CANCELLED\"}"))
        .andExpect(jsonPath("$.status").value("CANCELLED"));

    // El resultado no se pone a mano: llega con el marcador (H20)
    mockMvc
        .perform(
            admin(patch(MATCHES + "/" + match))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"FINISHED\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("status"));
    mockMvc
        .perform(
            admin(patch(MATCHES + "/" + match))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"PERDIDO\"}"))
        .andExpect(status().isBadRequest());
    mockMvc
        .perform(
            patch(MATCHES + "/" + match)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"SCHEDULED\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldEditAndDeleteMatchAndProtectWhatItUses() throws Exception {
    var match = create(jornada1, chicharros, roque, null, "2026-10-03T09:30:00Z");

    // Al editar, sus propios equipos no chocan consigo mismo
    mockMvc
        .perform(
            admin(put(MATCHES + "/" + match))
                .contentType(MediaType.APPLICATION_JSON)
                .content(match(jornada1, roque, chicharros, pabellon, "2026-10-04T10:00:00Z")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.homeTeam.name").value("Roque Vóley"))
        .andExpect(jsonPath("$.venue.name").value("Pabellón del Puerto"));

    // Lo que usa el partido no se puede borrar mientras exista
    mockMvc.perform(admin(delete("/api/v1/teams/" + roque))).andExpect(status().isConflict());
    mockMvc.perform(admin(delete("/api/v1/venues/" + pabellon))).andExpect(status().isConflict());
    mockMvc
        .perform(admin(delete("/api/v1/matchdays/" + jornada1)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.detail").value("No se puede borrar la jornada 1: tiene partidos"));
    mockMvc
        .perform(admin(delete("/api/v1/competitions/" + liga)))
        .andExpect(status().isConflict())
        .andExpect(
            jsonPath("$.detail")
                .value("No se puede borrar la competición Liga Insular: tiene partidos"));

    mockMvc.perform(admin(delete(MATCHES + "/" + match))).andExpect(status().isNoContent());
    mockMvc.perform(get(MATCHES + "/" + match)).andExpect(status().isNotFound());
    mockMvc.perform(admin(delete("/api/v1/teams/" + roque))).andExpect(status().isNoContent());
  }

  @Test
  void shouldFilterCalendarByTeamVenueAndClubDays() throws Exception {
    create(jornada1, chicharros, roque, pabellon, "2026-10-03T09:30:00Z");
    create(jornada2, anaga, roque, null, "2026-10-17T16:00:00Z");
    // A las 23:30 UTC del 24 de octubre ya es día 25 en Canarias (UTC+1 hasta el último domingo)
    create(jornada2, chicharros, tegueste(), null, "2026-10-24T23:30:00Z");

    mockMvc
        .perform(get(MATCHES).param("teamId", String.valueOf(chicharros)))
        .andExpect(jsonPath("$.totalElements").value(2));
    mockMvc
        .perform(get(MATCHES).param("venueId", String.valueOf(pabellon)))
        .andExpect(jsonPath("$.totalElements").value(1));
    mockMvc
        .perform(get(MATCHES).param("from", "2026-10-17").param("to", "2026-10-24"))
        .andExpect(jsonPath("$.content[*].startsAt", contains("2026-10-17T16:00:00Z")));
    mockMvc
        .perform(get(MATCHES).param("from", "2026-10-25").param("to", "2026-10-25"))
        .andExpect(jsonPath("$.totalElements").value(1));
    mockMvc
        .perform(get(MATCHES).param("sort", "startsAt,desc").param("size", "1"))
        .andExpect(jsonPath("$.content[0].startsAt").value("2026-10-24T23:30:00Z"))
        .andExpect(jsonPath("$.totalPages").value(3));
    mockMvc
        .perform(get(MATCHES).param("from", "2026-10-25").param("to", "2026-10-01"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("to"));
    mockMvc
        .perform(get(MATCHES).param("sort", "homeSets"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("sort"));
    mockMvc.perform(get(MATCHES).param("from", "03/10/2026")).andExpect(status().isBadRequest());
  }

  private long tegueste() throws Exception {
    return team(cadeteFemenina, "Tegueste Vóley", false);
  }

  private long matchday(int number) throws Exception {
    return id(
        mockMvc.perform(
            admin(post("/api/v1/matchdays"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"competitionId\":" + liga + ",\"number\":" + number + "}")));
  }

  private long team(long categoryId, String name, boolean ownClub) throws Exception {
    return id(
        mockMvc.perform(
            admin(post("/api/v1/teams"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"seasonId\":"
                        + season
                        + ",\"categoryId\":"
                        + categoryId
                        + ",\"name\":\""
                        + name
                        + "\",\"ownClub\":"
                        + ownClub
                        + "}")));
  }

  private long create(long matchdayId, long home, long away, Long venue, String startsAt)
      throws Exception {
    return id(
        mockMvc.perform(
            admin(post(MATCHES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(match(matchdayId, home, away, venue, startsAt))));
  }

  private static long id(ResultActions result) throws Exception {
    var body =
        result.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
    return ((Number) JsonPath.read(body, "$.id")).longValue();
  }

  private static String match(long matchdayId, long home, long away, Long venue, String startsAt) {
    return "{\"matchdayId\":"
        + matchdayId
        + ",\"homeTeamId\":"
        + home
        + ",\"awayTeamId\":"
        + away
        + (venue == null ? "" : ",\"venueId\":" + venue)
        + ",\"startsAt\":\""
        + startsAt
        + "\"}";
  }

  /** Las escrituras son solo del administrador: cada una lleva su token. */
  private MockHttpServletRequestBuilder admin(MockHttpServletRequestBuilder request) {
    return request.header(HttpHeaders.AUTHORIZATION, authTokens.admin());
  }
}
