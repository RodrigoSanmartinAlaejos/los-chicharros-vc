package com.loschicharros.api.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.loschicharros.api.mail.EmailMessage;
import com.loschicharros.api.mail.EmailSender;
import com.loschicharros.api.storage.ImageStorage;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** En los tests, las imágenes van a una carpeta temporal y los emails se quedan en memoria. */
class StorageAndMailFakesIT extends AbstractIntegrationTest {

  @Autowired private ImageStorage imageStorage;
  @Autowired private EmailSender sender;

  @Test
  void shouldWriteImageToTempFolderWhenStoringAndRemoveItWhenDeleting() throws Exception {
    // Dado
    var bytes = new byte[] {1, 2, 3};

    // Cuando
    var url = imageStorage.store("news", "portada.png", "image/png", bytes);

    // Entonces
    var file = ((TempDirImageStorage) imageStorage).pathOf(url);
    assertThat(url).startsWith("/test-images/news/").endsWith("-portada.png");
    assertThat(Files.readAllBytes(file)).containsExactly(bytes);

    imageStorage.delete(url);
    assertThat(file).doesNotExist();
  }

  @Test
  void shouldKeepEmailInMemoryWhenSending() {
    // Cuando
    sender.send(new EmailMessage("socio@example.com", "Bienvenida", "Hola"));

    // Entonces
    assertThat(emailSender.sent())
        .singleElement()
        .extracting(EmailMessage::to)
        .isEqualTo("socio@example.com");
  }
}
