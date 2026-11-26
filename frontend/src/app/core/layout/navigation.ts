/** Un enlace del menú principal de la web pública. */
export interface NavItem {
  readonly path: string;
  readonly label: string;
}

/** Menú principal, en el orden de los wireframes (docs/02-requisitos/wireframes.md). */
export const MAIN_NAV: readonly NavItem[] = [
  { path: '/noticias', label: 'Noticias' },
  { path: '/equipos', label: 'Equipos' },
  { path: '/calendario', label: 'Calendario' },
  { path: '/clasificacion', label: 'Clasificación' },
  { path: '/club', label: 'Club' },
];

/** Enlaces legales del pie (H08). */
export const LEGAL_NAV: readonly NavItem[] = [
  { path: '/privacidad', label: 'Privacidad' },
  { path: '/aviso-legal', label: 'Aviso legal' },
  { path: '/cookies', label: 'Cookies' },
];

/**
 * Menú lateral del panel de gestión. Cada historia añade su sección al crear su pantalla (orden de
 * los wireframes: noticias, equipos, personas, competiciones, partidos, clasificaciones,
 * configuración).
 */
export const ADMIN_NAV: readonly NavItem[] = [
  { path: '/gestion/personas', label: 'Personas' },
  { path: '/gestion/configuracion', label: 'Configuración' },
];
