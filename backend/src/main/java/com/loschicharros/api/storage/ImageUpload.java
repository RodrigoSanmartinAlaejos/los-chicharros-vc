package com.loschicharros.api.storage;

import com.loschicharros.api.common.error.InvalidFieldException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.springframework.web.multipart.MultipartFile;

/**
 * Una imagen subida y ya comprobada: JPG, PNG o WebP según sus primeros bytes, no según la
 * extensión ni el tipo que diga el navegador. El tamaño (5 MB) lo limita antes Spring ({@code
 * spring.servlet.multipart.max-file-size}), que responde 413.
 *
 * @param extension {@code jpg}, {@code png} o {@code webp}
 */
public record ImageUpload(byte[] content, String contentType, String extension) {

  /** Nombre del campo del formulario multipart, también en los errores. */
  public static final String FIELD = "file";

  private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
  private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'};
  private static final byte[] RIFF = "RIFF".getBytes(StandardCharsets.US_ASCII);
  private static final byte[] WEBP = "WEBP".getBytes(StandardCharsets.US_ASCII);

  /**
   * Lee y comprueba el fichero.
   *
   * @throws InvalidFieldException si falta o no es una imagen JPG, PNG o WebP
   */
  public static ImageUpload of(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new InvalidFieldException(FIELD, "Elige una imagen");
    }
    byte[] content;
    try {
      content = file.getBytes();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    if (startsWith(content, 0, JPEG)) {
      return new ImageUpload(content, "image/jpeg", "jpg");
    }
    if (startsWith(content, 0, PNG)) {
      return new ImageUpload(content, "image/png", "png");
    }
    if (startsWith(content, 0, RIFF) && startsWith(content, 8, WEBP)) {
      return new ImageUpload(content, "image/webp", "webp");
    }
    throw new InvalidFieldException(FIELD, "Tiene que ser una imagen JPG, PNG o WebP");
  }

  private static boolean startsWith(byte[] content, int offset, byte[] prefix) {
    return content.length >= offset + prefix.length
        && Arrays.equals(content, offset, offset + prefix.length, prefix, 0, prefix.length);
  }
}
