package com.loschicharros.api.venues;

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

/** CRUD de instalaciones (issue #59, H15). */
class VenueControllerIT extends AbstractIntegrationTest {

  private static final String VENUES = "/api/v1/venues";
  private static final String PUERTO =
      "{\"name\":\"Pabellón del Puerto\",\"address\":\"Avenida Marítima, 12, Santa Cruz de"
          + " Tenerife\",\"latitude\":28.4682,\"longitude\":-16.2466}";

  @Autowired private JdbcTemplate jdbc;

  @Test
  void shouldCreateVenueAndListItByName() throws Exception {
    // Dado
    mockMvc
        .perform(
            post(VENUES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"name\":\"Polideportivo La Marina\",\"address\":\"Calle La Marina, 3\"}"))
        .andExpect(status().isCreated());

    // Cuando
    mockMvc
        .perform(post(VENUES).contentType(MediaType.APPLICATION_JSON).content(PUERTO))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", Matchers.matchesPattern(".*/api/v1/venues/\\d+")))
        .andExpect(jsonPath("$.latitude").value(28.4682))
        .andExpect(jsonPath("$.longitude").value(-16.2466));

    // Entonces
    mockMvc
        .perform(get(VENUES))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name").value("Pabellón del Puerto"))
        .andExpect(jsonPath("$[1].name").value("Polideportivo La Marina"))
        .andExpect(jsonPath("$[1].latitude").doesNotExist());
  }

  @Test
  void shouldUpdateVenue() throws Exception {
    var id = create(PUERTO);
    mockMvc
        .perform(
            put(VENUES + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Pabellón del Puerto\",\"address\":\"Avenida Marítima, 14\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.address").value("Avenida Marítima, 14"))
        .andExpect(jsonPath("$.latitude").doesNotExist());
  }

  @Test
  void shouldRejectCoordinatesOutOfRange() throws Exception {
    mockMvc
        .perform(
            post(VENUES)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"X\",\"address\":\"Y\",\"latitude\":91,\"longitude\":-181}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors", hasSize(2)))
        .andExpect(jsonPath("$.errors[0].field").value("latitude"))
        .andExpect(jsonPath("$.errors[1].field").value("longitude"));
  }

  @Test
  void shouldReturnConflictWhenNameExists() throws Exception {
    create(PUERTO);
    mockMvc
        .perform(post(VENUES).contentType(MediaType.APPLICATION_JSON).content(PUERTO))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.detail").value("Ya existe la instalación Pabellón del Puerto"));
  }

  @Test
  void shouldDeleteVenue() throws Exception {
    var id = create(PUERTO);
    mockMvc.perform(delete(VENUES + "/" + id)).andExpect(status().isNoContent());
    mockMvc.perform(get(VENUES)).andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void shouldReturnConflictWhenDeletingVenueInUse() throws Exception {
    var id = create(PUERTO);
    jdbc.update("INSERT INTO dependency_probes (venue_id) VALUES (?)", id);
    mockMvc
        .perform(delete(VENUES + "/" + id))
        .andExpect(status().isConflict())
        .andExpect(
            jsonPath("$.detail")
                .value(
                    "No se puede borrar la instalación Pabellón del Puerto: tiene partidos o"
                        + " entrenamientos"));
  }

  private long create(String json) throws Exception {
    var response =
        mockMvc
            .perform(post(VENUES).contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return ((Number) JsonPath.read(response, "$.id")).longValue();
  }
}
