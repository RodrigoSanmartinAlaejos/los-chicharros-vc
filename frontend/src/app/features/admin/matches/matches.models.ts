// Tipos del contrato OpenAPI (etiqueta «Partidos»). Las fechas con hora viajan en UTC (ISO 8601).
import { Venue } from '../configuration/configuration.models';
import { TeamSummary } from '../teams/teams.models';

export type MatchStatus =
  'SCHEDULED' | 'IN_PROGRESS' | 'FINISHED' | 'POSTPONED' | 'CANCELLED' | 'FORFEITED';

export const STATUS_LABELS: Record<MatchStatus, string> = {
  SCHEDULED: 'Programado',
  IN_PROGRESS: 'En juego',
  FINISHED: 'Finalizado',
  POSTPONED: 'Aplazado',
  CANCELLED: 'Cancelado',
  FORFEITED: 'Incomparecencia',
};

export interface MatchRequest {
  matchdayId: number;
  homeTeamId: number;
  /** Distinto del local. */
  awayTeamId: number;
  /** Solo si se juega en un pabellón del club. */
  venueId: number | null;
  startsAt: string;
}

export interface MatchPatchRequest {
  status?: MatchStatus;
  startsAt?: string;
}

export interface Match {
  id: number;
  matchdayId: number;
  matchdayNumber: number;
  competitionId: number;
  homeTeam: TeamSummary;
  awayTeam: TeamSummary;
  venue: Venue | null;
  startsAt: string;
  status: MatchStatus;
  homeSets: number | null;
  awaySets: number | null;
}

/** Jornada con sus partidos, como la devuelve `GET /competitions/{id}/matchdays`. */
export interface MatchdaySchedule {
  id: number;
  competitionId: number;
  number: number;
  scheduledDate: string | null;
  matches: Match[];
}
