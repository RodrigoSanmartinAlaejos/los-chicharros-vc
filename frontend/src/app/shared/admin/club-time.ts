// Hora del club (Canarias). La API guarda las fechas con hora en UTC; el panel las muestra y las
// pide en la hora de Canarias, esté donde esté quien lo usa.

export const CLUB_ZONE = 'Atlantic/Canary';

const DATE_TIME = new Intl.DateTimeFormat('es-ES', {
  weekday: 'short',
  day: '2-digit',
  month: '2-digit',
  year: 'numeric',
  hour: '2-digit',
  minute: '2-digit',
  timeZone: CLUB_ZONE,
});

const PARTS = new Intl.DateTimeFormat('en-GB', {
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
  hour: '2-digit',
  minute: '2-digit',
  hourCycle: 'h23',
  timeZone: CLUB_ZONE,
});

/** «sáb, 03/10/2026, 09:30» en la hora del club. */
export function formatClubDateTime(iso: string): string {
  return DATE_TIME.format(new Date(iso));
}

/** Valor para un `<input type="datetime-local">`: «2026-10-03T09:30» en la hora del club. */
export function toClubInput(iso: string): string {
  const parts = Object.fromEntries(
    PARTS.formatToParts(new Date(iso)).map((part) => [part.type, part.value]),
  );
  return `${parts['year']}-${parts['month']}-${parts['day']}T${parts['hour']}:${parts['minute']}`;
}

/** Instante en UTC (ISO 8601) de una fecha y hora del club escrita como «2026-10-03T09:30». */
export function fromClubInput(local: string): string {
  const wall = Date.parse(`${local}:00Z`);
  // Se ajusta dos veces por si el primer intento cae al otro lado de un cambio de hora
  let utc = wall - offsetAt(wall);
  utc = wall - offsetAt(utc);
  return new Date(utc).toISOString();
}

/** Diferencia entre la hora del club y UTC en un instante, en milisegundos. */
function offsetAt(instant: number): number {
  return Date.parse(`${toClubInput(new Date(instant).toISOString())}:00Z`) - instant;
}
