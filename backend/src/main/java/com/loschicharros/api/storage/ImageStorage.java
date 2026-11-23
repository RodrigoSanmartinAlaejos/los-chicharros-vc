package com.loschicharros.api.storage;

/**
 * Guarda las imágenes de noticias, equipos y personas. La implementación la elige el perfil: disco
 * local en {@code dev}, Cloudinary en {@code staging} y {@code prod}, y una carpeta temporal en los
 * tests. La base de datos solo guarda la URL que devuelve (reglas técnicas, regla 13 de Java).
 */
public interface ImageStorage {

  /**
   * Guarda una imagen ya validada (tipo y tamaño) y devuelve su URL pública.
   *
   * @param folder carpeta lógica, por ejemplo {@code news} o {@code teams}
   * @param filename nombre sugerido; la implementación puede cambiarlo para que sea único
   * @param contentType tipo MIME: {@code image/jpeg}, {@code image/png} o {@code image/webp}
   * @param content bytes de la imagen
   */
  String store(String folder, String filename, String contentType, byte[] content);

  /** Borra la imagen de esa URL. No falla si ya no existe. */
  void delete(String url);
}
