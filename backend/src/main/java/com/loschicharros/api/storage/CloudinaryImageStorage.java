package com.loschicharros.api.storage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

/**
 * Imágenes en Cloudinary, para {@code staging} y {@code prod}: el disco de Render se borra en cada
 * despliegue. Usa la API REST de subida con peticiones firmadas (SHA-1 de los parámetros ordenados
 * más el secreto), sin el SDK. Cada entorno guarda en su carpeta ({@code
 * app.images.cloudinary-folder}).
 */
class CloudinaryImageStorage implements ImageStorage {

  private static final Pattern VERSION = Pattern.compile("^v\\d+/");

  private final CloudinaryCredentials credentials;
  private final String folderPrefix;
  private final RestClient http;
  private final Clock clock;

  CloudinaryImageStorage(
      CloudinaryCredentials credentials, String folderPrefix, RestClient http, Clock clock) {
    this.credentials = credentials;
    this.folderPrefix = folderPrefix.replaceAll("/+$", "");
    this.http = http;
    this.clock = clock;
  }

  @Override
  public String store(String folder, String filename, String contentType, byte[] content) {
    // public_id fija la URL; asset_folder, la carpeta de la Media Library (las cuentas nuevas usan
    // carpetas dinámicas y no la sacan del public_id)
    var assetFolder = folderPrefix + "/" + folder;
    var params =
        signed(
            Map.of(
                "public_id", assetFolder + "/" + UUID.randomUUID(), "asset_folder", assetFolder));
    var body = new LinkedMultiValueMap<String, Object>();
    params.forEach(body::add);
    body.add(
        "file",
        new ByteArrayResource(content) {
          @Override
          public String getFilename() {
            return filename;
          }
        });
    var response =
        http.post()
            .uri("/v1_1/{cloud}/image/upload", credentials.cloudName())
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(body)
            .retrieve()
            .body(UploadResponse.class);
    if (response == null || response.secure_url() == null) {
      throw new IllegalStateException("Cloudinary no ha devuelto la URL de la imagen");
    }
    return response.secure_url();
  }

  /** Solo borra las suyas (las de su nube); {@code invalidate} la quita también de la CDN. */
  @Override
  public void delete(String url) {
    var publicId = publicIdOf(url);
    if (publicId == null) {
      return;
    }
    var body = new LinkedMultiValueMap<String, String>();
    signed(Map.of("public_id", publicId, "invalidate", "true")).forEach(body::add);
    http.post()
        .uri("/v1_1/{cloud}/image/destroy", credentials.cloudName())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(body)
        .retrieve()
        .toBodilessEntity();
  }

  /**
   * El {@code public_id} de una URL de esta nube: {@code
   * https://res.cloudinary.com/<nube>/image/upload/v123/<public_id>.jpg}. {@code null} si no es
   * suya.
   */
  String publicIdOf(String url) {
    var prefix = "https://res.cloudinary.com/" + credentials.cloudName() + "/image/upload/";
    if (url == null || !url.startsWith(prefix)) {
      return null;
    }
    var path = VERSION.matcher(url.substring(prefix.length())).replaceFirst("");
    var dot = path.lastIndexOf('.');
    return dot > 0 ? path.substring(0, dot) : path;
  }

  /** Los parámetros con {@code timestamp}, {@code api_key} y la firma que pide Cloudinary. */
  private Map<String, String> signed(Map<String, String> params) {
    var toSign = new TreeMap<>(params);
    toSign.put("timestamp", String.valueOf(clock.instant().getEpochSecond()));
    var joined =
        toSign.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining("&"));
    var result = new TreeMap<>(toSign);
    result.put("signature", sha1(joined + credentials.apiSecret()));
    result.put("api_key", credentials.apiKey());
    return result;
  }

  private static String sha1(String text) {
    try {
      var digest = MessageDigest.getInstance("SHA-1").digest(text.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(digest);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("La JVM no tiene SHA-1", e);
    }
  }

  /** Lo que se usa de la respuesta de subida. */
  record UploadResponse(String secure_url, String public_id) {}
}
