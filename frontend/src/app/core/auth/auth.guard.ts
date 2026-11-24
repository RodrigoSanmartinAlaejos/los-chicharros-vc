import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs';
import { AuthService } from './auth.service';

/**
 * Deja entrar en el panel solo con sesión. Sin access token (por ejemplo, tras recargar la
 * página), lo pide con la cookie de refresco; si no hay sesión, lleva al login recordando a dónde
 * iba. Ocultar el panel no es la seguridad: la API comprueba cada petición (reglas técnicas, regla
 * 6 de Angular).
 */
export const authGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const toLogin = () => router.createUrlTree(['/login'], { queryParams: { redirect: state.url } });
  if (auth.isAuthenticated()) {
    return true;
  }
  return auth.refresh().pipe(map((ok) => ok || toLogin()));
};
