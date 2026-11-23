import { HttpInterceptorFn } from '@angular/common/http';
import { InjectionToken, inject } from '@angular/core';

/**
 * Origen de la API para las peticiones a `/api`. En el navegador es vacío: se llama a la ruta
 * relativa y el servidor SSR la reenvía (ADR-004). En el servidor es `API_URL`, porque Node no
 * tiene una página desde la que resolver rutas relativas (reglas técnicas, regla 4 de Angular).
 */
export const API_ORIGIN = new InjectionToken<string>('API_ORIGIN', {
  providedIn: 'root',
  factory: () => '',
});

/** Añade el origen de la API a las peticiones relativas a `/api/`. */
export const apiOriginInterceptor: HttpInterceptorFn = (req, next) => {
  const origin = inject(API_ORIGIN);
  if (!origin || !req.url.startsWith('/api/')) {
    return next(req);
  }
  return next(req.clone({ url: origin + req.url }));
};
