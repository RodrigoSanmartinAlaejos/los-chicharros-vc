// Tipos del contrato OpenAPI (etiqueta «Configuración»). Las fechas viajan como texto ISO (AAAA-MM-DD).

export interface SeasonRequest {
  name: string;
  startDate: string;
  endDate: string;
  active: boolean;
}

export interface Season extends SeasonRequest {
  id: number;
}

export type Gender = 'MALE' | 'FEMALE' | 'MIXED';

export const GENDER_LABELS: Record<Gender, string> = {
  MALE: 'Masculino',
  FEMALE: 'Femenino',
  MIXED: 'Mixto',
};

/** Sexo como adjetivo de «categoría», para nombrarla: «Cadete femenina». */
export const CATEGORY_GENDER: Record<Gender, string> = {
  MALE: 'masculina',
  FEMALE: 'femenina',
  MIXED: 'mixta',
};

export interface CategoryRequest {
  name: string;
  gender: Gender;
  sortOrder: number;
}

export interface Category extends CategoryRequest {
  id: number;
}

export interface VenueRequest {
  name: string;
  address: string;
  latitude: number | null;
  longitude: number | null;
}

export interface Venue extends VenueRequest {
  id: number;
}
