import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from './auth.service';
import { fakeAccessToken } from './auth.testing';

@Component({ template: '' })
class Empty {}

describe('authGuard', () => {
  let http: HttpTestingController;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([
          { path: 'gestion/configuracion', component: Empty, canActivate: [authGuard] },
          { path: '**', component: Empty },
        ]),
      ],
    });
    http = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
  });

  afterEach(() => http.verify());

  /** El guard pide la renovación cuando el router llega a él, que no es inmediato. */
  function refreshRequest() {
    return vi.waitFor(() => http.expectOne('/api/v1/auth/refresh'));
  }

  it('recupera la sesión con la cookie al recargar la página', async () => {
    const navigation = router.navigateByUrl('/gestion/configuracion');
    (await refreshRequest()).flush({ accessToken: fakeAccessToken(), expiresIn: 900 });
    await navigation;

    expect(router.url).toBe('/gestion/configuracion');
  });

  it('lleva al login si no hay sesión, recordando a dónde iba', async () => {
    const navigation = router.navigateByUrl('/gestion/configuracion');
    (await refreshRequest()).flush({ status: 401 }, { status: 401, statusText: 'Unauthorized' });
    await navigation;

    expect(router.url).toBe('/login?redirect=%2Fgestion%2Fconfiguracion');
  });

  it('deja entrar sin pedir nada si ya hay sesión', async () => {
    TestBed.inject(AuthService).login('admin@loschicharros.example', 'secreta').subscribe();
    http.expectOne('/api/v1/auth/login').flush({ accessToken: fakeAccessToken(), expiresIn: 900 });

    await router.navigateByUrl('/gestion/configuracion');

    expect(router.url).toBe('/gestion/configuracion');
  });
});
