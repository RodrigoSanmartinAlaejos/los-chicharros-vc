package com.loschicharros.api.teams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.loschicharros.api.support.AbstractIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/** Equipos y plantillas (issue #61, H17). */
class TeamControllerIT extends AbstractIntegrationTest {

  private static final String TEAMS = "/api/v1/teams";

  @Autowired private JdbcTemplate jdbc;

  private long season;
  private long cadeteFemenina;

  @BeforeEach
  void createSeasonAndFindCategory() throws Exception {
    season = season("2026-27", "2026-09-01", "2027-06-30");
    cadeteFemenina =
        jdbc.queryForObject(
            "SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE'", Long.class);
  }

  @Test
  void shouldShowOwnTeamButNotRivalInClubTeams() throws Exception {
    // Dado
    mockMvc
        .perform(
            admin(post(TEAMS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(team(season, cadeteFemenina, "Los Chicharros V.C.", true)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", Matchers.matchesPattern(".*/api/v1/teams/\\d+")))
        .andExpect(jsonPath("$.category.name").value("Cadete"))
        .andExpect(jsonPath("$.players", hasSize(0)));
    // Un rival: ownClub no llega y vale false
    mockMvc
        .perform(
            admin(post(TEAMS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"seasonId\":"
                        + season
                        + ",\"categoryId\":"
                        + cadeteFemenina
                        + ",\"name\":\"Roque Vóley\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.ownClub").value(false));

    // Cuando / Entonces: sin sesión, la web pide los equipos del club
    mockMvc
        .perform(get(TEAMS).param("seasonId", String.valueOf(season)).param("ownClub", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].name", contains("Los Chicharros V.C.")));
    // Partidos y clasificaciones piden todos: primero los del club
    mockMvc
        .perform(get(TEAMS).param("categoryId", String.valueOf(cadeteFemenina)))
        .andExpect(jsonPath("$[*].name", contains("Los Chicharros V.C.", "Roque Vóley")));
  }

  @Test
  void shouldRejectDuplicatedTeamAndUnknownSeason() throws Exception {
    createTeam("Roque Vóley", false);

    mockMvc
        .perform(
            admin(post(TEAMS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(team(season, cadeteFemenina, "Roque Vóley", false)))
        .andExpect(status().isConflict())
        .andExpect(
            jsonPath("$.detail")
                .value("Ya existe el equipo Roque Vóley en esa temporada y categoría"));
    mockMvc
        .perform(
            admin(post(TEAMS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(team(999, cadeteFemenina, "Otro", false)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("seasonId"));
  }

  @Test
  void shouldUpdateTeam() throws Exception {
    var id = createTeam("Roque Voley", false);

    mockMvc
        .perform(
            admin(put(TEAMS + "/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .content(team(season, cadeteFemenina, "Roque Vóley", false)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Roque Vóley"));
  }

  @Test
  void shouldAddPlayersWithShirtNumberAndCoaches() throws Exception {
    var team = createTeam("Los Chicharros V.C.", true);
    var lucia = person("Lucía", "Marrero Cabrera");
    var tania = person("Tania", "Mesa Ramos");
    var coach = person("Carmen", "Lucas Pérez");

    // Dado un jugador con el dorsal 7
    mockMvc
        .perform(
            admin(post(TEAMS + "/" + team + "/roster"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(player(lucia, 7, "colocador")))
        .andExpect(status().isCreated())
        .andExpect(
            header()
                .string("Location", Matchers.matchesPattern(".*/api/v1/teams/\\d+/roster/\\d+")))
        .andExpect(jsonPath("$.person.displayName").value("Lucía M."));
    addToRoster(team, player(tania, 4, null));
    addToRoster(team, "{\"personId\":" + coach + ",\"type\":\"COACH\"}");

    // Entonces aparece en la plantilla pública con su dorsal
    mockMvc
        .perform(get(TEAMS + "/" + team))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.players[*].shirtNumber", contains(4, 7)))
        .andExpect(jsonPath("$.players[1].person.displayName").value("Lucía M."))
        .andExpect(jsonPath("$.players[1].position").value("colocador"))
        .andExpect(jsonPath("$.players[1].person.lastName").doesNotExist())
        .andExpect(jsonPath("$.coaches[*].person.displayName", contains("Carmen L.")));
  }

  @Test
  void shouldRejectShirtNumberAlreadyTaken() throws Exception {
    var team = createTeam("Los Chicharros V.C.", true);
    addToRoster(team, player(person("Lucía", "Marrero"), 7, null));

    mockMvc
        .perform(
            admin(post(TEAMS + "/" + team + "/roster"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(player(person("Tania", "Mesa"), 7, null)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.detail").value("El dorsal 7 ya lo lleva Lucía M."));
  }

  @Test
  void shouldRejectSamePersonTwiceAndInvalidMembers() throws Exception {
    var team = createTeam("Los Chicharros V.C.", true);
    var lucia = person("Lucía", "Marrero");
    addToRoster(team, player(lucia, 7, null));

    mockMvc
        .perform(
            admin(post(TEAMS + "/" + team + "/roster"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(player(lucia, 8, null)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.detail").value("Lucía M. ya está en la plantilla como jugador"));
    mockMvc
        .perform(
            admin(post(TEAMS + "/" + team + "/roster"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"personId\":"
                        + person("Carmen", "Lucas")
                        + ",\"type\":\"COACH\","
                        + "\"shirtNumber\":1}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("shirtNumber"));
    mockMvc
        .perform(
            admin(post(TEAMS + "/" + team + "/roster"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(player(999, 100, null)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("shirtNumber"));
    mockMvc
        .perform(
            admin(post(TEAMS + "/" + team + "/roster"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(player(999, 5, null)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors[0].field").value("personId"));
  }

  @Test
  void shouldChangeShirtNumberAndRemoveFromRoster() throws Exception {
    var team = createTeam("Los Chicharros V.C.", true);
    addToRoster(team, player(person("Lucía", "Marrero"), 7, null));
    var tania = addToRoster(team, player(person("Tania", "Mesa"), 4, null));
    var roster = TEAMS + "/" + team + "/roster/" + tania;

    mockMvc
        .perform(
            admin(patch(roster))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"shirtNumber\":10,\"position\":\"líbero\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.shirtNumber").value(10))
        .andExpect(jsonPath("$.position").value("líbero"));
    mockMvc
        .perform(
            admin(patch(roster))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"shirtNumber\":7}"))
        .andExpect(status().isConflict());

    mockMvc.perform(admin(delete(roster))).andExpect(status().isNoContent());
    mockMvc.perform(admin(delete(roster))).andExpect(status().isNotFound());
    mockMvc
        .perform(get(TEAMS + "/" + team))
        .andExpect(jsonPath("$.players[*].shirtNumber", contains(7)));
  }

  @Test
  void shouldShowPhotoOnlyWithImageConsent() throws Exception {
    var team = createTeam("Los Chicharros V.C.", true);
    var withConsent = person("Lucía", "Marrero");
    var withoutConsent = person("Tania", "Mesa");
    jdbc.update(
        "UPDATE people SET photo_url = 'https://img/foto.jpg', image_consent = (id = ?)",
        withConsent);
    addToRoster(team, player(withConsent, 7, null));
    addToRoster(team, player(withoutConsent, 8, null));

    mockMvc
        .perform(get(TEAMS + "/" + team))
        .andExpect(jsonPath("$.players[0].person.photoUrl").value("https://img/foto.jpg"))
        .andExpect(jsonPath("$.players[1].person.photoUrl").doesNotExist());
  }

  @Test
  void shouldDeleteTeamWithItsRoster() throws Exception {
    var team = createTeam("Los Chicharros V.C.", true);
    addToRoster(team, player(person("Lucía", "Marrero"), 7, null));

    mockMvc.perform(admin(delete(TEAMS + "/" + team))).andExpect(status().isNoContent());

    mockMvc.perform(get(TEAMS + "/" + team)).andExpect(status().isNotFound());
    var memberships = jdbc.queryForObject("SELECT COUNT(*) FROM team_memberships", Integer.class);
    assertThat(memberships).isZero();
  }

  @Test
  void shouldKeepDeletedPersonOnlyInPastSeasonRosters() throws Exception {
    var past = season("2024-25", "2024-09-01", "2025-06-30");
    var pastTeam = createTeam(past, "Los Chicharros V.C.", true);
    var currentTeam = createTeam("Los Chicharros V.C.", true);
    var lucia = person("Lucía", "Marrero");
    addToRoster(pastTeam, player(lucia, 7, null));
    addToRoster(currentTeam, player(lucia, 7, null));

    mockMvc.perform(admin(delete("/api/v1/people/" + lucia))).andExpect(status().isNoContent());

    // El dorsal 7 queda libre este año y la temporada pasada sigue con su plantilla
    mockMvc.perform(get(TEAMS + "/" + currentTeam)).andExpect(jsonPath("$.players", hasSize(0)));
    mockMvc
        .perform(get(TEAMS + "/" + pastTeam))
        .andExpect(jsonPath("$.players[*].person.displayName", contains("Lucía M.")));
  }

  @Test
  void shouldRequireAdminToWrite() throws Exception {
    mockMvc
        .perform(
            post(TEAMS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(team(season, cadeteFemenina, "X", false)))
        .andExpect(status().isUnauthorized());
    mockMvc
        .perform(post(TEAMS + "/1/roster").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldNotDeleteCategoryOrSeasonWithTeams() throws Exception {
    createTeam("Los Chicharros V.C.", true);

    mockMvc
        .perform(admin(delete("/api/v1/categories/" + cadeteFemenina)))
        .andExpect(status().isConflict());
    mockMvc.perform(admin(delete("/api/v1/seasons/" + season))).andExpect(status().isConflict());
  }

  private long season(String name, String start, String end) throws Exception {
    return id(
        mockMvc.perform(
            admin(post("/api/v1/seasons"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"name\":\""
                        + name
                        + "\",\"startDate\":\""
                        + start
                        + "\",\"endDate\":\""
                        + end
                        + "\"}")));
  }

  private long createTeam(String name, boolean ownClub) throws Exception {
    return createTeam(season, name, ownClub);
  }

  private long createTeam(long seasonId, String name, boolean ownClub) throws Exception {
    return id(
        mockMvc.perform(
            admin(post(TEAMS))
                .contentType(MediaType.APPLICATION_JSON)
                .content(team(seasonId, cadeteFemenina, name, ownClub))));
  }

  private long person(String firstName, String lastName) throws Exception {
    return id(
        mockMvc.perform(
            admin(post("/api/v1/people"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"firstName\":\"" + firstName + "\",\"lastName\":\"" + lastName + "\"}")));
  }

  private long addToRoster(long team, String json) throws Exception {
    return id(
        mockMvc.perform(
            admin(post(TEAMS + "/" + team + "/roster"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)));
  }

  private static long id(ResultActions result) throws Exception {
    var body =
        result.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
    return ((Number) JsonPath.read(body, "$.id")).longValue();
  }

  private static String team(long seasonId, long categoryId, String name, boolean ownClub) {
    return "{\"seasonId\":"
        + seasonId
        + ",\"categoryId\":"
        + categoryId
        + ",\"name\":\""
        + name
        + "\",\"ownClub\":"
        + ownClub
        + "}";
  }

  private static String player(long personId, int shirtNumber, String position) {
    return "{\"personId\":"
        + personId
        + ",\"type\":\"PLAYER\",\"shirtNumber\":"
        + shirtNumber
        + (position == null ? "" : ",\"position\":\"" + position + "\"")
        + "}";
  }

  /** Las escrituras son solo del administrador: cada una lleva su token. */
  private MockHttpServletRequestBuilder admin(MockHttpServletRequestBuilder request) {
    return request.header(HttpHeaders.AUTHORIZATION, authTokens.admin());
  }
}
