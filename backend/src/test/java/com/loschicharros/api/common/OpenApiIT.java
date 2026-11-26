package com.loschicharros.api.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.loschicharros.api.support.AbstractIntegrationTest;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.yaml.snakeyaml.Yaml;

/**
 * La documentación que genera springdoc (T08) coincide con el contrato {@code
 * docs/05-api/openapi-fase-1.yaml} en las rutas que ya existen: cada operación generada está en el
 * contrato con la misma etiqueta, el mismo resumen, la misma seguridad y solo códigos de respuesta
 * que el contrato prevé.
 */
class OpenApiIT extends AbstractIntegrationTest {

  private static final Path CONTRACT = Path.of("../docs/05-api/openapi-fase-1.yaml");
  private static final String PREFIX = "/api/v1";
  private static final Set<String> METHODS = Set.of("get", "post", "put", "patch", "delete");

  @Test
  void shouldMatchContractWhenDocsAreGenerated() throws Exception {
    var generated = generatedDocs();
    var contract = contract();
    var contractPaths = asMap(contract.get("paths"));
    var softly = new SoftAssertions();

    for (var path : asMap(generated.get("paths")).entrySet()) {
      assertThat(path.getKey()).as("ruta generada").startsWith(PREFIX + "/");
      var contractPath = asMap(contractPaths.get(path.getKey().substring(PREFIX.length())));
      softly.assertThat(contractPath).as("%s está en el contrato", path.getKey()).isNotNull();
      if (contractPath == null) {
        continue;
      }
      for (var op : asMap(path.getValue()).entrySet()) {
        if (!METHODS.contains(op.getKey())) {
          continue;
        }
        var name = op.getKey().toUpperCase() + " " + path.getKey();
        var actual = asMap(op.getValue());
        var expected = asMap(contractPath.get(op.getKey()));
        softly.assertThat(expected).as("%s está en el contrato", name).isNotNull();
        if (expected == null) {
          continue;
        }
        softly
            .assertThat(actual.get("tags"))
            .as("%s: etiqueta", name)
            .isEqualTo(expected.get("tags"));
        softly
            .assertThat(actual.get("summary"))
            .as("%s: resumen", name)
            .isEqualTo(expected.get("summary"));
        softly
            .assertThat(securityOf(actual))
            .as("%s: seguridad", name)
            .isEqualTo(securityOf(expected));
        softly
            .assertThat(asMap(expected.get("responses")).keySet())
            .as("%s: códigos de respuesta", name)
            .containsAll(asMap(actual.get("responses")).keySet());
      }
    }
    softly.assertAll();
  }

  @Test
  void shouldDocumentOnlyApiRoutesWhenDocsAreGenerated() throws Exception {
    var paths = asMap(generatedDocs().get("paths")).keySet();

    assertThat(paths)
        .contains(
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/api/v1/auth/logout",
            "/api/v1/seasons",
            "/api/v1/seasons/{id}",
            "/api/v1/categories",
            "/api/v1/categories/{id}",
            "/api/v1/venues",
            "/api/v1/venues/{id}")
        .allMatch(path -> path.startsWith(PREFIX + "/"));
  }

  @Test
  void shouldDescribeSecurityAndErrorsWhenDocsAreGenerated() throws Exception {
    mockMvc
        .perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
        .andExpect(jsonPath("$.components.securitySchemes.refreshCookie.in").value("cookie"))
        .andExpect(jsonPath("$.components.schemas.ProblemDetail.properties.errors").exists())
        .andExpect(
            jsonPath("$.components.responses.Conflict.content['application/problem+json']")
                .exists())
        .andExpect(
            jsonPath("$.paths['/api/v1/seasons'].post.responses['201'].headers.Location").exists());
  }

  @Test
  void shouldServeSwaggerUiWhenRequested() throws Exception {
    mockMvc
        .perform(get("/swagger-ui"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/swagger-ui/index.html"));
    mockMvc
        .perform(get("/swagger-ui/index.html"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
  }

  private Map<String, Object> generatedDocs() throws Exception {
    var json =
        mockMvc
            .perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return JsonPath.read(json, "$");
  }

  private static Map<String, Object> contract() throws IOException {
    try (Reader reader = Files.newBufferedReader(CONTRACT)) {
      return new Yaml().load(reader);
    }
  }

  /** Nombres de los esquemas de seguridad; sin {@code security}, la operación es pública. */
  private static Set<String> securityOf(Map<String, Object> operation) {
    var names = new TreeSet<String>();
    var security = operation.get("security");
    if (security instanceof List<?> requirements) {
      requirements.forEach(requirement -> names.addAll(asMap(requirement).keySet()));
    }
    return names;
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> asMap(Object value) {
    return (Map<String, Object>) value;
  }
}
