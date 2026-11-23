import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let auth: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
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

  it('guarda el token en memoria al iniciar sesión', () => {
    auth.login('admin@loschicharros.example', 'secreta').subscribe();
    const request = http.expectOne('/api/v1/auth/login');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({
      email: 'admin@loschicharros.example',
      password: 'secreta',
    });
    request.flush({ accessToken: 'token', expiresIn: 900 });

    expect(auth.isAuthenticated()).toBe(true);
    expect(auth.accessToken()).toBe('token');
    expect(auth.email()).toBe('admin@loschicharros.example');
    expect(localStorage.length + sessionStorage.length).toBe(0);
  });

  it('no da por buena una sesión cuyo token ha caducado', () => {
    vi.useFakeTimers();
    auth.login('admin@loschicharros.example', 'secreta').subscribe();
    http.expectOne('/api/v1/auth/login').flush({ accessToken: 'token', expiresIn: 900 });

    vi.advanceTimersByTime(900_000);

    expect(auth.accessToken()).toBeNull();
    expect(auth.isAuthenticated()).toBe(false);
  });

  it('olvida el token al cerrar la sesión', () => {
    auth.login('admin@loschicharros.example', 'secreta').subscribe();
    http.expectOne('/api/v1/auth/login').flush({ accessToken: 'token', expiresIn: 900 });

    auth.logout();

    expect(auth.isAuthenticated()).toBe(false);
    expect(auth.email()).toBeNull();
  });

  it('no abre sesión si la API rechaza las credenciales', () => {
    let failed = false;
    auth.login('admin@loschicharros.example', 'mala').subscribe({ error: () => (failed = true) });
    http
      .expectOne('/api/v1/auth/login')
      .flush({ status: 401 }, { status: 401, statusText: 'Unauthorized' });

    expect(failed).toBe(true);
    expect(auth.isAuthenticated()).toBe(false);
  });
});
