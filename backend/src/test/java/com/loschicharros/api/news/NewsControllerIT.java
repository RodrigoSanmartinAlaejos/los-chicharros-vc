package com.loschicharros.api.news;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.loschicharros.api.support.AbstractIntegrationTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/** Noticias del panel y de la web (issue #56, H12). */
class NewsControllerIT extends AbstractIntegrationTest {

  private static final String NEWS = "/api/v1/news";

  @Test
  void shouldCreateDraftThatOnlyTheAdminSees() throws Exception {
    // Cuando: el administrador guarda una noticia nueva
    var created =
        mockMvc
            .perform(
                admin(post(NEWS))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        request(
                            "  ¡Las cadetes remontan ante el CV Alisios!  ",
                            "MATCH_REPORT",
                            "Ganaron **3-2**.")))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, containsString(NEWS + "/")))
            .andExpect(jsonPath("$.title").value("¡Las cadetes remontan ante el CV Alisios!"))
            .andExpect(jsonPath("$.slug").value("las-cadetes-remontan-ante-el-cv-alisios"))
            .andExpect(jsonPath("$.publishedAt").value(nullValue()))
            .andExpect(jsonPath("$.featured").value(false))
            .andExpect(jsonPath("$.contentMarkdown").value("Ganaron **3-2**."))
            .andExpect(jsonPath("$.contentHtml").value("<p>Ganaron <strong>3-2</strong>.</p>\n"));
    var id = id(created);

    // Entonces: es un borrador; la web no lo ve, el panel sí
    mockMvc
        .perform(get(NEWS))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(0)));
    mockMvc
        .perform(get(NEWS + "/by-slug/las-cadetes-remontan-ante-el-cv-alisios"))
        .andExpect(status().isNotFound());
    mockMvc
        .perform(admin(get(NEWS)).param("status", "draft"))
        .andExpect(jsonPath("$.content[*].id", contains((int) id)));
    mockMvc
        .perform(admin(get(NEWS + "/" + id)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.contentMarkdown").value("Ganaron **3-2**."));
    mockMvc.perform(get(NEWS + "/" + id)).andExpect(status().isUnauthorized());
  }

  @Test
  void shouldPublishWithdrawAndFeature() throws Exception {
    var id = create("Jornada de puertas abiertas", "EVENT");
    var url = NEWS + "/" + id;

    // Cuando: se publica, aparece en la web con su fecha y sin el Markdown
    mockMvc
        .perform(patch(url, "{\"publishedAt\":\"2026-11-14T10:00:00Z\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.publishedAt").value("2026-11-14T10:00:00Z"))
        .andExpect(jsonPath("$.featured").value(false));
    mockMvc
        .perform(get(NEWS))
        .andExpect(jsonPath("$.content[*].slug", contains("jornada-de-puertas-abiertas")))
        .andExpect(jsonPath("$.content[0].publishedAt").value("2026-11-14T10:00:00Z"))
        .andExpect(jsonPath("$.content[0].contentHtml").doesNotExist());
    mockMvc
        .perform(get(NEWS + "/by-slug/jornada-de-puertas-abiertas"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.contentHtml").value("<p>Texto de la noticia.</p>\n"))
        .andExpect(jsonPath("$.contentMarkdown").doesNotExist());

    // Cuando: se destaca, solo cambia eso
    mockMvc
        .perform(patch(url, "{\"featured\":true}"))
        .andExpect(jsonPath("$.featured").value(true))
        .andExpect(jsonPath("$.publishedAt").value("2026-11-14T10:00:00Z"));
    mockMvc
        .perform(get(NEWS).param("featured", "true"))
        .andExpect(jsonPath("$.content", hasSize(1)));

    // Cuando: se retira, vuelve a borrador: la web deja de verla y el panel la sigue viendo
    mockMvc
        .perform(patch(url, "{\"publishedAt\":null}"))
        .andExpect(jsonPath("$.publishedAt").value(nullValue()))
        .andExpect(jsonPath("$.featured").value(true));
    mockMvc.perform(get(NEWS)).andExpect(jsonPath("$.content", hasSize(0)));
    mockMvc
        .perform(get(NEWS + "/by-slug/jornada-de-puertas-abiertas"))
        .andExpect(status().isNotFound());
    mockMvc
        .perform(admin(get(NEWS)).param("status", "all"))
        .andExpect(jsonPath("$.content[*].id", contains((int) id)));
  }

  @Test
  void shouldHideScheduledNewsUntilItsDate() throws Exception {
    var id = create("Campus de Navidad", "EVENT");
    var tomorrow = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);

    mockMvc.perform(patch(NEWS + "/" + id, "{\"publishedAt\":\"" + tomorrow + "\"}"));

    mockMvc.perform(get(NEWS)).andExpect(jsonPath("$.content", hasSize(0)));
    mockMvc.perform(get(NEWS + "/by-slug/campus-de-navidad")).andExpect(status().isNotFound());
    mockMvc
        .perform(admin(get(NEWS)).param("status", "all"))
        .andExpect(jsonPath("$.content[0].publishedAt").value(tomorrow.toString()));
  }

  @Test
  void shouldListPublishedNewsNewestFirstAndFilterByType() throws Exception {
    publish(create("Horarios de entrenamiento", "STATEMENT"), "2026-09-07T09:00:00Z");
    publish(create("Victoria del cadete", "MATCH_REPORT"), "2026-11-02T12:00:00Z");
    publish(create("Victoria del juvenil", "MATCH_REPORT"), "2026-11-16T12:00:00Z");
    create("Asamblea de socios", "STATEMENT");

    mockMvc
        .perform(get(NEWS))
        .andExpect(
            jsonPath(
                "$.content[*].title",
                contains(
                    "Victoria del juvenil", "Victoria del cadete", "Horarios de entrenamiento")))
        .andExpect(jsonPath("$.totalElements").value(3));
    mockMvc
        .perform(get(NEWS).param("type", "MATCH_REPORT").param("size", "1"))
        .andExpect(jsonPath("$.content[*].title", contains("Victoria del juvenil")))
        .andExpect(jsonPath("$.totalPages").value(2));
    mockMvc
        .perform(get(NEWS).param("sort", "title,asc"))
        .andExpect(jsonPath("$.content[0].title").value("Horarios de entrenamiento"));
    mockMvc
        .perform(admin(get(NEWS)).param("status", "all"))
        .andExpect(jsonPath("$.totalElements").value(4));
  }

  @Test
  void shouldRejectInvalidNewsAndFilters() throws Exception {
    mockMvc
        .perform(
            admin(post(NEWS))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\" \",\"summary\":\"Resumen\",\"content\":\"Texto\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[*].field", containsInAnyOrder("title", "type")));
    mockMvc
        .perform(
            admin(post(NEWS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    request("Título", "MATCH_REPORT", "x").replace("Resumen", "r".repeat(301))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("summary"));
    mockMvc
        .perform(get(NEWS).param("status", "pending"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("status"));
    mockMvc
        .perform(get(NEWS).param("sort", "content"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("sort"));
    mockMvc.perform(get(NEWS).param("type", "PODCAST")).andExpect(status().isBadRequest());
  }

  @Test
  void shouldOnlyLetTheAdminSeeDraftsOrWrite() throws Exception {
    var member = authTokens.bearer(authTokens.adminId(), List.of("MEMBER"));

    mockMvc.perform(get(NEWS).param("status", "draft")).andExpect(status().isForbidden());
    mockMvc
        .perform(get(NEWS).param("status", "all").header(HttpHeaders.AUTHORIZATION, member))
        .andExpect(status().isForbidden());
    mockMvc
        .perform(
            post(NEWS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request("Título", "EVENT", "Texto")))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            post(NEWS)
                .header(HttpHeaders.AUTHORIZATION, member)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request("Título", "EVENT", "Texto")))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldKeepSlugsUniqueAndStableOncePublished() throws Exception {
    var first = create("Nuevo horario del pabellón", "STATEMENT");
    var second = create("Nuevo horario del pabellón", "STATEMENT");
    mockMvc
        .perform(admin(get(NEWS + "/" + second)))
        .andExpect(jsonPath("$.slug").value("nuevo-horario-del-pabellon-2"));

    // Un borrador cambia de slug con el título
    update(second, "Horario de Navidad del pabellón")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.slug").value("horario-de-navidad-del-pabellon"));

    // Publicada, el slug se conserva aunque cambie el título
    publish(first, "2026-11-05T09:00:00Z");
    update(first, "Cambia el horario del pabellón")
        .andExpect(jsonPath("$.title").value("Cambia el horario del pabellón"))
        .andExpect(jsonPath("$.slug").value("nuevo-horario-del-pabellon"));

    // El slug de una noticia borrada no se reutiliza
    mockMvc.perform(admin(delete(NEWS + "/" + first))).andExpect(status().isNoContent());
    var third = create("Nuevo horario del pabellón", "STATEMENT");
    mockMvc
        .perform(admin(get(NEWS + "/" + third)))
        .andExpect(jsonPath("$.slug").value("nuevo-horario-del-pabellon-2"));
  }

  @Test
  void shouldSanitizeTheBody() throws Exception {
    var body =
        "# Crónica\\n\\nTexto con [enlace](https://example.com) y <b onclick=\\\"robar()\\\">negrita</b>."
            + "\\n\\n<script>alert('hola')</script>\\n\\n[malo](javascript:alert(1))"
            + "\\n\\n| Equipo | Hora |\\n| --- | ---: |\\n| Cadete | 17:30 |";
    var created =
        mockMvc
            .perform(
                admin(post(NEWS))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request("Prueba de saneado", "EVENT", body)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.contentHtml", containsString("<h2>Crónica</h2>")))
            .andExpect(
                jsonPath(
                    "$.contentHtml",
                    containsString(
                        "<a href=\"https://example.com\" rel=\"nofollow noopener noreferrer\">")))
            .andExpect(jsonPath("$.contentHtml", containsString("<b>negrita</b>")))
            .andExpect(jsonPath("$.contentHtml", containsString("<th align=\"right\">Hora</th>")))
            .andExpect(jsonPath("$.contentHtml", containsString("<td>Cadete</td>")))
            .andExpect(jsonPath("$.contentHtml", not(containsString("script"))))
            .andExpect(jsonPath("$.contentHtml", not(containsString("onclick"))))
            .andExpect(jsonPath("$.contentHtml", not(containsString("javascript"))));
    // El Markdown se guarda tal cual, para seguir editándolo
    created.andExpect(jsonPath("$.contentMarkdown", containsString("<script>")));
  }

  @Test
  void shouldDeleteLogically() throws Exception {
    var id = create("Noticia que sobra", "EVENT");
    publish(id, "2026-11-01T09:00:00Z");

    mockMvc.perform(admin(delete(NEWS + "/" + id))).andExpect(status().isNoContent());

    mockMvc.perform(admin(get(NEWS + "/" + id))).andExpect(status().isNotFound());
    mockMvc.perform(get(NEWS + "/by-slug/noticia-que-sobra")).andExpect(status().isNotFound());
    mockMvc
        .perform(admin(get(NEWS)).param("status", "all"))
        .andExpect(jsonPath("$.content", hasSize(0)));
    mockMvc.perform(admin(delete(NEWS + "/" + id))).andExpect(status().isNotFound());
    mockMvc.perform(patch(NEWS + "/" + id, "{\"featured\":true}")).andExpect(status().isNotFound());
  }

  private long create(String title, String type) throws Exception {
    return id(
        mockMvc.perform(
            admin(post(NEWS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request(title, type, "Texto de la noticia."))));
  }

  private void publish(long id, String when) throws Exception {
    mockMvc
        .perform(patch(NEWS + "/" + id, "{\"publishedAt\":\"" + when + "\"}"))
        .andExpect(status().isOk());
  }

  private MockHttpServletRequestBuilder patch(String url, String body) {
    return admin(
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body));
  }

  private ResultActions update(long id, String title) throws Exception {
    return mockMvc.perform(
        admin(put(NEWS + "/" + id))
            .contentType(MediaType.APPLICATION_JSON)
            .content(request(title, "STATEMENT", "Texto de la noticia.")));
  }

  private static String request(String title, String type, String content) {
    return "{\"title\":\"%s\",\"summary\":\"Resumen\",\"type\":\"%s\",\"content\":\"%s\"}"
        .formatted(title, type, content);
  }

  private static long id(ResultActions result) throws Exception {
    var body =
        result.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
    return ((Number) JsonPath.read(body, "$.id")).longValue();
  }

  /** Cada petición del panel lleva el token del administrador. */
  private MockHttpServletRequestBuilder admin(MockHttpServletRequestBuilder request) {
    return request.header(HttpHeaders.AUTHORIZATION, authTokens.admin());
  }
}
