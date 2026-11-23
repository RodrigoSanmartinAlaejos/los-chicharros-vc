package com.loschicharros.api.categories;

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

/** CRUD de categorías (issue #59, H15). Las diez del catálogo las carga la migración V4_1. */
class CategoryControllerIT extends AbstractIntegrationTest {

  private static final String CATEGORIES = "/api/v1/categories";

  @Autowired private JdbcTemplate jdbc;

  @Test
  void shouldListCatalogInDisplayOrder() throws Exception {
    mockMvc
        .perform(get(CATEGORIES))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(10)))
        .andExpect(jsonPath("$[0].name").value("Alevín"))
        .andExpect(jsonPath("$[0].gender").value("MALE"))
        .andExpect(jsonPath("$[0].sortOrder").value(1))
        .andExpect(jsonPath("$[9].name").value("Sénior"))
        .andExpect(jsonPath("$[9].gender").value("FEMALE"));
  }

  @Test
  void shouldCreateCategoryWithLocation() throws Exception {
    mockMvc
        .perform(
            post(CATEGORIES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("Benjamín", "MIXED", 0)))
        .andExpect(status().isCreated())
        .andExpect(
            header().string("Location", Matchers.matchesPattern(".*/api/v1/categories/\\d+")))
        .andExpect(jsonPath("$.name").value("Benjamín"))
        .andExpect(jsonPath("$.gender").value("MIXED"))
        .andExpect(jsonPath("$.sortOrder").value(0));
    mockMvc.perform(get(CATEGORIES)).andExpect(jsonPath("$[0].name").value("Benjamín"));
  }

  @Test
  void shouldUpdateCategory() throws Exception {
    var id = create("Benjamín", "MIXED", 0);
    mockMvc
        .perform(
            put(CATEGORIES + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("Benjamín", "FEMALE", 11)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.gender").value("FEMALE"))
        .andExpect(jsonPath("$.sortOrder").value(11));
  }

  @Test
  void shouldReturnConflictWhenNameAndGenderExist() throws Exception {
    mockMvc
        .perform(
            post(CATEGORIES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("Cadete", "FEMALE", 20)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.detail").value("Ya existe una categoría con ese nombre y sexo"));
  }

  @Test
  void shouldRejectInvalidBody() throws Exception {
    mockMvc
        .perform(
            post(CATEGORIES)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"gender\":\"MALE\",\"sortOrder\":-1}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors", hasSize(2)))
        .andExpect(jsonPath("$.errors[0].field").value("name"))
        .andExpect(jsonPath("$.errors[1].field").value("sortOrder"));
  }

  @Test
  void shouldDeleteCategory() throws Exception {
    var id = create("Benjamín", "MIXED", 0);
    mockMvc.perform(delete(CATEGORIES + "/" + id)).andExpect(status().isNoContent());
    mockMvc.perform(get(CATEGORIES)).andExpect(jsonPath("$", hasSize(10)));
  }

  @Test
  void shouldReturnConflictWhenDeletingCategoryWithTeams() throws Exception {
    // Dado: una fila que apunta a la categoría, como hará un equipo
    var id = create("Benjamín", "MIXED", 0);
    jdbc.update("INSERT INTO dependency_probes (category_id) VALUES (?)", id);

    // Cuando / Entonces
    mockMvc
        .perform(delete(CATEGORIES + "/" + id))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.detail").value("No se puede borrar la categoría: tiene equipos"));
  }

  @Test
  void shouldReturnNotFoundWhenDeletingMissingCategory() throws Exception {
    mockMvc
        .perform(delete(CATEGORIES + "/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("No existe categoría con id 999"));
  }

  private long create(String name, String gender, int sortOrder) throws Exception {
    var response =
        mockMvc
            .perform(
                post(CATEGORIES)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body(name, gender, sortOrder)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return ((Number) JsonPath.read(response, "$.id")).longValue();
  }

  private static String body(String name, String gender, int sortOrder) {
    return "{\"name\":\"%s\",\"gender\":\"%s\",\"sortOrder\":%d}"
        .formatted(name, gender, sortOrder);
  }
}
