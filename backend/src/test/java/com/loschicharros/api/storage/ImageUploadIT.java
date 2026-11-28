package com.loschicharros.api.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.loschicharros.api.support.AbstractIntegrationTest;
import com.loschicharros.api.support.TempDirImageStorage;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.AbstractMockHttpServletRequestBuilder;

/** Imagen de una noticia, foto de una persona y escudo de un equipo (issue #58, H14). */
class ImageUploadIT extends AbstractIntegrationTest {

  @Autowired private TempDirImageStorage storage;
  @Autowired private JdbcTemplate jdbc;

  @Test
  void shouldUploadNewsImageAndDeleteThePreviousOne() throws Exception {
    var news =
        create(
            "/api/v1/news",
            "{\"title\":\"Campus\",\"summary\":\"R\",\"content\":\"T\",\"type\":\"EVENT\"}");

    var first = url(upload("/api/v1/news/" + news + "/image", png("foto.png")));
    assertThat(storage.pathOf(first)).exists().hasBinaryContent(ImageUploadTest.PNG);

    var second =
        url(
            upload("/api/v1/news/" + news + "/image", file("otra.jpg", ImageUploadTest.JPEG))
                .andExpect(jsonPath("$.imageUrl", startsWith("/test-images/news/")))
                .andExpect(jsonPath("$.contentMarkdown").value("T")));
    assertThat(second).endsWith("-noticia-" + news + ".jpg");
    assertThat(storage.pathOf(second)).exists();
    assertThat(storage.pathOf(first)).doesNotExist();
    mockMvc
        .perform(admin(get("/api/v1/news/" + news)))
        .andExpect(jsonPath("$.imageUrl").value(second));
  }

  @Test
  void shouldRejectFilesThatAreNotImages() throws Exception {
    var news =
        create(
            "/api/v1/news",
            "{\"title\":\"Campus\",\"summary\":\"R\",\"content\":\"T\",\"type\":\"EVENT\"}");
    var gif = file("foto.png", "GIF89a".getBytes(StandardCharsets.US_ASCII));

    upload("/api/v1/news/" + news + "/image", gif)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("file"))
        .andExpect(
            jsonPath("$.errors[0].message").value("Tiene que ser una imagen JPG, PNG o WebP"));
    mockMvc
        .perform(admin(multipart(HttpMethod.PUT, "/api/v1/news/" + news + "/image")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].message").value("Elige una imagen"));
    upload("/api/v1/news/999999/image", png("foto.png")).andExpect(status().isNotFound());
    mockMvc
        .perform(multipart(HttpMethod.PUT, "/api/v1/news/" + news + "/image").file(png("x.png")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldShowPersonPhotoInTheWebOnlyWithImageConsent() throws Exception {
    var season =
        create(
            "/api/v1/seasons",
            "{\"name\":\"2026-27\",\"startDate\":\"2026-09-01\",\"endDate\":\"2027-06-30\",\"active\":true}");
    var category =
        jdbc.queryForObject(
            "SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE'", Long.class);
    var team =
        create(
            "/api/v1/teams",
            "{\"seasonId\":%d,\"categoryId\":%d,\"name\":\"Los Chicharros V.C.\",\"ownClub\":true}"
                .formatted(season, category));
    var person = create("/api/v1/people", "{\"firstName\":\"Lucía\",\"lastName\":\"Marrero\"}");
    create(
        "/api/v1/teams/" + team + "/roster",
        "{\"personId\":" + person + ",\"type\":\"PLAYER\",\"shirtNumber\":7}");

    // Cuando: se sube su foto, el panel la ve, pero la web no sin consentimiento
    var photo =
        url(
            upload("/api/v1/people/" + person + "/photo", png("lucia.png"))
                .andExpect(jsonPath("$.photoUrl", startsWith("/test-images/people/"))),
            "$.photoUrl");
    mockMvc
        .perform(get("/api/v1/teams/" + team))
        .andExpect(jsonPath("$.players[0].person.photoUrl").doesNotExist());

    // Con consentimiento, sí
    mockMvc
        .perform(
            admin(put("/api/v1/people/" + person))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"firstName\":\"Lucía\",\"lastName\":\"Marrero\",\"imageConsent\":true}"))
        .andExpect(status().isOk());
    mockMvc
        .perform(get("/api/v1/teams/" + team))
        .andExpect(jsonPath("$.players[0].person.photoUrl").value(photo));

    // Y el escudo del equipo se borra con el equipo
    mockMvc
        .perform(admin(delete("/api/v1/teams/" + team + "/roster/" + membership(team))))
        .andExpect(status().isNoContent());
    var crest =
        url(
            upload("/api/v1/teams/" + team + "/crest", png("escudo.png"))
                .andExpect(jsonPath("$.name").value("Los Chicharros V.C.")),
            "$.crestUrl");
    assertThat(storage.pathOf(crest)).exists();
    mockMvc.perform(admin(delete("/api/v1/teams/" + team))).andExpect(status().isNoContent());
    assertThat(storage.pathOf(crest)).doesNotExist();
  }

  private long membership(long team) {
    return jdbc.queryForObject(
        "SELECT id FROM team_memberships WHERE team_id = ?", Long.class, team);
  }

  private ResultActions upload(String url, MockMultipartFile file) throws Exception {
    return mockMvc.perform(admin(multipart(HttpMethod.PUT, url).file(file)));
  }

  private static MockMultipartFile png(String name) {
    return file(name, ImageUploadTest.PNG);
  }

  private static MockMultipartFile file(String name, byte[] content) {
    return new MockMultipartFile("file", name, "image/png", content);
  }

  private static String url(ResultActions result) throws Exception {
    return url(result, "$.imageUrl");
  }

  private static String url(ResultActions result, String path) throws Exception {
    var body = result.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    return JsonPath.read(body, path);
  }

  private long create(String url, String json) throws Exception {
    var body =
        mockMvc
            .perform(admin(post(url)).contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return ((Number) JsonPath.read(body, "$.id")).longValue();
  }

  private <B extends AbstractMockHttpServletRequestBuilder<B>> B admin(B request) {
    return request.header(HttpHeaders.AUTHORIZATION, authTokens.admin());
  }
}
