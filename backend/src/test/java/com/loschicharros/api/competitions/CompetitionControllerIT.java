package com.loschicharros.api.competitions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.loschicharros.api.support.AbstractIntegrationTest;
import java.time.LocalDate;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/** Competiciones y jornadas (issue #62, H18). */
class CompetitionControllerIT extends AbstractIntegrationTest {

  private static final String COMPETITIONS = "/api/v1/competitions";
  private static final String MATCHDAYS = "/api/v1/matchdays";

  @Autowired private JdbcTemplate jdbc;

  private long season;
  private long cadeteFemenina;
  private long juvenilMasculina;

  @BeforeEach
  void createSeasonAndFindCategories() throws Exception {
    season = season("2026-27", "2026-09-01", "2027-06-30", true);
    cadeteFemenina = category("Cadete", "FEMALE");
    juvenilMasculina = category("Juvenil", "MALE");
  }

  @Test
  void shouldShowNewCompetitionInActiveSeasonWithoutSession() throws Exception {
    // Dado: una competición con sus puntos de sanción y otra sin ellos (valen 1)
    mockMvc
        .perform(
            admin(post(COMPETITIONS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    competition(season, juvenilMasculina, "Liga Insular Juvenil masculina")
                        .replace(
                            "}",
                            ",\"federation\":\"Federación Isleña\",\"forfeitPenaltyPoints\":2}")))
        .andExpect(status().isCreated())
        .andExpect(
            header().string("Location", Matchers.matchesPattern(".*/api/v1/competitions/\\d+")))
        .andExpect(jsonPath("$.forfeitPenaltyPoints").value(2))
        .andExpect(jsonPath("$.federation").value("Federación Isleña"));
    var cadete = createCompetition(cadeteFemenina, "Liga Insular Cadete femenina");
    mockMvc
        .perform(get(COMPETITIONS + "/" + cadete))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.forfeitPenaltyPoints").value(1))
        .andExpect(jsonPath("$.federation").isEmpty());

    // Cuando / Entonces: sin sesión y sin temporada, la de la temporada activa, por categoría
    mockMvc
        .perform(get(COMPETITIONS))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath(
                "$[*].name",
                contains("Liga Insular Cadete femenina", "Liga Insular Juvenil masculina")));
    mockMvc
        .perform(get(COMPETITIONS).param("categoryId", String.valueOf(cadeteFemenina)))
        .andExpect(jsonPath("$[*].name", contains("Liga Insular Cadete femenina")));
  }

