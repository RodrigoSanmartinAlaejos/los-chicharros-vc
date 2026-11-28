package com.loschicharros.api.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Imágenes en el disco (dev). */
class LocalImageStorageTest {

  @TempDir Path root;

  @Test
  void shouldStoreServeAndDeleteOnlyItsOwnImages() throws Exception {
    var storage = new LocalImageStorage(root);

    var url = storage.store("teams", "equipo-3.png", "image/png", new byte[] {1, 2, 3});

    assertThat(url).startsWith("/api/images/teams/").endsWith("-equipo-3.png");
    var file = root.resolve(url.substring("/api/images/".length()));
    assertThat(Files.readAllBytes(file)).containsExactly(1, 2, 3);

    storage.delete("/assets/imagenes/equipo.svg");
    storage.delete("https://res.cloudinary.com/nube/image/upload/v1/x.png");
    assertThat(file).exists();

    storage.delete(url);
    assertThat(file).doesNotExist();
    storage.delete(url);
  }

  @Test
  void shouldNeverLeaveItsFolder() {
    var storage = new LocalImageStorage(root);

    assertThatThrownBy(() -> storage.delete("/api/images/../../etc/passwd"))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
