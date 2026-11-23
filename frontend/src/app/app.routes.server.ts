import { RenderMode, ServerRoute } from '@angular/ssr';

/**
 * Modo de renderizado de cada ruta. Las páginas públicas se renderizan en el servidor en cada
 * petición, porque sus datos cambian (reglas técnicas, «SSR»). El panel será `RenderMode.Client`.
 */
export const serverRoutes: ServerRoute[] = [
  {
    path: '**',
    renderMode: RenderMode.Server,
  },
];
