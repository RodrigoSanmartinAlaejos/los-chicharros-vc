import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

const AUTH_PATH = '/api/v1/auth/';

/**
 * Añade el access token a las peticiones a la API y, si la API responde 401 a una de ellas, cierra
 * la sesión y lleva al login, volviendo después a la misma página. Las rutas de autenticación no
 * llevan token: el login no lo necesita y su 401 es un error de credenciales.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith('/api/') || req.url.startsWith(AUTH_PATH)) {
    return next(req);
  }
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.accessToken();
  const request = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;
  return next(request).pipe(
    catchError((error: unknown) => {
      if (token && error instanceof HttpErrorResponse && error.status === 401) {
        auth.logout();
        void router.navigate(['/login'], { queryParams: { redirect: router.url } });
      }
      return throwError(() => error);
    }),
  );
};
