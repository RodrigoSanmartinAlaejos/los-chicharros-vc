import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from './auth.service';
import { fakeAccessToken } from './auth.testing';

@Component({ template: '' })
class Empty {}

describe('authInterceptor', () => {
  let http: HttpClient;
  let backend: HttpTestingController;
  let auth: AuthService;
  const first = fakeAccessToken('primero@loschicharros.example');
  const second = fakeAccessToken('segundo@loschicharros.example');

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        provideRouter([{ path: '**', component: Empty }]),
      ],
    });
    http = TestBed.inject(HttpClient);
    backend = TestBed.inject(HttpTestingController);
    auth = TestBed.inject(AuthService);
  });

  afterEach(() => backend.verify());

  function logIn() {
    auth.login('admin@loschicharros.example', 'secreta').subscribe();
    backend.expectOne('/api/v1/auth/login').flush({ accessToken: first, expiresIn: 900 });
  }

  it('añade el token a las peticiones a la API', () => {
    logIn();

    http.post('/api/v1/venues', {}).subscribe();

    expect(backend.expectOne('/api/v1/venues').request.headers.get('Authorization')).toBe(
      `Bearer ${first}`,
    );
  });

  it('no añade nada sin sesión ni fuera de la API', () => {
    http.get('/api/v1/venues').subscribe();
    logIn();
    http.get('/escudo.svg').subscribe();

    expect(backend.expectOne('/api/v1/venues').request.headers.has('Authorization')).toBe(false);
    expect(backend.expectOne('/escudo.svg').request.headers.has('Authorization')).toBe(false);
  });

  it('renueva la sesión y repite la petición si la API responde 401', () => {
    logIn();
    let body: unknown;

    http.post('/api/v1/venues', {}).subscribe((response) => (body = response));
    backend
      .expectOne('/api/v1/venues')
      .flush({ status: 401 }, { status: 401, statusText: 'Unauthorized' });
    backend.expectOne('/api/v1/auth/refresh').flush({ accessToken: second, expiresIn: 900 });
    const retry = backend.expectOne('/api/v1/venues');
    retry.flush({ id: 1 });

    expect(retry.request.headers.get('Authorization')).toBe(`Bearer ${second}`);
    expect(body).toEqual({ id: 1 });
  });

  it('lleva al login si la sesión no se puede renovar', async () => {
    logIn();
    const router = TestBed.inject(Router);
    await router.navigateByUrl('/gestion/configuracion');

    http.get('/api/v1/venues').subscribe({ error: () => undefined });
    backend
      .expectOne('/api/v1/venues')
      .flush({ status: 401 }, { status: 401, statusText: 'Unauthorized' });
    backend
      .expectOne('/api/v1/auth/refresh')
      .flush({ status: 401 }, { status: 401, statusText: 'Unauthorized' });

    expect(auth.isAuthenticated()).toBe(false);
    await vi.waitFor(() => expect(router.url).toBe('/login?redirect=%2Fgestion%2Fconfiguracion'));
  });

  it('no toca las rutas de autenticación', () => {
    logIn();

    http.post('/api/v1/auth/logout', {}).subscribe();

    expect(backend.expectOne('/api/v1/auth/logout').request.headers.has('Authorization')).toBe(
      false,
    );
  });
});
