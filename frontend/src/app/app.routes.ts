import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

const comingSoon = () => import('./features/coming-soon/coming-soon').then((m) => m.ComingSoon);

/**
 * Rutas de la web, todas con carga diferida (reglas técnicas, regla 7 de Angular). Hay dos zonas,
 * cada una con su layout: el panel de gestión (`/gestion`, con su `/login`) y la web pública. Las secciones marcadas
 * como provisionales se sustituyen por su página real en su historia.
 */
export const routes: Routes = [
  {
    path: 'login',
    title: 'Iniciar sesión',
    loadComponent: () => import('./features/login/login').then((m) => m.Login),
  },
  {
    // Panel de gestión: solo con sesión (H09). La API vuelve a comprobar cada petición
    path: 'gestion',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./core/layout/admin-layout/admin-layout').then((m) => m.AdminLayout),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'configuracion' },
      {
        path: 'configuracion',
        title: 'Configuración',
        loadComponent: () =>
          import('./features/admin/configuration/configuration').then((m) => m.Configuration),
      },
    ],
  },
  {
    path: '',
    loadComponent: () =>
      import('./core/layout/public-layout/public-layout').then((m) => m.PublicLayout),
    children: [
      {
        path: '',
        pathMatch: 'full',
        loadComponent: () => import('./features/home/home').then((m) => m.Home),
      },
      // Provisionales: H02-H03 (#46, #47), H04 (#48), H05 (#49), H06 (#50), H07 (#51), H08 (#52)
      { path: 'noticias', title: 'Noticias', loadComponent: comingSoon },
      { path: 'equipos', title: 'Equipos', loadComponent: comingSoon },
      { path: 'calendario', title: 'Calendario', loadComponent: comingSoon },
      { path: 'clasificacion', title: 'Clasificación', loadComponent: comingSoon },
      { path: 'club', title: 'Club', loadComponent: comingSoon },
      { path: 'privacidad', title: 'Privacidad', loadComponent: comingSoon },
      { path: 'aviso-legal', title: 'Aviso legal', loadComponent: comingSoon },
      { path: 'cookies', title: 'Cookies', loadComponent: comingSoon },
      {
        path: '**',
        title: 'Página no encontrada',
        loadComponent: () => import('./features/not-found/not-found').then((m) => m.NotFound),
      },
    ],
  },
];
