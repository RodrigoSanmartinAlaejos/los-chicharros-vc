package com.loschicharros.api.seasons;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

/** CRUD de temporadas (issue #59, H15). */
class SeasonControllerIT extends AbstractIntegrationTest {

  private static final String SEASONS = "/api/v1/seasons";

  @Autowired private JdbcTemplate jdbc;

  @Test
  void shouldCreateSeasonWithLocation() throws Exception {
    mockMvc
        .perform(
            post(SEASONS).contentType(MediaType.APPLICATION_JSON).content(body("2026-27", true)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", Matchers.matchesPattern(".*/api/v1/seasons/\\d+")))
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.name").value("2026-27"))
        .andExpect(jsonPath("$.startDate").value("2026-09-01"))
        .andExpect(jsonPath("$.endDate").value("2027-06-30"))
        .andExpect(jsonPath("$.active").value(true));
  }

  @Test
  void shouldCreateInactiveSeasonWhenActiveIsMissing() throws Exception {
    var json = "{\"name\":\"2025-26\",\"startDate\":\"2025-09-01\",\"endDate\":\"2026-06-30\"}";
    mockMvc
        .perform(post(SEASONS).contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.active").value(false));
  }

  @Test
  void shouldLeaveOnlyOneActiveSeasonWhenCreatingAnActiveOne() throws Exception {
    // Dado
    create("2025-26", true);

    // Cuando
    create("2026-27", true);

    // Entonces: la lista va de la más reciente a la más antigua
    mockMvc
        .perform(get(SEASONS))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name").value("2026-27"))
        .andExpect(jsonPath("$[0].active").value(true))
        .andExpect(jsonPath("$[1].name").value("2025-26"))
        .andExpect(jsonPath("$[1].active").value(false));
  }

  @Test
  void shouldLeaveOnlyOneActiveSeasonWhenActivatingOnUpdate() throws Exception {
    // Dado
    create("2025-26", true);
    var id = create("2026-27", false);

    // Cuando
    mockMvc
        .perform(
            put(SEASONS + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("2026-27", true)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.active").value(true));

    // Entonces
    var active = jdbc.queryForList("SELECT name FROM seasons WHERE active", String.class);
    assertThat(active).containsExactly("2026-27");
  }

  @Test
  void shouldRejectEndDateBeforeStartDate() throws Exception {
    var json = "{\"name\":\"2026-27\",\"startDate\":\"2027-06-30\",\"endDate\":\"2026-09-01\"}";
    mockMvc
        .perform(post(SEASONS).contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("endDate"))
        .andExpect(
            jsonPath("$.errors[0].message").value("debe ser posterior a la fecha de inicio"));
  }

  @Test
  void shouldRejectInvalidName() throws Exception {
    mockMvc
        .perform(post(SEASONS).contentType(MediaType.APPLICATION_JSON).content(body("2026", false)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("name"))
        .andExpect(jsonPath("$.errors[0].message").value("debe tener el formato 2026-27"));
  }

  @Test
  void shouldReturnConflictWhenNameExists() throws Exception {
    create("2026-27", false);
    mockMvc
        .perform(
            post(SEASONS).contentType(MediaType.APPLICATION_JSON).content(body("2026-27", false)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.detail").value("Ya existe la temporada 2026-27"));
  }

  @Test
  void shouldReturnNotFoundWhenUpdatingMissingSeason() throws Exception {
    mockMvc
        .perform(
            put(SEASONS + "/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("2026-27", false)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("No existe temporada con id 999"));
  }

  @Test
  void shouldDeleteSeason() throws Exception {
    var id = create("2026-27", false);
    mockMvc.perform(delete(SEASONS + "/" + id)).andExpect(status().isNoContent());
    mockMvc.perform(get(SEASONS)).andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void shouldReturnConflictWhenDeletingSeasonInUse() throws Exception {
    // Dado
    var id = create("2026-27", false);
    jdbc.update("INSERT INTO dependency_probes (season_id) VALUES (?)", id);

    // Cuando / Entonces
    mockMvc
        .perform(delete(SEASONS + "/" + id))
        .andExpect(status().isConflict())
        .andExpect(
            jsonPath("$.detail")
                .value("No se puede borrar la temporada 2026-27: tiene equipos o competiciones"));
  }

  private long create(String name, boolean active) throws Exception {
    var response =
        mockMvc
            .perform(
                post(SEASONS).contentType(MediaType.APPLICATION_JSON).content(body(name, active)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return ((Number) JsonPath.read(response, "$.id")).longValue();
  }

  /** Temporada de septiembre a junio a partir del año de inicio del nombre. */
  private static String body(String name, boolean active) {
    var year = Integer.parseInt(name.substring(0, 4));
    return "{\"name\":\"%s\",\"startDate\":\"%d-09-01\",\"endDate\":\"%d-06-30\",\"active\":%s}"
        .formatted(name, year, year + 1, active);
  }
}
