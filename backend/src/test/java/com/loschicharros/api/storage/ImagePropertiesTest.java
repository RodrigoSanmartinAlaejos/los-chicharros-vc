package com.loschicharros.api.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/** La API no arranca con una configuración de imágenes que no sirve, y dice qué falta. */
class ImagePropertiesTest {

  @Test
  void shouldExplainWhatIsWrongWhenStarting() {
    assertThatThrownBy(() -> new ImageProperties("", null, null, null))
        .hasMessage("IMAGE_STORAGE tiene que ser local (disco) o cloudinary, y es «»");
    assertThatThrownBy(() -> new ImageProperties("cloudinary", null, " ", null))
        .hasMessageStartingWith("Con IMAGE_STORAGE=cloudinary hace falta CLOUDINARY_URL");
  }

  @Test
  void shouldUseDefaultFolders() {
    var properties = new ImageProperties("local", "", null, null);

    assertThat(properties.localDirectory()).isEqualTo("uploads");
    assertThat(properties.cloudinaryFolder()).isEqualTo("los-chicharros");
  }
}
