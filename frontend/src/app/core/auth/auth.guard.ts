import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

/**
 * Deja entrar en el panel solo con sesión; si no, lleva al login recordando a dónde iba. Ocultar
 * el panel no es la seguridad: la API comprueba cada petición (reglas técnicas, regla 6 de
 * Angular).
 */
export const authGuard: CanActivateFn = (_route, state) => {
  if (inject(AuthService).isAuthenticated()) {
    return true;
  }
  return inject(Router).createUrlTree(['/login'], { queryParams: { redirect: state.url } });
};
