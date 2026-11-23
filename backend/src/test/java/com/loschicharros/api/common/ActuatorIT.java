package com.loschicharros.api.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.loschicharros.api.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.json.JsonCompareMode;

/** Actuator solo expone el estado de la API, sin detalles (reglas técnicas, «Configuración»). */
class ActuatorIT extends AbstractIntegrationTest {

  @Test
  void shouldReturnOnlyStatusWhenHealthIsRequested() throws Exception {
    mockMvc
        .perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"status\": \"UP\"}", JsonCompareMode.STRICT));
  }

  @ParameterizedTest(name = "{0}")
  @ValueSource(
      strings = {
        "/actuator",
        "/actuator/info",
        "/actuator/env",
        "/actuator/beans",
        "/actuator/configprops",
        "/actuator/mappings",
        "/actuator/metrics",
        "/actuator/loggers",
        "/actuator/health/db",
        "/actuator/health/liveness"
      })
  void shouldNotExposeEndpointWhenItIsNotHealth(String path) throws Exception {
    mockMvc.perform(get(path)).andExpect(status().isNotFound());
  }

  @Test
  void shouldNotAllowShutdownWhenRequested() throws Exception {
    mockMvc.perform(post("/actuator/shutdown")).andExpect(status().isNotFound());
  }
}
