// Tipos del contrato OpenAPI (etiqueta «Equipos»).
import { Category } from '../configuration/configuration.models';

export interface TeamRequest {
  seasonId: number;
  categoryId: number;
  name: string;
  /** Del club o rival. Los rivales no salen en la página de equipos del club. */
  ownClub: boolean;
}

/** Equipo sin plantilla, como llega en los listados. */
export interface TeamSummary {
  id: number;
  name: string;
  ownClub: boolean;
  crestUrl: string | null;
  category: Category;
  seasonId: number;
}

export type MembershipType = 'PLAYER' | 'COACH';

/** Persona con sus datos públicos: nombre e inicial del apellido. */
export interface PublicPerson {
  id: number;
  displayName: string;
  photoUrl: string | null;
}

export interface Membership {
  id: number;
  person: PublicPerson;
  type: MembershipType;
  shirtNumber: number | null;
  position: string | null;
}

export interface Team extends TeamSummary {
  players: Membership[];
  coaches: Membership[];
}

export interface MembershipRequest {
  personId: number;
  type: MembershipType;
  shirtNumber: number | null;
  position: string | null;
}

export interface MembershipPatchRequest {
  shirtNumber: number | null;
  position: string | null;
}

/** Posiciones de voleibol (diccionario de datos); en alevín (minivoley) no se usan. */
export const POSITIONS: readonly string[] = [
  'colocador',
  'opuesto',
  'central',
  'receptor',
  'líbero',
];
