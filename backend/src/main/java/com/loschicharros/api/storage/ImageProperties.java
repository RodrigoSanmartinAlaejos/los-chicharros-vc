package com.loschicharros.api.storage;

import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Dónde se guardan las imágenes ({@code app.images.*}).
 *
 * @param storage {@code local} (disco, en {@code dev}), {@code cloudinary} ({@code staging} y
 *     {@code prod}) o {@code test} (carpeta temporal de los tests); sale de {@code IMAGE_STORAGE}
 * @param localDirectory carpeta del disco para {@code local}
 * @param cloudinaryUrl {@code CLOUDINARY_URL}: {@code cloudinary://<api_key>:<api_secret>@<cloud>}
 * @param cloudinaryFolder carpeta de Cloudinary donde van las imágenes de este entorno
 */
@ConfigurationProperties("app.images")
public record ImageProperties(
    String storage, String localDirectory, String cloudinaryUrl, String cloudinaryFolder) {

  static final String LOCAL = "local";
  static final String CLOUDINARY = "cloudinary";
  private static final Set<String> STORAGES = Set.of(LOCAL, CLOUDINARY, "test");

  public ImageProperties {
    if (storage == null || !STORAGES.contains(storage)) {
      throw new IllegalStateException(
          "IMAGE_STORAGE tiene que ser local (disco) o cloudinary, y es «" + storage + "»");
    }
    if (CLOUDINARY.equals(storage) && (cloudinaryUrl == null || cloudinaryUrl.isBlank())) {
      throw new IllegalStateException(
          "Con IMAGE_STORAGE=cloudinary hace falta CLOUDINARY_URL: cópiala de «API environment"
              + " variable» en el panel de Cloudinary");
    }
    if (localDirectory == null || localDirectory.isBlank()) {
      localDirectory = "uploads";
    }
    if (cloudinaryFolder == null || cloudinaryFolder.isBlank()) {
      cloudinaryFolder = "los-chicharros";
    }
  }
}
