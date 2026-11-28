package com.loschicharros.api.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

/** Subida y borrado firmados contra la API de Cloudinary, sin salir a internet. */
class CloudinaryImageStorageTest {

  private static final String CLOUD = "demo-chicharros";
  private static final CloudinaryCredentials CREDENTIALS =
      CloudinaryCredentials.parse("cloudinary://123456789:secreto-de-prueba@" + CLOUD);
  private static final Clock CLOCK =
      Clock.fixed(Instant.parse("2026-11-28T18:00:00Z"), ZoneOffset.UTC);

  private MockRestServiceServer server;
  private CloudinaryImageStorage storage;

  @BeforeEach
  void setUp() {
    var builder = RestClient.builder().baseUrl("https://api.cloudinary.com");
    server = MockRestServiceServer.bindTo(builder).build();
    storage =
        new CloudinaryImageStorage(CREDENTIALS, "los-chicharros/dev/", builder.build(), CLOCK);
  }

  @Test
  void shouldUploadSignedImageIntoTheEnvironmentFolder() {
    var url = "https://res.cloudinary.com/" + CLOUD + "/image/upload/v1795888800/x.png";
    server
        .expect(requestTo("https://api.cloudinary.com/v1_1/" + CLOUD + "/image/upload"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(
            content()
                .string(
                    org.hamcrest.Matchers.allOf(
                        org.hamcrest.Matchers.containsString("name=\"api_key\""),
                        org.hamcrest.Matchers.containsString("123456789"),
                        org.hamcrest.Matchers.containsString("los-chicharros/dev/news/"),
                        org.hamcrest.Matchers.containsString("name=\"asset_folder\""),
                        org.hamcrest.Matchers.containsString("1795888800"),
                        org.hamcrest.Matchers.containsString("filename=\"noticia-7.png\""),
                        org.hamcrest.Matchers.not(
                            org.hamcrest.Matchers.containsString("secreto-de-prueba")))))
        .andRespond(
            withSuccess(
                "{\"secure_url\":\"" + url + "\",\"public_id\":\"x\"}",
                MediaType.APPLICATION_JSON));

    var stored =
        storage.store(
            "news", "noticia-7.png", "image/png", "png".getBytes(StandardCharsets.US_ASCII));

    assertThat(stored).isEqualTo(url);
    server.verify();
  }

  @Test
  void shouldDeleteOnlyImagesOfItsOwnCloud() {
    // La firma es el SHA-1 de los parámetros ordenados seguidos del secreto
    server
        .expect(requestTo("https://api.cloudinary.com/v1_1/" + CLOUD + "/image/destroy"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(
            content()
                .formDataContains(
                    java.util.Map.of(
                        "public_id",
                        "los-chicharros/dev/news/abc",
                        "invalidate",
                        "true",
                        "timestamp",
                        "1795888800",
                        "api_key",
                        "123456789",
                        "signature",
                        sha1(
                            "invalidate=true&public_id=los-chicharros/dev/news/abc"
                                + "&timestamp=1795888800secreto-de-prueba"))))
        .andRespond(withSuccess("{\"result\":\"ok\"}", MediaType.APPLICATION_JSON));

    storage.delete(
        "https://res.cloudinary.com/"
            + CLOUD
            + "/image/upload/v17/los-chicharros/dev/news/abc.jpg");
    storage.delete("/assets/imagenes/noticia-evento.svg");
    storage.delete("https://res.cloudinary.com/otra-nube/image/upload/v1/x.jpg");

    server.verify();
  }

  @Test
  void shouldReadPublicIdWithOrWithoutVersion() {
    var prefix = "https://res.cloudinary.com/" + CLOUD + "/image/upload/";
    assertThat(storage.publicIdOf(prefix + "v1795888800/a/b/c.webp")).isEqualTo("a/b/c");
    assertThat(storage.publicIdOf(prefix + "a/b/c.png")).isEqualTo("a/b/c");
    assertThat(storage.publicIdOf(null)).isNull();
  }

  @Test
  void shouldRejectMalformedUrlWithoutShowingTheSecret() {
    assertThatThrownBy(() -> CloudinaryCredentials.parse("https://clave:secreto@nube"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("cloudinary://<api_key>:<api_secret>@<cloud_name>")
        .hasMessageNotContaining("secreto");
    assertThatThrownBy(() ->
            CloudinaryCredentials.parse("cloudinary://<your_api_key>:<your_api_secret>@" + CLOUD))
        .hasMessageStartingWith("CLOUDINARY_URL todavía tiene el ejemplo de Cloudinary");
    assertThat(CREDENTIALS.toString()).doesNotContain("secreto-de-prueba").contains(CLOUD);
  }

  private static String sha1(String text) {
    try {
      return java.util.HexFormat.of()
          .formatHex(
              java.security.MessageDigest.getInstance("SHA-1")
                  .digest(text.getBytes(StandardCharsets.UTF_8)));
    } catch (java.security.NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }
}
