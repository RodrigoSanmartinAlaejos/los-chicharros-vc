package com.loschicharros.api.storage;

import static org.assertj.core.api.Assertions.assertThat;

import com.loschicharros.api.support.AuthTokens;
import com.loschicharros.api.support.TempDirImageStorage;
import com.loschicharros.api.support.TestcontainersConfiguration;
import com.loschicharros.api.support.probe.ProbeController;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Una imagen de más de 5 MB da 413 (H14). Necesita el servidor real: MockMvc no aplica los límites
 * de {@code spring.servlet.multipart}. Mismo contexto que ForwardedHeadersIT, para no arrancar
 * otro.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import({
  TestcontainersConfiguration.class,
  ProbeController.class,
  TempDirImageStorage.class,
  AuthTokens.class
})
class ImageSizeLimitIT {

  @LocalServerPort private int port;
  @Autowired private AuthTokens authTokens;

  @Test
  void shouldRejectImagesOfMoreThanFiveMegabytes() throws Exception {
    var boundary = "limite-de-la-prueba";
    var body = new ByteArrayOutputStream();
    body.writeBytes(
        ("--"
                + boundary
                + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"g.png\""
                + "\r\nContent-Type: image/png\r\n\r\n")
            .getBytes(StandardCharsets.US_ASCII));
    body.writeBytes(ImageUploadTest.PNG);
    body.writeBytes(new byte[5 * 1024 * 1024 + 512 * 1024]);
    body.writeBytes(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.US_ASCII));
    var request =
        HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1/news/1/image"))
            .header("Authorization", authTokens.admin())
            .header("Content-Type", "multipart/form-data; boundary=" + boundary)
            .PUT(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
            .build();

    var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(413);
    assertThat(response.body()).contains("La imagen pesa más de 5 MB");
  }
}
