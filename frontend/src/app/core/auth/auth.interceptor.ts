import { HttpErrorResponse, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from './auth.service';

const AUTH_PATH = '/api/v1/auth/';

/**
 * Añade el access token a las peticiones a la API. Si la API responde 401 (el token caducó, por
 * ejemplo, tras un rato sin actividad), renueva la sesión una vez con la cookie y repite la
 * petición; si no se puede renovar, lleva al login y vuelve después a la misma página. Las rutas de
 * autenticación no llevan token: usan la cookie y su 401 es un error de credenciales o de sesión.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith('/api/') || req.url.startsWith(AUTH_PATH)) {
    return next(req);
  }
  const auth = inject(AuthService);
  const router = inject(Router);
  const withToken = (request: HttpRequest<unknown>) => {
    const token = auth.accessToken();
    return token ? request.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : request;
  };
  return next(withToken(req)).pipe(
    catchError((error: unknown) => {
      if (!(error instanceof HttpErrorResponse) || error.status !== 401) {
        return throwError(() => error);
      }
      return auth.refresh().pipe(
        switchMap((ok) => {
          if (ok) {
            return next(withToken(req));
          }
          void router.navigate(['/login'], { queryParams: { redirect: router.url } });
          return throwError(() => error);
        }),
      );
    }),
  );
};
