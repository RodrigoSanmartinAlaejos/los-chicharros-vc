/** Página de resultados de la API (docs/05-api/README.md, «Paginación»). */
export interface Page<T> {
  content: T[];
  /** Número de página, empezando por 0. */
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