  @Test
  void shouldListNothingWithoutActiveSeasonAndRejectUnknownFilters() throws Exception {
    var past = season("2025-26", "2025-09-01", "2026-06-30", false);
    createCompetition(past, cadeteFemenina, "Liga de la temporada pasada");
    jdbc.update("UPDATE seasons SET active = FALSE");

    mockMvc
        .perform(get(COMPETITIONS))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
    mockMvc
        .perform(get(COMPETITIONS).param("seasonId", String.valueOf(past)))
        .andExpect(jsonPath("$[*].name", contains("Liga de la temporada pasada")));
    mockMvc
        .perform(get(COMPETITIONS).param("seasonId", "999999"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("seasonId"));
    mockMvc
        .perform(get(COMPETITIONS).param("categoryId", "999999"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("categoryId"));
  }

  @Test
  void shouldRejectDuplicatedOrInvalidCompetition() throws Exception {
    createCompetition(cadeteFemenina, "Liga Insular");

    mockMvc
        .perform(
            admin(post(COMPETITIONS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(competition(season, cadeteFemenina, " Liga Insular ")))
        .andExpect(status().isConflict())
        .andExpect(
            jsonPath("$.detail")
                .value("Ya existe la competición Liga Insular en esa temporada y categoría"));
    // El mismo nombre en otra categoría sí vale
    createCompetition(juvenilMasculina, "Liga Insular");
    mockMvc
        .perform(
            admin(post(COMPETITIONS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    competition(season, cadeteFemenina, "Copa")
                        .replace("}", ",\"forfeitPenaltyPoints\":11}")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("forfeitPenaltyPoints"));
    mockMvc
        .perform(
            admin(post(COMPETITIONS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(competition(999999, cadeteFemenina, "Copa")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("seasonId"));
  }

  @Test
  void shouldAddMatchdaysOneToTenAndListThemInOrder() throws Exception {
    // Dado
    var liga = createCompetition(cadeteFemenina, "Liga Insular Cadete femenina");

    // Cuando: las jornadas 1 a 10, de dos en dos semanas y en desorden
    for (var number : new int[] {2, 1, 3, 4, 5, 6, 7, 8, 9, 10}) {
      mockMvc
          .perform(
              admin(post(MATCHDAYS))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      matchday(
                          liga,
                          number,
                          LocalDate.of(2026, 10, 3).plusWeeks(2L * (number - 1)).toString())))
          .andExpect(status().isCreated())
          .andExpect(
              header().string("Location", Matchers.matchesPattern(".*/api/v1/matchdays/\\d+")))
          .andExpect(jsonPath("$.competitionId").value(liga))
          .andExpect(jsonPath("$.number").value(number));
    }

    // Entonces: sin sesión se ven por número, listas para asignarles partidos
    mockMvc
        .perform(get(COMPETITIONS + "/" + liga + "/matchdays"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(10)))
        .andExpect(jsonPath("$[0].number").value(1))
        .andExpect(jsonPath("$[0].scheduledDate").value("2026-10-03"))
        .andExpect(jsonPath("$[9].number").value(10))
        .andExpect(jsonPath("$[9].scheduledDate").value("2027-02-06"));
    mockMvc.perform(get(COMPETITIONS + "/999999/matchdays")).andExpect(status().isNotFound());
  }

  @Test
  void shouldRejectRepeatedNumberAndDateOutsideSeason() throws Exception {
    var liga = createCompetition(cadeteFemenina, "Liga Insular");
    var first = createMatchday(liga, 1, "2026-10-03");

    mockMvc
        .perform(
            admin(post(MATCHDAYS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(matchday(liga, 1, null)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.detail").value("Ya existe la jornada 1 en esta competición"));
    mockMvc
        .perform(
            admin(post(MATCHDAYS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(matchday(liga, 2, "2027-07-01")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("scheduledDate"))
        .andExpect(
            jsonPath("$.errors[0].message")
                .value(
                    "La fecha tiene que estar dentro de la temporada 2026-27 (del 01/09/2026 al"
                        + " 30/06/2027)"));
    mockMvc
        .perform(
            admin(post(MATCHDAYS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(matchday(liga, 0, null)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("number"));
    mockMvc
        .perform(
            admin(post(MATCHDAYS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(matchday(999999, 2, null)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("competitionId"));

    // Al editar, su propio número no choca; el de otra jornada sí
    var second = createMatchday(liga, 2, null);
    mockMvc
        .perform(
            admin(put(MATCHDAYS + "/" + first))
                .contentType(MediaType.APPLICATION_JSON)
                .content(matchday(liga, 1, "2026-10-10")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.scheduledDate").value("2026-10-10"));
    mockMvc
        .perform(
            admin(put(MATCHDAYS + "/" + second))
                .contentType(MediaType.APPLICATION_JSON)
                .content(matchday(liga, 1, null)))
        .andExpect(status().isConflict());
  }

  @Test
  void shouldDeleteCompetitionWithItsMatchdaysAndKeepSeasonInUse() throws Exception {
    var liga = createCompetition(cadeteFemenina, "Liga Insular");
    var first = createMatchday(liga, 1, null);
    createMatchday(liga, 2, null);
    mockMvc.perform(admin(delete(MATCHDAYS + "/" + first))).andExpect(status().isNoContent());
    mockMvc
        .perform(get(COMPETITIONS + "/" + liga + "/matchdays"))
        .andExpect(jsonPath("$[*].number", contains(2)));

    // La temporada y la categoría no se pueden borrar mientras tengan competiciones
    mockMvc.perform(admin(delete("/api/v1/seasons/" + season))).andExpect(status().isConflict());
    mockMvc
        .perform(admin(delete("/api/v1/categories/" + cadeteFemenina)))
        .andExpect(status().isConflict());

    mockMvc.perform(admin(delete(COMPETITIONS + "/" + liga))).andExpect(status().isNoContent());
    mockMvc.perform(get(COMPETITIONS + "/" + liga)).andExpect(status().isNotFound());
    assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM matchdays", Integer.class)).isZero();
    mockMvc.perform(admin(delete(MATCHDAYS + "/" + first))).andExpect(status().isNotFound());
  }

  @Test
  void shouldEditCompetition() throws Exception {
    var liga = createCompetition(cadeteFemenina, "Liga Insular");
    createCompetition(cadeteFemenina, "Copa");

    mockMvc
        .perform(
            admin(put(COMPETITIONS + "/" + liga))
                .contentType(MediaType.APPLICATION_JSON)
                .content(competition(season, juvenilMasculina, "Liga Insular Juvenil")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.categoryId").value(juvenilMasculina))
        .andExpect(jsonPath("$.name").value("Liga Insular Juvenil"));
    mockMvc
        .perform(
            admin(put(COMPETITIONS + "/" + liga))
                .contentType(MediaType.APPLICATION_JSON)
                .content(competition(season, cadeteFemenina, "Copa")))
        .andExpect(status().isConflict());
    mockMvc
        .perform(
            admin(put(COMPETITIONS + "/999999"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(competition(season, cadeteFemenina, "Otra")))
        .andExpect(status().isNotFound());
    // Sin sesión no se puede escribir
    mockMvc
        .perform(
            put(COMPETITIONS + "/" + liga)
                .contentType(MediaType.APPLICATION_JSON)
                .content(competition(season, cadeteFemenina, "Otra")))
        .andExpect(status().isUnauthorized());
  }

  private long season(String name, String start, String end, boolean active) throws Exception {
    return id(
        mockMvc.perform(
            admin(post("/api/v1/seasons"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"name\":\""
                        + name
                        + "\",\"startDate\":\""
                        + start
                        + "\",\"endDate\":\""
                        + end
                        + "\",\"active\":"
                        + active
                        + "}")));
  }

  private long category(String name, String gender) {
    return jdbc.queryForObject(
        "SELECT id FROM categories WHERE name = ? AND gender = ?", Long.class, name, gender);
  }

  private long createCompetition(long categoryId, String name) throws Exception {
    return createCompetition(season, categoryId, name);
  }

  private long createCompetition(long seasonId, long categoryId, String name) throws Exception {
    return id(
        mockMvc.perform(
            admin(post(COMPETITIONS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(competition(seasonId, categoryId, name))));
  }

  private long createMatchday(long competitionId, int number, String date) throws Exception {
    return id(
        mockMvc.perform(
            admin(post(MATCHDAYS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(matchday(competitionId, number, date))));
  }

  private static long id(ResultActions result) throws Exception {
    var body =
        result.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
    return ((Number) JsonPath.read(body, "$.id")).longValue();
  }

  private static String competition(long seasonId, long categoryId, String name) {
    return "{\"seasonId\":"
        + seasonId
        + ",\"categoryId\":"
        + categoryId
        + ",\"name\":\""
        + name
        + "\"}";
  }

  private static String matchday(long competitionId, int number, String date) {
    return "{\"competitionId\":"
        + competitionId
        + ",\"number\":"
        + number
        + (date == null ? "" : ",\"scheduledDate\":\"" + date + "\"")
        + "}";
  }

  /** Las escrituras son solo del administrador: cada una lleva su token. */
  private MockHttpServletRequestBuilder admin(MockHttpServletRequestBuilder request) {
    return request.header(HttpHeaders.AUTHORIZATION, authTokens.admin());
  }
}
