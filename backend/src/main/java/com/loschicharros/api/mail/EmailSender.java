package com.loschicharros.api.mail;

/**
 * Envía los emails de la web (desde la fase 2: activar cuenta, recuperar contraseña). La
 * implementación la elige el perfil: SMTP contra Mailpit en {@code dev}, la API HTTP de Brevo en
 * {@code staging} y {@code prod}, y en memoria en los tests (reglas técnicas, regla 14 de Java).
 */
public interface EmailSender {

  void send(EmailMessage message);
}
