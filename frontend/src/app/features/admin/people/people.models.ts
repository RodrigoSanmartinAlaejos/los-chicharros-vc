// Tipos del contrato OpenAPI (etiqueta «Personas»). Las fechas viajan como texto ISO (AAAA-MM-DD).

export interface PersonRequest {
  firstName: string;
  lastName: string;
  /** Opcional: los entrenadores adultos no la necesitan. */
  birthDate: string | null;
  /** Si se puede mostrar su foto en la web. */
  imageConsent: boolean;
}

/** Persona con todos sus datos: solo la ve el panel de gestión. */
export interface Person extends PersonRequest {
  id: number;
  photoUrl: string | null;
}
