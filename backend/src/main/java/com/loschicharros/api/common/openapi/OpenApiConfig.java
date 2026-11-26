package com.loschicharros.api.common.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

/**
 * Documentación viva de la API en {@code /swagger-ui}, generada desde el código con springdoc.
 *
 * <p>Sigue el contrato {@code docs/05-api/openapi-fase-1.yaml}: mismas etiquetas, mismos esquemas
 * de seguridad y los mismos errores RFC 7807. Las respuestas de cada operación salen de la tabla
 * «Métodos y códigos» de {@code docs/05-api/README.md}, salvo en las operaciones que las declaran
 * con {@code @ApiResponse} (autenticación). {@code OpenApiIT} comprueba que no se separan.
 */
@Configuration(proxyBeanMethods = false)
class OpenApiConfig {

  static final String BEARER = "bearerAuth";
  static final String REFRESH_COOKIE = "refreshCookie";

  private static final String PROBLEM = "ProblemDetail";
  private static final String PROBLEM_JSON = "application/problem+json";
  private static final String RESPONSES = "#/components/responses/";

  /** Errores reutilizables, con el nombre y el texto del contrato. */
  private static final Map<String, String> ERRORS =
      Map.of(
          "BadRequest", "Datos o filtros no válidos; `errors` detalla cada campo",
          "Unauthorized", "Sin sesión, token caducado o credenciales incorrectas",
          "Forbidden", "Sin permiso para esta acción",
          "NotFound", "No existe",
          "Conflict", "Duplicado o con registros que dependen de él");

