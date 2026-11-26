package com.loschicharros.api.people;

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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/** Alta, búsqueda, edición y borrado lógico de personas (issue #60, H16). */
class PersonControllerIT extends AbstractIntegrationTest {

  private static final String PEOPLE = "/api/v1/people";

  @Autowired private JdbcTemplate jdbc;

  @Test
  void shouldCreatePersonAndFindItInSearch() throws Exception {
    // Dado / Cuando
    mockMvc
        .perform(
            admin(post(PEOPLE))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"firstName\":\"  Lucía \",\"lastName\":\"Marrero Cabrera\","
                        + "\"birthDate\":\"2011-03-14\",\"imageConsent\":true}"))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", Matchers.matchesPattern(".*/api/v1/people/\\d+")))
        .andExpect(jsonPath("$.firstName").value("Lucía"))
        .andExpect(jsonPath("$.birthDate").value("2011-03-14"))
        .andExpect(jsonPath("$.imageConsent").value(true))
        .andExpect(jsonPath("$.photoUrl").doesNotExist());

    // Entonces
    mockMvc
        .perform(admin(get(PEOPLE)).param("search", "Lucía"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].lastName").value("Marrero Cabrera"));
  }

  @Test
  void shouldFindPeopleByPartOfNameIgnoringCaseAndAccents() throws Exception {
    create("Lucía", "Marrero Cabrera");
    create("Luciano", "Hernández Díaz");
    create("Carmen", "Lucas Pérez");
    create("Iván", "Toledo Reyes");

    mockMvc
        .perform(admin(get(PEOPLE)).param("search", "luc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(3))
        // Por apellidos: Hernández, Lucas, Marrero
        .andExpect(jsonPath("$.content[*].firstName", contains("Luciano", "Carmen", "Lucía")));
    mockMvc
        .perform(admin(get(PEOPLE)).param("search", "LUCIA MARRERO"))
        .andExpect(jsonPath("$.content[*].firstName", contains("Lucía")));
    mockMvc
        .perform(admin(get(PEOPLE)).param("search", "iván toledo"))
        .andExpect(jsonPath("$.content[*].lastName", contains("Toledo Reyes")));
  }

  @Test
  void shouldTreatLikeWildcardsAsPlainText() throws Exception {
    create("Ana", "Pérez");

    mockMvc
        .perform(admin(get(PEOPLE)).param("search", "%"))
        .andExpect(jsonPath("$.totalElements").value(0));
    mockMvc
        .perform(admin(get(PEOPLE)).param("search", "_"))
        .andExpect(jsonPath("$.totalElements").value(0));
  }

  @Test
  void shouldPaginateAndSortPeople() throws Exception {
    for (var i = 1; i <= 3; i++) {
      create("Persona" + i, "Apellido" + (4 - i));
    }

    mockMvc
        .perform(admin(get(PEOPLE)).param("size", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[*].lastName", contains("Apellido1", "Apellido2")))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(2))
        .andExpect(jsonPath("$.totalElements").value(3))
        .andExpect(jsonPath("$.totalPages").value(2));
    mockMvc
        .perform(admin(get(PEOPLE)).param("size", "2").param("page", "1"))
        .andExpect(jsonPath("$.content[*].lastName", contains("Apellido3")));
    mockMvc
        .perform(admin(get(PEOPLE)).param("sort", "firstName,desc"))
        .andExpect(jsonPath("$.content[0].firstName").value("Persona3"));
    mockMvc
        .perform(admin(get(PEOPLE)).param("size", "500"))
        .andExpect(jsonPath("$.size").value(100));
  }

  @Test
  void shouldRejectSortingByUnknownField() throws Exception {
    mockMvc
        .perform(admin(get(PEOPLE)).param("sort", "password"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("sort"))
        .andExpect(jsonPath("$.errors[0].message").value("No se puede ordenar por password"));
  }

  @Test
  void shouldRejectInvalidPerson() throws Exception {
    var tomorrow = LocalDate.now().plusDays(1);
    mockMvc
        .perform(
            admin(post(PEOPLE))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"firstName\":\" \",\"lastName\":\"Pérez\",\"birthDate\":\""
                        + tomorrow
                        + "\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors", hasSize(2)))
        .andExpect(jsonPath("$.errors[0].field").value("birthDate"))
        .andExpect(jsonPath("$.errors[1].field").value("firstName"));
  }

  @Test
  void shouldGetAndUpdatePerson() throws Exception {
    var id = create("Iván", "Toledo Reyes");

    mockMvc
        .perform(
            admin(put(PEOPLE + "/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"Iván\",\"lastName\":\"Toledo Ramos\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lastName").value("Toledo Ramos"))
        .andExpect(jsonPath("$.imageConsent").value(false));
    mockMvc
        .perform(admin(get(PEOPLE + "/" + id)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lastName").value("Toledo Ramos"));
  }

  @Test
  void shouldReturnNotFoundWhenPersonDoesNotExist() throws Exception {
    mockMvc
        .perform(admin(get(PEOPLE + "/999")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.detail").value("No existe persona con id 999"));
  }

  @Test
  void shouldHidePersonButKeepRowWhenDeleted() throws Exception {
    var id = create("Iván", "Toledo Reyes");

    mockMvc.perform(admin(delete(PEOPLE + "/" + id))).andExpect(status().isNoContent());

    mockMvc.perform(admin(get(PEOPLE))).andExpect(jsonPath("$.totalElements").value(0));
    mockMvc.perform(admin(get(PEOPLE + "/" + id))).andExpect(status().isNotFound());
    mockMvc.perform(admin(delete(PEOPLE + "/" + id))).andExpect(status().isNotFound());
    var deletedAt =
        jdbc.queryForObject("SELECT deleted_at FROM people WHERE id = ?", Object.class, id);
    assertThat(deletedAt).isNotNull();
  }

  @Test
  void shouldRequireSessionToReadPeople() throws Exception {
    // Son datos personales, muchos de menores: ni siquiera el listado es público
    mockMvc.perform(get(PEOPLE)).andExpect(status().isUnauthorized());
    mockMvc.perform(get(PEOPLE + "/1")).andExpect(status().isUnauthorized());
  }

  private long create(String firstName, String lastName) throws Exception {
    var response =
        mockMvc
            .perform(
                admin(post(PEOPLE))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"firstName\":\"" + firstName + "\",\"lastName\":\"" + lastName + "\"}"))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return ((Number) JsonPath.read(response, "$.id")).longValue();
  }

  /** Todo es solo del administrador, también leer: cada petición lleva su token. */
  private MockHttpServletRequestBuilder admin(MockHttpServletRequestBuilder request) {
    return request.header(HttpHeaders.AUTHORIZATION, authTokens.admin());
  }
}
