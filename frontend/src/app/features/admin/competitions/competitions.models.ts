// Tipos del contrato OpenAPI (etiqueta «Competiciones»). Las fechas viajan como texto ISO
// (AAAA-MM-DD).
import { TeamSummary } from '../teams/teams.models';

export interface CompetitionRequest {
  seasonId: number;
  categoryId: number;
  name: string;
  federation: string | null;
  /** Puntos que se restan por incomparecencia, de 0 a 10. */
  forfeitPenaltyPoints: number;
}

export interface Competition extends CompetitionRequest {
  id: number;
}

/** Fila de la clasificación tal como se escribe a mano (fase 1). */
export interface StandingRequest {
  teamId: number;
  /** Puede ser negativo por las sanciones. */
  points: number;
  played: number;
  won: number;
  lost: number;
  setsWon: number;
  setsLost: number;
  penaltyPoints: number;
}

export interface Standing extends Omit<StandingRequest, 'teamId'> {
  position: number;
  team: TeamSummary;
}

export interface MatchdayRequest {
  competitionId: number;
  /** De 1 a 99, sin repetirse en la competición. */
  number: number;
  /** Dentro de la temporada de la competición. */
  scheduledDate: string | null;
}

export interface Matchday extends MatchdayRequest {
  id: number;
}
