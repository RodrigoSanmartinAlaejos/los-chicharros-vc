import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from './auth.service';

@Component({ template: '' })
class Empty {}

describe('authGuard', () => {
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
  });

  it('lleva al login sin sesión, recordando a dónde iba', async () => {
    const router = TestBed.inject(Router);

    await router.navigateByUrl('/gestion/configuracion');

    expect(router.url).toBe('/login?redirect=%2Fgestion%2Fconfiguracion');
  });

  it('deja entrar con sesión', async () => {
    TestBed.inject(AuthService).login('admin@loschicharros.example', 'secreta').subscribe();
    TestBed.inject(HttpTestingController)
      .expectOne('/api/v1/auth/login')
      .flush({ accessToken: 'token', expiresIn: 900 });
    const router = TestBed.inject(Router);

    await router.navigateByUrl('/gestion/configuracion');

    expect(router.url).toBe('/gestion/configuracion');
  });
});
