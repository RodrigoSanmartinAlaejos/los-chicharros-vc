import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AuthService, RENEW_BEFORE_MS } from './auth.service';
import { fakeAccessToken } from './auth.testing';

describe('AuthService', () => {
  let auth: AuthService;
  let http: HttpTestingController;
  const token = fakeAccessToken('admin@loschicharros.example');

  beforeEach(() => {
    vi.useFakeTimers();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    auth = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    http.verify();
    vi.useRealTimers();
  });

  function logIn() {
    auth.login('admin@loschicharros.example', 'secreta').subscribe();
    http.expectOne('/api/v1/auth/login').flush({ accessToken: token, expiresIn: 900 });
  }

  it('guarda el token solo en memoria al iniciar sesión y saca el email del token', () => {
    auth.login('admin@loschicharros.example', 'secreta').subscribe();
    const request = http.expectOne('/api/v1/auth/login');
    expect(request.request.method).toBe('POST');
    request.flush({ accessToken: token, expiresIn: 900 });

    expect(auth.accessToken()).toBe(token);
    expect(auth.email()).toBe('admin@loschicharros.example');
    expect(localStorage.length + sessionStorage.length).toBe(0);
  });

  it('no da por buena una sesión cuyo token ha caducado', () => {
    logIn();

    vi.advanceTimersByTime(900_000);

    expect(auth.isAuthenticated()).toBe(false);
  });

  it('renueva el token un minuto antes de que caduque si ha habido actividad', () => {
    logIn();
    vi.advanceTimersByTime(60_000);
    document.dispatchEvent(new KeyboardEvent('keydown'));

    vi.advanceTimersByTime(900_000 - RENEW_BEFORE_MS - 60_000);

    const renewed = fakeAccessToken('admin@loschicharros.example');
    http.expectOne('/api/v1/auth/refresh').flush({ accessToken: renewed, expiresIn: 900 });
    vi.advanceTimersByTime(RENEW_BEFORE_MS);
    expect(auth.isAuthenticated()).toBe(true);
  });

  it('deja caducar el token si no ha habido actividad', () => {
    logIn();

    vi.advanceTimersByTime(900_000);

    http.expectNone('/api/v1/auth/refresh');
    expect(auth.isAuthenticated()).toBe(false);
  });

  it('renueva la sesión con la cookie y comparte la petición si se pide dos veces a la vez', () => {
    const results: boolean[] = [];

    auth.refresh().subscribe((ok) => results.push(ok));
    auth.refresh().subscribe((ok) => results.push(ok));
    http.expectOne('/api/v1/auth/refresh').flush({ accessToken: token, expiresIn: 900 });

    expect(results).toEqual([true, true]);
    expect(auth.email()).toBe('admin@loschicharros.example');
  });

  it('cierra la sesión en memoria si la renovación falla', () => {
    logIn();
    let result: boolean | undefined;

    auth.refresh().subscribe((ok) => (result = ok));
    http
      .expectOne('/api/v1/auth/refresh')
      .flush({ status: 401 }, { status: 401, statusText: 'Unauthorized' });

    expect(result).toBe(false);
    expect(auth.isAuthenticated()).toBe(false);
  });

  it('cierra la sesión en la API y en memoria', () => {
    logIn();
    let done = false;

    auth.logout().subscribe({ complete: () => (done = true) });
    const request = http.expectOne('/api/v1/auth/logout');
    expect(request.request.method).toBe('POST');
    request.flush(null, { status: 204, statusText: 'No Content' });

    expect(done).toBe(true);
    expect(auth.isAuthenticated()).toBe(false);
    expect(auth.email()).toBeNull();
  });

  it('cierra la sesión en memoria aunque la API no responda', () => {
    logIn();

    auth.logout().subscribe();
    http.expectOne('/api/v1/auth/logout').error(new ProgressEvent('error'));

    expect(auth.isAuthenticated()).toBe(false);
  });
});
