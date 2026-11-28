package com.loschicharros.api.storage;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Las tres partes de {@code CLOUDINARY_URL} ({@code cloudinary://<api_key>:<api_secret>@<cloud>}).
 * El secreto nunca sale en un mensaje ni en {@link #toString()}.
 */
record CloudinaryCredentials(String apiKey, String apiSecret, String cloudName) {

  private static final Pattern URL = Pattern.compile("^cloudinary://([^:@/]+):([^@/]+)@([\\w-]+)$");

  static CloudinaryCredentials parse(String url) {
    if (url != null && (url.contains("<") || url.contains(">"))) {
      throw new IllegalStateException(
          "CLOUDINARY_URL todavía tiene el ejemplo de Cloudinary (<your_api_key>…): pon en su lugar"
              + " el API Key y el API Secret de tu clave, sin los signos < >");
    }
    var match = URL.matcher(url == null ? "" : url.strip());
    if (!match.matches()) {
      throw new IllegalStateException(
          "CLOUDINARY_URL no es válida: tiene que ser cloudinary://<api_key>:<api_secret>@<cloud_name>");
    }
    return new CloudinaryCredentials(
        match.group(1), URLDecoder.decode(match.group(2), StandardCharsets.UTF_8), match.group(3));
  }

  @Override
  public String toString() {
    return "CloudinaryCredentials[apiKey=" + apiKey + ", cloudName=" + cloudName + "]";
  }
}
