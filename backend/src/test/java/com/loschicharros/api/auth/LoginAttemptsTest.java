package com.loschicharros.api.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

/** El bloqueo por email + IP dura 15 minutos (issue #55, H11). */
class LoginAttemptsTest {

  private final MutableClock clock = new MutableClock();
  private final LoginAttempts attempts =
      new LoginAttempts(new LoginProtectionProperties(null, null, null), clock);

  @Test
  void shouldBlockForFifteenMinutesAfterFiveFailures() {
    for (var i = 0; i < 4; i++) {
      attempts.recordFailure("a@b.es", "1.1.1.1");
    }
    assertThat(attempts.isBlocked("a@b.es", "1.1.1.1")).isFalse();

    attempts.recordFailure("a@b.es", "1.1.1.1");
    assertThat(attempts.isBlocked("a@b.es", "1.1.1.1")).isTrue();
    assertThat(attempts.isBlocked("a@b.es", "2.2.2.2")).isFalse();

    clock.advance(Duration.ofMinutes(14));
    assertThat(attempts.isBlocked("a@b.es", "1.1.1.1")).isTrue();
    clock.advance(Duration.ofMinutes(1));
    assertThat(attempts.isBlocked("a@b.es", "1.1.1.1")).isFalse();
  }

  @Test
  void shouldForgetOldFailures() {
    for (var i = 0; i < 4; i++) {
      attempts.recordFailure("a@b.es", "1.1.1.1");
    }
    clock.advance(Duration.ofMinutes(16));

    attempts.recordFailure("a@b.es", "1.1.1.1");

    assertThat(attempts.isBlocked("a@b.es", "1.1.1.1")).isFalse();
  }

  /** Reloj que solo avanza cuando el test lo pide. */
  private static final class MutableClock extends Clock {

    private Instant now = Instant.parse("2026-11-26T20:00:00Z");

    void advance(Duration duration) {
      now = now.plus(duration);
    }

    @Override
    public Instant instant() {
      return now;
    }

    @Override
    public ZoneId getZone() {
      return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zone) {
      return this;
    }
  }
}
