import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { DestroyRef, Injectable, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { Observable, catchError, finalize, map, of, shareReplay } from 'rxjs';

/** Respuesta del login y de la renovación (contrato OpenAPI, `AccessTokenResponse`). */
interface AccessTokenResponse {
  accessToken: string;
  expiresIn: number;
}

interface Session {
  readonly accessToken: string;
  readonly email: string | null;
  /** Momento en que caduca el token, en milisegundos. */
  readonly expiresAt: number;
  /** Momento en que se emitió, para saber si ha habido actividad desde entonces. */
  readonly issuedAt: number;
}

/** Margen con el que se renueva el token antes de que caduque. */
export const RENEW_BEFORE_MS = 60_000;

/**
 * Sesión del panel. El access token vive solo en memoria (reglas técnicas, regla 5 de Angular); el
 * refresh token va en una cookie `HttpOnly` que el navegador manda solo a `/api/v1/auth`.
 *
 * - Al entrar en el panel sin token (por ejemplo, tras recargar), se pide uno con la cookie.
 * - Un minuto antes de que caduque, se renueva si ha habido actividad desde el último; si no, se
 *   deja caducar, para que 2 horas sin actividad cierren la sesión (H10).
 * - Las renovaciones van de una en una: si se piden dos a la vez, comparten la misma petición
 *   (si no, la segunda usaría un token ya rotado y la API cerraría la sesión).
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly browser = isPlatformBrowser(inject(PLATFORM_ID));
  private readonly session = signal<Session | null>(null);
  private lastActivity = 0;
  private renewTimer: ReturnType<typeof setTimeout> | undefined;
  private refreshing: Observable<boolean> | null = null;

  /** Email con el que se inició la sesión, para mostrarlo en el panel. */
  readonly email = computed(() => this.session()?.email ?? null);

  constructor() {
    if (this.browser) {
      const markActivity = () => (this.lastActivity = Date.now());
      for (const event of ['pointerdown', 'keydown'] as const) {
        document.addEventListener(event, markActivity, { passive: true });
      }
      inject(DestroyRef).onDestroy(() => {
        clearTimeout(this.renewTimer);
        for (const event of ['pointerdown', 'keydown'] as const) {
          document.removeEventListener(event, markActivity);
        }
      });
    }
  }

  /** Inicia sesión; si las credenciales no valen, el observable falla con el 401 de la API. */
  login(email: string, password: string): Observable<void> {
    return this.http
      .post<AccessTokenResponse>('/api/v1/auth/login', { email, password })
      .pipe(map((response) => this.start(response)));
  }

  /**
   * Pide un access token nuevo con la cookie de refresco. Devuelve `true` si lo consigue; si no,
   * cierra la sesión en memoria y devuelve `false`. Nunca falla.
   */
  refresh(): Observable<boolean> {
    this.refreshing ??= this.http.post<AccessTokenResponse>('/api/v1/auth/refresh', {}).pipe(
      map((response) => {
        this.start(response);
        return true;
      }),
      catchError(() => {
        this.clear();
        return of(false);
      }),
      finalize(() => (this.refreshing = null)),
      shareReplay(1),
    );
    return this.refreshing;
  }

  /** Cierra la sesión en la API (revoca el refresh token y borra la cookie) y en memoria. */
  logout(): Observable<void> {
    return this.http.post<void>('/api/v1/auth/logout', {}).pipe(
      catchError(() => of(undefined)),
      map(() => undefined),
      finalize(() => this.clear()),
    );
  }

  /** Token para la cabecera `Authorization`, o `null` si no hay sesión o ya ha caducado. */
  accessToken(): string | null {
    const session = this.session();
    return session && Date.now() < session.expiresAt ? session.accessToken : null;
  }

  isAuthenticated(): boolean {
    return this.accessToken() !== null;
  }

  private start({ accessToken, expiresIn }: AccessTokenResponse): void {
    const now = Date.now();
    this.session.set({
      accessToken,
      email: emailOf(accessToken),
      expiresAt: now + expiresIn * 1000,
      issuedAt: now,
    });
    this.scheduleRenewal(expiresIn * 1000);
  }

  private scheduleRenewal(validForMs: number): void {
    if (!this.browser) {
      return;
    }
    clearTimeout(this.renewTimer);
    this.renewTimer = setTimeout(
      () => {
        const session = this.session();
        if (session && this.lastActivity > session.issuedAt) {
          this.refresh().subscribe();
        }
      },
      Math.max(0, validForMs - RENEW_BEFORE_MS),
    );
  }

  private clear(): void {
    clearTimeout(this.renewTimer);
    this.session.set(null);
  }
}

/** Email de la claim `email` del token (JWT: cabecera.carga.firma, en base64url). */
function emailOf(token: string): string | null {
  try {
    const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    const json = new TextDecoder().decode(
      Uint8Array.from(atob(payload), (char) => char.charCodeAt(0)),
    );
    const email = (JSON.parse(json) as { email?: unknown }).email;
    return typeof email === 'string' ? email : null;
  } catch {
    return null;
  }
}
