import { RenderMode, ServerRoute } from '@angular/ssr';

/**
 * Modo de renderizado de cada ruta. Las páginas públicas se renderizan en el servidor en cada
 * petición, porque sus datos cambian (reglas técnicas, «SSR»). El panel de gestión se renderiza en
 * el navegador: no lo indexan los buscadores y sus datos dependen de la sesión.
 */
export const serverRoutes: ServerRoute[] = [
  { path: 'gestion', renderMode: RenderMode.Client },
  { path: 'gestion/**', renderMode: RenderMode.Client },
  { path: '**', renderMode: RenderMode.Server },
];
