package com.loschicharros.api.standings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.loschicharros.api.support.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/** Clasificación editada a mano (issue #65, H21). */
class StandingControllerIT extends AbstractIntegrationTest {

  @Autowired private JdbcTemplate jdbc;

  private long season;
  private long cadeteFemenina;
  private long liga;
  private String standings;
  private long chicharros;
  private long roque;
  private long anaga;

  @BeforeEach
  void createCompetitionAndTeams() throws Exception {
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
    standings = "/api/v1/competitions/" + liga + "/standings";
    chicharros = team(cadeteFemenina, "Los Chicharros V.C.");
    roque = team(cadeteFemenina, "Roque Vóley");
    anaga = team(cadeteFemenina, "Anaga Vóley");
  }

  @Test
  void shouldReplaceStandingsAndShowThemSortedWithoutSession() throws Exception {
    // Dado: una competición sin clasificación todavía
    mockMvc.perform(get(standings)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));

    // Cuando: el administrador la escribe en cualquier orden. Roque y Chicharros empatan a
    // puntos y ganados: decide el cociente de sets (14/6 frente a 13/7)
    mockMvc
        .perform(
            admin(put(standings))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "["
                        + row(anaga, 4, 5, 1, 4, 6, 13, 1)
                        + ","
                        + row(chicharros, 12, 5, 4, 1, 13, 7, 0)
                        + ","
                        + row(roque, 12, 5, 4, 1, 14, 6, 0)
                        + "]"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].position").value(1))
        .andExpect(jsonPath("$[0].team.name").value("Roque Vóley"));

    // Entonces: la tabla pública sale ordenada, con su puesto
    mockMvc
        .perform(get(standings))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath(
                "$[*].team.name", contains("Roque Vóley", "Los Chicharros V.C.", "Anaga Vóley")))
        .andExpect(jsonPath("$[*].position", contains(1, 2, 3)))
        .andExpect(jsonPath("$[1].setsWon").value(13))
        .andExpect(jsonPath("$[2].penaltyPoints").value(1));

    // Al sustituirla otra vez, lo que no llega desaparece
    mockMvc
        .perform(
            admin(put(standings))
                .contentType(MediaType.APPLICATION_JSON)
                .content("[" + row(chicharros, 15, 6, 5, 1, 16, 7, 0) + "]"))
        .andExpect(status().isOk());
    mockMvc.perform(get(standings)).andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void shouldAllowNegativePointsButRejectNegativeCounts() throws Exception {
    // Por las sanciones, los puntos pueden quedar por debajo de cero
    mockMvc
        .perform(
            admin(put(standings))
                .contentType(MediaType.APPLICATION_JSON)
                .content("[" + row(anaga, -2, 2, 0, 2, 1, 6, 2) + "]"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].points").value(-2));

    mockMvc
        .perform(
            admin(put(standings))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "["
                        + row(chicharros, 3, 1, 1, 0, 3, 0, 0)
                        + ","
                        + row(roque, 0, 1, 0, 1, -1, 3, 0)
                        + "]"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("[1].setsWon"));
    // No se ha guardado nada: sigue la clasificación anterior
    mockMvc.perform(get(standings)).andExpect(jsonPath("$[*].team.name", contains("Anaga Vóley")));
  }

  @Test
  void shouldRejectInconsistentRowsAndTeamsOutsideCompetition() throws Exception {
    mockMvc
        .perform(
            admin(put(standings))
                .contentType(MediaType.APPLICATION_JSON)
                .content("[" + row(chicharros, 3, 2, 1, 0, 3, 0, 0) + "]"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("[0].played"))
        .andExpect(
            jsonPath("$.errors[0].message")
                .value("Los ganados y los perdidos tienen que sumar los partidos jugados"));
    mockMvc
        .perform(
            admin(put(standings))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "["
                        + row(chicharros, 3, 1, 1, 0, 3, 0, 0)
                        + ","
                        + row(chicharros, 3, 1, 1, 0, 3, 0, 0)
                        + "]"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("[1].teamId"));
    var juvenil =
        team(
            jdbc.queryForObject(
                "SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE'", Long.class),
            "Juvenil Rival");
    mockMvc
        .perform(
            admin(put(standings))
                .contentType(MediaType.APPLICATION_JSON)
                .content("[" + row(juvenil, 0, 0, 0, 0, 0, 0, 0) + "]"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("[0].teamId"))
        .andExpect(
            jsonPath("$.errors[0].message")
                .value("Juvenil Rival no es de la categoría y la temporada de Liga Insular"));
    mockMvc
        .perform(
            admin(put("/api/v1/competitions/999999/standings"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))
        .andExpect(status().isNotFound());
    mockMvc
        .perform(put(standings).contentType(MediaType.APPLICATION_JSON).content("[]"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldKeepTeamsInStandingsAndDeleteThemWithCompetition() throws Exception {
    mockMvc
        .perform(
            admin(put(standings))
                .contentType(MediaType.APPLICATION_JSON)
                .content("[" + row(roque, 3, 1, 1, 0, 3, 1, 0) + "]"))
        .andExpect(status().isOk());
    mockMvc.perform(admin(delete("/api/v1/teams/" + roque))).andExpect(status().isConflict());

    mockMvc
        .perform(admin(delete("/api/v1/competitions/" + liga)))
        .andExpect(status().isNoContent());
    assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM standings", Integer.class)).isZero();
    mockMvc.perform(admin(delete("/api/v1/teams/" + roque))).andExpect(status().isNoContent());
  }

  private long team(long categoryId, String name) throws Exception {
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
                        + "\",\"ownClub\":false}")));
  }

  private static String row(
      long teamId,
      int points,
      int played,
      int won,
      int lost,
      int setsWon,
      int setsLost,
      int penaltyPoints) {
    return "{\"teamId\":%d,\"points\":%d,\"played\":%d,\"won\":%d,\"lost\":%d,\"setsWon\":%d,\"setsLost\":%d,\"penaltyPoints\":%d}"
        .formatted(teamId, points, played, won, lost, setsWon, setsLost, penaltyPoints);
  }

  private static long id(ResultActions result) throws Exception {
    var body =
        result.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
    return ((Number) JsonPath.read(body, "$.id")).longValue();
  }

  /** Las escrituras son solo del administrador: cada una lleva su token. */
  private MockHttpServletRequestBuilder admin(MockHttpServletRequestBuilder request) {
    return request.header(HttpHeaders.AUTHORIZATION, authTokens.admin());
  }
}
