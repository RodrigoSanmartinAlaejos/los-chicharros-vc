package com.loschicharros.api.mail;

/** Un email de la web: texto plano siempre y HTML opcional. */
public record EmailMessage(String to, String subject, String text, String html) {

  public EmailMessage(String to, String subject, String text) {
    this(to, subject, text, null);
  }
}
