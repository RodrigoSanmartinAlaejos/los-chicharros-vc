package com.loschicharros.api.support;

import com.loschicharros.api.storage.ImageStorage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.boot.test.context.TestComponent;

/** {@link ImageStorage} de los tests: escribe en una carpeta temporal, nunca en Cloudinary. */
@TestComponent
public class TempDirImageStorage implements ImageStorage {

  private static final String URL_PREFIX = "/test-images/";
  private final Path root;

  public TempDirImageStorage() throws IOException {
    this.root = Files.createTempDirectory("chicharros-images");
    root.toFile().deleteOnExit();
  }

  @Override
  public String store(String folder, String filename, String contentType, byte[] content) {
    var relative = folder + "/" + UUID.randomUUID() + "-" + filename;
    try {
      var file = root.resolve(relative);
      Files.createDirectories(file.getParent());
      Files.write(file, content);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return URL_PREFIX + relative;
  }

  @Override
  public void delete(String url) {
    try {
      Files.deleteIfExists(pathOf(url));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /** Fichero guardado para una URL, para que el test compruebe su contenido. */
  public Path pathOf(String url) {
    return root.resolve(url.substring(URL_PREFIX.length()));
  }
}
