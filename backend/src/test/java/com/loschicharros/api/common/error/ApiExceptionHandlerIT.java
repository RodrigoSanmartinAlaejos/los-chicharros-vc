package com.loschicharros.api.common.error;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.loschicharros.api.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

/** Formato RFC 7807 de los errores (docs/05-api/README.md, «Errores»). */
class ApiExceptionHandlerIT extends AbstractIntegrationTest {

  private static final String PROBES = "/test/probes";

  @Test
  void shouldReturnFieldErrorsWhenBodyIsInvalid() throws Exception {
    // Dado
    var body = "{\"name\": \"\", \"code\": \"DEMASIADO\"}";

    // Cuando
    var result =
        mockMvc.perform(post(PROBES).contentType(MediaType.APPLICATION_JSON).content(body));

    // Entonces
    result
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.title").value("Bad Request"))
        .andExpect(jsonPath("$.detail").value("La petición tiene campos no válidos"))
        .andExpect(jsonPath("$.instance").value(PROBES))
        .andExpect(jsonPath("$.errors", hasSize(2)))
        .andExpect(jsonPath("$.errors[0].field").value("code"))
        .andExpect(jsonPath("$.errors[0].message").value("el tamaño debe estar entre 0 y 5"))
        .andExpect(jsonPath("$.errors[1].field").value("name"))
        .andExpect(jsonPath("$.errors[1].message").value("no debe estar vacío"));
  }

  @Test
  void shouldReturnParameterErrorWhenQueryParamIsInvalid() throws Exception {
    mockMvc
        .perform(get(PROBES + "/page").param("size", "500"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("size"))
        .andExpect(jsonPath("$.errors[0].message").value("debe ser menor que o igual a 100"));
  }

  @Test
  void shouldReturnBadRequestWhenJsonIsMalformed() throws Exception {
    mockMvc
        .perform(post(PROBES).contentType(MediaType.APPLICATION_JSON).content("{\"name\": "))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.instance").value(PROBES));
  }

  @Test
  void shouldReturnNotFoundWhenResourceDoesNotExist() throws Exception {
    mockMvc
        .perform(get(PROBES + "/999"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.detail").value("No existe sonda con id 999"))
        .andExpect(jsonPath("$.instance").value(PROBES + "/999"));
  }

  @Test
  void shouldReturnNotFoundWhenRouteDoesNotExist() throws Exception {
    mockMvc
        .perform(get("/api/v1/no-existe"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
  }

  @Test
  void shouldReturnConflictWhenServiceRejectsTheChange() throws Exception {
    mockMvc
        .perform(get(PROBES + "/conflict"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.detail").value("No se puede borrar: tiene equipos"));
  }

  @Test
  void shouldReturnConflictWhenUniqueConstraintFails() throws Exception {
    var body = "{\"name\": \"Sonda\", \"code\": \"AB1\"}";
    mockMvc
        .perform(post(PROBES).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isCreated());

    mockMvc
        .perform(post(PROBES).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.detail").value("La operación choca con datos que ya existen"));
  }

  @Test
  void shouldHideInternalDetailsWhenErrorIsUnexpected() throws Exception {
    mockMvc
        .perform(get(PROBES + "/failure"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.detail").value("Ha ocurrido un error inesperado"))
        .andExpect(content().string(not(containsString("detalle interno"))));
  }
}
