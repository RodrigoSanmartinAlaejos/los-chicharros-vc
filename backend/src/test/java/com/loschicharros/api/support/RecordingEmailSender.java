package com.loschicharros.api.support;

import com.loschicharros.api.mail.EmailMessage;
import com.loschicharros.api.mail.EmailSender;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.boot.test.context.TestComponent;

/**
 * {@link EmailSender} de los tests: guarda los emails en memoria para que el test los compruebe.
 */
@TestComponent
public class RecordingEmailSender implements EmailSender {

  private final List<EmailMessage> sent = new CopyOnWriteArrayList<>();

  @Override
  public void send(EmailMessage message) {
    sent.add(message);
  }

  public List<EmailMessage> sent() {
    return List.copyOf(sent);
  }

  public void clear() {
    sent.clear();
  }
}
