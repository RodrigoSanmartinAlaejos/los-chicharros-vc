package com.loschicharros.api.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Cambia la imagen de una noticia, una persona o un equipo sin dejar ficheros sueltos: la anterior
 * se borra cuando la transacción se confirma, y la nueva, si se deshace. Si borrar falla, la
 * petición no falla: solo queda un aviso en el log.
 */
@Component
public class StoredImages {

  private static final Logger log = LoggerFactory.getLogger(StoredImages.class);

  private final ImageStorage storage;

  public StoredImages(ImageStorage storage) {
    this.storage = storage;
  }

  /**
   * Guarda la imagen nueva y devuelve su URL. Hay que llamarlo dentro de una transacción.
   *
   * @param folder carpeta lógica: {@code news}, {@code people} o {@code teams}
   * @param name nombre que ayuda a reconocer el fichero, por ejemplo {@code noticia-7}
   * @param previousUrl la imagen que sustituye, o {@code null}
   */
  public String replace(String folder, String name, ImageUpload upload, String previousUrl) {
    var url =
        storage.store(
            folder, name + "." + upload.extension(), upload.contentType(), upload.content());
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            if (status == STATUS_COMMITTED && previousUrl != null) {
              deleteQuietly(previousUrl);
            } else if (status == STATUS_ROLLED_BACK) {
              deleteQuietly(url);
            }
          }
        });
    return url;
  }

  /** Borra la imagen cuando se confirme la transacción (por ejemplo, al borrar su equipo). */
  public void deleteAfterCommit(String url) {
    if (url == null) {
      return;
    }
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            deleteQuietly(url);
          }
        });
  }

  private void deleteQuietly(String url) {
    try {
      storage.delete(url);
    } catch (RuntimeException e) {
      log.warn("No se ha podido borrar la imagen {}", url, e);
    }
  }
}
