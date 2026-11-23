import { RenderMode, ServerRoute } from '@angular/ssr';

/**
 * Modo de renderizado de cada ruta. Las páginas públicas se renderizan en el servidor en cada
 * petición, porque sus datos cambian (reglas técnicas, «SSR»). El panel de gestión y su login se
 * renderizan en el navegador: no los indexan los buscadores y dependen de la sesión, que solo
 * existe allí.
 */
export const serverRoutes: ServerRoute[] = [
  { path: 'login', renderMode: RenderMode.Client },
  { path: 'gestion', renderMode: RenderMode.Client },
  { path: 'gestion/**', renderMode: RenderMode.Client },
  { path: '**', renderMode: RenderMode.Server },
];
