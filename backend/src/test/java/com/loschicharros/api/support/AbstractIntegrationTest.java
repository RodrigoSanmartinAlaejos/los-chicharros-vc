package com.loschicharros.api.support;

import com.loschicharros.api.support.probe.ProbeController;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Base de todos los tests de integración (docs/07-desarrollo/guia-tests.md). Todos comparten la
 * misma configuración, así que Spring reutiliza un único contexto y un único MySQL para la suite.
 * No se añade {@code @MockitoBean} en los tests que heredan de aquí: crearía otro contexto.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({
  TestcontainersConfiguration.class,
  DatabaseCleaner.class,
  TestCurrentUser.class,
  TempDirImageStorage.class,
  RecordingEmailSender.class,
  ProbeController.class
})
public abstract class AbstractIntegrationTest {

  @Autowired protected MockMvc mockMvc;
  @Autowired protected TestCurrentUser currentUser;
  @Autowired protected RecordingEmailSender emailSender;
  @Autowired private DatabaseCleaner databaseCleaner;

  @BeforeEach
  void resetState() {
    databaseCleaner.clean();
    currentUser.clear();
    emailSender.clear();
  }
}
