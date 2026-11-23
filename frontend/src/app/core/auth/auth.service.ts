import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, map } from 'rxjs';

/** Respuesta del login (contrato OpenAPI, `AccessTokenResponse`). */
interface AccessTokenResponse {
  accessToken: string;
  expiresIn: number;
}

interface Session {
  readonly accessToken: string;
  readonly email: string;
  /** Momento en que caduca el token, en milisegundos. */
  readonly expiresAt: number;
}

/**
 * Sesión del panel. El access token vive solo en memoria, nunca en `localStorage` ni
 * `sessionStorage` (reglas técnicas, regla 5 de Angular): al recargar la página hay que volver a
 * entrar hasta que llegue la renovación con la cookie de refresco (#54).
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly session = signal<Session | null>(null);

  /** Email con el que se inició la sesión, para mostrarlo en el panel. */
  readonly email = computed(() => this.session()?.email ?? null);

  /** Inicia sesión; si las credenciales no valen, el observable falla con el 401 de la API. */
  login(email: string, password: string): Observable<void> {
    return this.http
      .post<AccessTokenResponse>('/api/v1/auth/login', { email, password })
      .pipe(
        map(({ accessToken, expiresIn }) =>
          this.session.set({ accessToken, email, expiresAt: Date.now() + expiresIn * 1000 }),
        ),
      );
  }

  /** Olvida el token. El cierre en el servidor (revocar el refresh token) llega con #54. */
  logout(): void {
    this.session.set(null);
  }

  /** Token para la cabecera `Authorization`, o `null` si no hay sesión o ya ha caducado. */
  accessToken(): string | null {
    const session = this.session();
    return session && Date.now() < session.expiresAt ? session.accessToken : null;
  }

  isAuthenticated(): boolean {
    return this.accessToken() !== null;
  }
}
