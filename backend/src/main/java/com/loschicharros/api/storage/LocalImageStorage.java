package com.loschicharros.api.storage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Imágenes en el disco, para {@code dev}: se guardan en {@code app.images.local-directory} y la API
 * las sirve en {@code /api/images/**} (ImageStorageConfig), que el proxy del SSR ya reenvía. En
 * Render el disco se borra en cada despliegue: allí se usa Cloudinary.
 */
class LocalImageStorage implements ImageStorage {

  static final String URL_PREFIX = "/api/images/";

  private final Path root;

  LocalImageStorage(Path root) {
    this.root = root.toAbsolutePath().normalize();
  }

  Path root() {
    return root;
  }

  @Override
  public String store(String folder, String filename, String contentType, byte[] content) {
    var relative = folder + "/" + UUID.randomUUID() + "-" + filename;
    var file = resolve(relative);
    try {
      Files.createDirectories(file.getParent());
      Files.write(file, content);
    } catch (IOException e) {
      throw new UncheckedIOException("No se ha podido guardar la imagen " + relative, e);
    }
    return URL_PREFIX + relative;
  }

  /** Solo borra las suyas: las de la semilla ({@code /assets/...}) o de Cloudinary no se tocan. */
  @Override
  public void delete(String url) {
    if (url == null || !url.startsWith(URL_PREFIX)) {
      return;
    }
    try {
      Files.deleteIfExists(resolve(url.substring(URL_PREFIX.length())));
    } catch (IOException e) {
      throw new UncheckedIOException("No se ha podido borrar la imagen " + url, e);
    }
  }

  /** Un fichero dentro de la carpeta; nunca fuera de ella (por ejemplo, con {@code ../}). */
  private Path resolve(String relative) {
    var file = root.resolve(relative).normalize();
    if (!file.startsWith(root)) {
      throw new IllegalArgumentException("Ruta de imagen fuera de la carpeta: " + relative);
    }
    return file;
  }
}
