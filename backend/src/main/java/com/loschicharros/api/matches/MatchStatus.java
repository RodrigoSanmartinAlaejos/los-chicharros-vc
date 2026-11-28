package com.loschicharros.api.matches;

/** Estado de un partido (diccionario de datos, {@code ck_matches_status}). */
public enum MatchStatus {
  SCHEDULED,
  IN_PROGRESS,
  FINISHED,
  POSTPONED,
  CANCELLED,
  /** Incomparecencia: se usa desde la fase 3. */
  FORFEITED
}
