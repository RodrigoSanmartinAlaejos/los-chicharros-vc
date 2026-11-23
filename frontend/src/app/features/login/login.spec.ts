import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Meta } from '@angular/platform-browser';
import { Router, provideRouter } from '@angular/router';
import { Login } from './login';

@Component({ template: '' })
class Empty {}

describe('Login', () => {
  let http: HttpTestingController;

  async function render(url = '/login') {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([
          { path: 'login', component: Login },
          { path: '**', component: Empty },
        ]),
      ],
    });
    http = TestBed.inject(HttpTestingController);
    await TestBed.inject(Router).navigateByUrl(url);
    const fixture = TestBed.createComponent(Login);
    await fixture.whenStable();
    return { fixture, page: fixture.nativeElement as HTMLElement };
  }

  function fill(page: HTMLElement, email: string, password: string) {
    for (const [selector, value] of [
      ['#login-email', email],
      ['#login-password', password],
    ]) {
      const input = page.querySelector(selector) as HTMLInputElement;
      input.value = value;
      input.dispatchEvent(new Event('input'));
    }
    (page.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));
  }

  afterEach(() => http.verify());

  it('entra en el panel con las credenciales correctas', async () => {
    const { fixture, page } = await render();

    fill(page, ' admin@loschicharros.example ', 'secreta');
    const request = http.expectOne('/api/v1/auth/login');
    expect(request.request.body).toEqual({
      email: 'admin@loschicharros.example',
      password: 'secreta',
    });
    request.flush({ accessToken: 'token', expiresIn: 900 });
    await fixture.whenStable();

    expect(TestBed.inject(Router).url).toBe('/gestion');
  });

  it('vuelve a la página del panel que se pedía', async () => {
    const { fixture, page } = await render('/login?redirect=%2Fgestion%2Fconfiguracion');

    fill(page, 'admin@loschicharros.example', 'secreta');
    http.expectOne('/api/v1/auth/login').flush({ accessToken: 'token', expiresIn: 900 });
    await fixture.whenStable();

    expect(TestBed.inject(Router).url).toBe('/gestion/configuracion');
  });

  it('no redirige fuera del panel aunque lo pida el enlace', async () => {
    const { fixture, page } = await render('/login?redirect=https%3A%2F%2Fotra-web.example');

    fill(page, 'admin@loschicharros.example', 'secreta');
    http.expectOne('/api/v1/auth/login').flush({ accessToken: 'token', expiresIn: 900 });
    await fixture.whenStable();

    expect(TestBed.inject(Router).url).toBe('/gestion');
  });

  it('muestra un único mensaje genérico si las credenciales no valen', async () => {
    const { fixture, page } = await render();

    fill(page, 'admin@loschicharros.example', 'mala');
    http
      .expectOne('/api/v1/auth/login')
      .flush(
        { status: 401, detail: 'Email o contraseña incorrectos' },
        { status: 401, statusText: 'Unauthorized' },
      );
    await fixture.whenStable();

    expect(page.querySelector('[role="alert"]')?.textContent?.trim()).toBe(
      'Email o contraseña incorrectos.',
    );
    expect((page.querySelector('#login-password') as HTMLInputElement).value).toBe('');
    expect(TestBed.inject(Router).url).toBe('/login');
  });

  it('pide los dos campos antes de llamar a la API', async () => {
    const { fixture, page } = await render();

    fill(page, '', '');
    await fixture.whenStable();

    expect(page.querySelector('#login-email-error')?.textContent).toBe('Escribe tu email');
    expect(page.querySelector('#login-password-error')?.textContent).toBe('Escribe tu contraseña');
  });

  it('pide a los buscadores que no indexen el login', async () => {
    await render();

    expect(TestBed.inject(Meta).getTag('name="robots"')?.content).toBe('noindex, nofollow');
  });
});
