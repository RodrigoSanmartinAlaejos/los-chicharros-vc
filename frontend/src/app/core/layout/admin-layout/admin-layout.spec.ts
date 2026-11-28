import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { fakeAccessToken } from '../../auth/auth.testing';
import { AdminLayout } from './admin-layout';

@Component({ template: '' })
class Empty {}

describe('AdminLayout', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminLayout],
      providers: [
        provideRouter([{ path: '**', component: Empty }]),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();
  });

  async function render() {
    const fixture = TestBed.createComponent(AdminLayout);
    await fixture.whenStable();
    return fixture;
  }

  it('enlaza las secciones del panel y deja volver a la web', async () => {
    const fixture = await render();
    const page = fixture.nativeElement as HTMLElement;
    const links = Array.from(page.querySelectorAll('.admin__list a'));

    expect(links.map((a) => a.textContent?.trim())).toEqual([
      'Noticias',
      'Equipos',
      'Personas',
      'Competiciones',
      'Partidos',
      'Configuración',
    ]);
    expect(links[0].getAttribute('href')).toBe('/gestion/noticias');
    expect(page.querySelector('.admin__public-link')?.getAttribute('href')).toBe('/');
    expect(page.querySelector('main#contenido')).toBeTruthy();
  });

  it('abre el menú en móvil y lo cierra con Escape', async () => {
    const fixture = await render();
    const page = fixture.nativeElement as HTMLElement;
    const toggle = page.querySelector('.admin__toggle') as HTMLButtonElement;

    toggle.click();
    await fixture.whenStable();
    expect(toggle.getAttribute('aria-expanded')).toBe('true');
    expect(page.querySelector('nav')?.classList).toContain('admin__sidebar--open');

    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape' }));
    await fixture.whenStable();
    expect(toggle.getAttribute('aria-expanded')).toBe('false');
  });

  it('muestra quién tiene la sesión y la cierra también en la API', async () => {
    const auth = TestBed.inject(AuthService);
    auth.login('admin@loschicharros.example', 'secreta').subscribe();
    TestBed.inject(HttpTestingController)
      .expectOne('/api/v1/auth/login')
      .flush({ accessToken: fakeAccessToken('admin@loschicharros.example'), expiresIn: 900 });
    const fixture = await render();
    const page = fixture.nativeElement as HTMLElement;

    expect(page.querySelector('.admin__session-email')?.textContent).toBe(
      'admin@loschicharros.example',
    );

    (page.querySelector('.admin__logout') as HTMLButtonElement).click();
    TestBed.inject(HttpTestingController)
      .expectOne('/api/v1/auth/logout')
      .flush(null, { status: 204, statusText: 'No Content' });
    await fixture.whenStable();

    expect(auth.isAuthenticated()).toBe(false);
    expect(TestBed.inject(Router).url).toBe('/');
  });
});
