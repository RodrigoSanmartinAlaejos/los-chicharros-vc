package com.loschicharros.api.common;

import static org.assertj.core.api.Assertions.assertThat;

import com.loschicharros.api.support.AuthTokens;
import com.loschicharros.api.support.TempDirImageStorage;
import com.loschicharros.api.support.TestcontainersConfiguration;
import com.loschicharros.api.support.probe.ProbeController;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * La API toma la IP del cliente de {@code X-Forwarded-For} cuando la petición llega del proxy del
 * SSR (ADR-004). Necesita el servidor real (Tomcat), no MockMvc, porque lo resuelve Tomcat.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import({
  TestcontainersConfiguration.class,
  ProbeController.class,
  TempDirImageStorage.class,
  AuthTokens.class
})
class ForwardedHeadersIT {

  @LocalServerPort private int port;

  @Test
  void shouldUseForwardedClientIpWhenRequestComesFromLocalProxy() throws Exception {
    // Dado: el proxy del SSR, en la misma máquina, reenvía la IP del navegador
    var request =
        HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/test/probes/client-ip"))
            .header("X-Forwarded-For", "203.0.113.7")
            .build();

    // Cuando
    var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    // Entonces
    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(response.body()).isEqualTo("203.0.113.7");
  }
}
