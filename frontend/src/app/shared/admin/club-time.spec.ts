import { formatClubDateTime, fromClubInput, toClubInput } from './club-time';

describe('club-time', () => {
  it('pasa de UTC a la hora de Canarias en invierno y en verano', () => {
    expect(toClubInput('2026-12-05T10:00:00Z')).toBe('2026-12-05T10:00');
    expect(toClubInput('2026-10-03T09:30:00Z')).toBe('2026-10-03T10:30');
    // A las 23:30 UTC del 24 de octubre ya es día 25 en Canarias
    expect(toClubInput('2026-10-24T23:30:00Z')).toBe('2026-10-25T00:30');
  });

  it('pasa de la hora de Canarias a UTC', () => {
    expect(fromClubInput('2026-12-05T10:00')).toBe('2026-12-05T10:00:00.000Z');
    expect(fromClubInput('2026-10-03T10:30')).toBe('2026-10-03T09:30:00.000Z');
    expect(fromClubInput('2027-03-28T12:00')).toBe('2027-03-28T11:00:00.000Z');
  });

  it('escribe la fecha con el día de la semana y la hora del club', () => {
    expect(formatClubDateTime('2026-10-03T09:30:00Z')).toContain('03/10/2026');
    expect(formatClubDateTime('2026-10-03T09:30:00Z')).toContain('10:30');
  });
});