  @Bean
  OpenAPI openApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("API de Los Chicharros V.C.")
                .version("0.1.0")
                .description(
                    "API REST del club. Rutas bajo `/api/v1`, JSON en `camelCase`, fechas en ISO"
                        + " 8601 y errores RFC 7807. En la fase 1 solo escribe el administrador:"
                        + " las operaciones con candado exigen su access token. El contrato"
                        + " completo está en `docs/05-api/openapi-fase-1.yaml`.")
                .license(new License().name("MIT").identifier("MIT")))
        .tags(
            List.of(
                tag("Autenticación"),
                tag("Noticias"),
                tag("Equipos"),
                tag("Personas"),
                tag("Competiciones"),
                tag("Partidos"),
                tag("Configuración")))
        .components(components());
  }

  /**
   * Completa cada operación según la convención de la API: código de éxito, errores habituales y,
   * si tiene {@code @PreAuthorize}, el candado del access token con sus 401 y 403.
   */
  @Bean
  OperationCustomizer conventionCustomizer() {
    return (operation, handler) -> {
      if (handler.hasMethodAnnotation(PreAuthorize.class)) {
        operation.addSecurityItem(new SecurityRequirement().addList(BEARER));
      }
      if (!declaresResponses(handler)) {
        operation.setResponses(conventionalResponses(operation, handler));
      }
      return operation;
    };
  }

  /**
   * Ordena las rutas y quita las etiquetas que aún no tienen ninguna (las de historias sin hacer),
   * para que {@code /swagger-ui} no muestre secciones vacías. Las etiquetas conservan el orden del
   * contrato y los campos de cada esquema, el del código.
   */
  @Bean
  OpenApiCustomizer pathsAndTagsTidier() {
    return openApi -> {
      if (openApi.getPaths() == null) {
        return;
      }
      var sorted = new Paths();
      new TreeMap<>(openApi.getPaths()).forEach(sorted::addPathItem);
      openApi.setPaths(sorted);
      var used = new HashSet<String>();
      sorted.values().stream()
          .flatMap(path -> path.readOperations().stream())
          .filter(operation -> operation.getTags() != null)
          .forEach(operation -> used.addAll(operation.getTags()));
      openApi.setTags(openApi.getTags().stream().filter(t -> used.contains(t.getName())).toList());
    };
  }

  private static ApiResponses conventionalResponses(Operation operation, HandlerMethod handler) {
    var method = httpMethod(handler);
    var generated = operation.getResponses().get("200");
    var content = generated == null ? null : generated.getContent();
    var responses = new ApiResponses();
    switch (method) {
      case POST ->
          responses.addApiResponse(
              "201",
              new ApiResponse()
                  .description("Creado")
                  .addHeaderObject(
                      "Location",
                      new Header().description("URL del recurso creado").schema(new StringSchema()))
                  .content(content));
      case DELETE ->
          responses.addApiResponse("204", new ApiResponse().description("Sin contenido"));
      default ->
          responses.addApiResponse("200", new ApiResponse().description("OK").content(content));
    }
    if (operation.getRequestBody() != null) {
      responses.addApiResponse("400", error("BadRequest"));
    }
    if (handler.hasMethodAnnotation(PreAuthorize.class)) {
      responses.addApiResponse("401", error("Unauthorized"));
      responses.addApiResponse("403", error("Forbidden"));
    }
    if (hasIdParameter(operation)) {
      responses.addApiResponse("404", error("NotFound"));
    }
    if (method == RequestMethod.POST
        || method == RequestMethod.PUT
        || method == RequestMethod.DELETE) {
      responses.addApiResponse("409", error("Conflict"));
    }
    return responses;
  }

  private static boolean declaresResponses(HandlerMethod handler) {
    return handler.hasMethodAnnotation(io.swagger.v3.oas.annotations.responses.ApiResponse.class)
        || handler.hasMethodAnnotation(io.swagger.v3.oas.annotations.responses.ApiResponses.class);
  }

  private static RequestMethod httpMethod(HandlerMethod handler) {
    var mapping =
        AnnotatedElementUtils.findMergedAnnotation(handler.getMethod(), RequestMapping.class);
    return mapping == null || mapping.method().length == 0
        ? RequestMethod.GET
        : mapping.method()[0];
  }

  private static boolean hasIdParameter(Operation operation) {
    return operation.getParameters() != null
        && operation.getParameters().stream()
            .anyMatch(p -> "path".equals(p.getIn()) && "id".equals(p.getName()));
  }

  private static ApiResponse error(String name) {
    return new ApiResponse().$ref(RESPONSES + name);
  }

  private static Components components() {
    var components =
        new Components()
            .addSecuritySchemes(
                BEARER,
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Access token de 15 minutos, en la cabecera `Authorization`"))
            .addSecuritySchemes(
                REFRESH_COOKIE,
                new SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.COOKIE)
                    .name("refresh_token")
                    .description("`HttpOnly`, `Secure`, `SameSite=Strict`, `Path=/api/v1/auth`"))
            .addHeaders(
                "RefreshCookie",
                new Header().description("Refresh token nuevo").schema(new StringSchema()))
            .addSchemas(PROBLEM, problemSchema());
    var problem = new Content().addMediaType(PROBLEM_JSON, new MediaType().schema(problemRef()));
    ERRORS.forEach((name, description) ->
        components.addResponses(name, new ApiResponse().description(description).content(problem)));
    return components;
  }

  private static Schema<?> problemSchema() {
    var fieldError =
        new ObjectSchema()
            .addProperty("field", new StringSchema())
            .addProperty("message", new StringSchema())
            .required(List.of("field", "message"));
    return new ObjectSchema()
        .description("Error RFC 7807 (`application/problem+json`).")
        .addProperty("type", new StringSchema().format("uri")._default("about:blank"))
        .addProperty("title", new StringSchema())
        .addProperty("status", new IntegerSchema())
        .addProperty("detail", new StringSchema())
        .addProperty("instance", new StringSchema())
        .addProperty(
            "errors",
            new ArraySchema().items(fieldError).description("Solo en errores de validación"))
        .required(List.of("title", "status"));
  }

  private static Schema<?> problemRef() {
    return new Schema<>().$ref("#/components/schemas/" + PROBLEM);
  }

  private static Tag tag(String name) {
    return new Tag().name(name);
  }
}
