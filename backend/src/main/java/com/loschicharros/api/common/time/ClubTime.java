package com.loschicharros.api.common.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Hora del club. Las fechas con hora se guardan en UTC, pero los días («partidos del sábado», «del
 * 1 al 7 de noviembre») son los de Canarias, donde juega el club.
 */
public final class ClubTime {

  /** Zona horaria del club (Canarias: UTC en invierno, UTC+1 en verano). */
  public static final ZoneId ZONE = ZoneId.of("Atlantic/Canary");

  private ClubTime() {}

  /** El día del club en que cae un instante. */
  public static LocalDate dayOf(Instant instant) {
    return LocalDate.ofInstant(instant, ZONE);
  }

  /** Primer instante de un día del club. */
  public static Instant startOf(LocalDate day) {
    return day.atStartOfDay(ZONE).toInstant();
  }
}
