package com.loschicharros.api.storage;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Elige el {@link ImageStorage} según {@code app.images.storage} (reglas técnicas, regla 13 de
 * Java): disco en {@code dev} y Cloudinary en {@code staging} y {@code prod}. En los tests ({@code
 * test}) no se crea ninguno: lo pone {@code TempDirImageStorage}.
 */
@Configuration
@EnableConfigurationProperties(ImageProperties.class)
class ImageStorageConfig {

  static final String CLOUDINARY_API = "https://api.cloudinary.com";
  private static final String PROPERTY = "app.images.storage";

  @Bean
  @ConditionalOnProperty(name = PROPERTY, havingValue = ImageProperties.LOCAL)
  LocalImageStorage localImageStorage(ImageProperties properties) {
    return new LocalImageStorage(Path.of(properties.localDirectory()));
  }

  /**
   * La API sirve las imágenes del disco en {@code /api/images/**}. Cada imagen tiene un nombre
   * único que no se reutiliza, así que el navegador puede guardarla un año.
   */
  @Bean
  @ConditionalOnProperty(name = PROPERTY, havingValue = ImageProperties.LOCAL)
  WebMvcConfigurer localImages(LocalImageStorage storage) {
    return new WebMvcConfigurer() {
      @Override
      public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler(LocalImageStorage.URL_PREFIX + "**")
            .addResourceLocations(storage.root().toUri().toString())
            .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic());
      }
    };
  }

  @Bean
  @ConditionalOnProperty(name = PROPERTY, havingValue = ImageProperties.CLOUDINARY)
  CloudinaryImageStorage cloudinaryImageStorage(ImageProperties properties) {
    return new CloudinaryImageStorage(
        CloudinaryCredentials.parse(properties.cloudinaryUrl()),
        properties.cloudinaryFolder(),
        RestClient.builder().baseUrl(CLOUDINARY_API).build(),
        Clock.systemUTC());
  }
}
