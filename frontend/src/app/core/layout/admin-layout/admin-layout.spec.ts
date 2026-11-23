import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AdminLayout } from './admin-layout';

@Component({ template: '' })
class Empty {}

describe('AdminLayout', () => {
  async function render() {
    await TestBed.configureTestingModule({
      imports: [AdminLayout],
      providers: [provideRouter([{ path: '**', component: Empty }])],
    }).compileComponents();
    const fixture = TestBed.createComponent(AdminLayout);
    await fixture.whenStable();
    return fixture;
  }

  it('enlaza las secciones del panel y deja volver a la web', async () => {
    const fixture = await render();
    const page = fixture.nativeElement as HTMLElement;
    const links = Array.from(page.querySelectorAll('.admin__list a'));

    expect(links.map((a) => a.textContent?.trim())).toEqual(['Configuración']);
    expect(links[0].getAttribute('href')).toBe('/gestion/configuracion');
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
});
