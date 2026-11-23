package com.loschicharros.api.common.audit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.loschicharros.api.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

/** Las columnas de auditoría se rellenan solas al crear y al modificar. */
class AuditingIT extends AbstractIntegrationTest {

  private static final String PROBES = "/test/probes";

  @Test
  void shouldFillAuditColumnsWhenEntityIsCreated() throws Exception {
    // Dado
    currentUser.set(7);

    // Cuando
    var result =
        mockMvc.perform(
            post(PROBES)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Sonda\", \"code\": \"A1\"}"));

    // Entonces
    result
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.createdBy").value(7))
        .andExpect(jsonPath("$.updatedBy").value(7))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.updatedAt").isNotEmpty());
  }

  @Test
  void shouldKeepCreatorAndChangeEditorWhenAnotherUserUpdates() throws Exception {
    // Dado
    currentUser.set(7);
    var created =
        mockMvc
            .perform(
                post(PROBES)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\": \"Sonda\", \"code\": \"A1\"}"))
            .andReturn()
            .getResponse()
            .getContentAsString();
    Number id = JsonPath.read(created, "$.id");
    String createdAt = JsonPath.read(created, "$.createdAt");
    currentUser.set(9);

    // Cuando
    var result =
        mockMvc.perform(
            put(PROBES + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Sonda renombrada\", \"code\": \"A1\"}"));

    // Entonces
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Sonda renombrada"))
        .andExpect(jsonPath("$.createdBy").value(7))
        .andExpect(jsonPath("$.createdAt").value(createdAt))
        .andExpect(jsonPath("$.updatedBy").value(9));
  }

  @Test
  void shouldLeaveAuditUsersEmptyWhenThereIsNoUser() throws Exception {
    mockMvc
        .perform(
            post(PROBES)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Sonda\", \"code\": \"A1\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.createdBy").doesNotExist())
        .andExpect(jsonPath("$.updatedBy").doesNotExist());
  }
}
