package com.loschicharros.api.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.loschicharros.api.common.error.InvalidFieldException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

/**
 * El tipo de una imagen sale de sus primeros bytes, no del nombre ni de lo que diga el navegador.
 */
class ImageUploadTest {

  static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n', 0, 0};
  static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0};
  static final byte[] WEBP = "RIFF\0\0\0\0WEBPVP8 ".getBytes(StandardCharsets.US_ASCII);

  @Test
  void shouldRecognizeJpegPngAndWebp() {
    assertThat(ImageUpload.of(file("foto.txt", JPEG)).extension()).isEqualTo("jpg");
    assertThat(ImageUpload.of(file("escudo", PNG)).contentType()).isEqualTo("image/png");
    assertThat(ImageUpload.of(file("x.webp", WEBP)).extension()).isEqualTo("webp");
  }

  @Test
  void shouldRejectOtherFilesEvenWithAnImageName() {
    var gif = "GIF89a".getBytes(StandardCharsets.US_ASCII);

    assertThatThrownBy(() -> ImageUpload.of(file("foto.png", gif)))
        .isInstanceOf(InvalidFieldException.class)
        .hasMessage("Tiene que ser una imagen JPG, PNG o WebP");
    assertThatThrownBy(() -> ImageUpload.of(file("vacia.png", new byte[0])))
        .hasMessage("Elige una imagen");
    assertThatThrownBy(() -> ImageUpload.of(null)).hasMessage("Elige una imagen");
  }

  private static MockMultipartFile file(String name, byte[] content) {
    return new MockMultipartFile("file", name, "image/png", content);
  }
}
