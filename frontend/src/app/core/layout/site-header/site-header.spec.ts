import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { SiteHeader } from './site-header';

@Component({ template: '' })
class Empty {}

describe('SiteHeader', () => {
  async function render() {
    await TestBed.configureTestingModule({
      imports: [SiteHeader],
      providers: [provideRouter([{ path: '**', component: Empty }])],
    }).compileComponents();
    const fixture = TestBed.createComponent(SiteHeader);
    await fixture.whenStable();
    return fixture;
  }

  it('enlaza las cinco secciones del menú principal', async () => {
    const fixture = await render();
    const links = (fixture.nativeElement as HTMLElement).querySelectorAll('nav a');

    expect(Array.from(links, (a) => a.textContent?.trim())).toEqual([
      'Noticias',
      'Equipos',
      'Calendario',
      'Clasificación',
      'Club',
    ]);
  });

  it('abre y cierra el menú con el botón y lo anuncia con aria-expanded', async () => {
    const fixture = await render();
    const page = fixture.nativeElement as HTMLElement;
    const toggle = page.querySelector('button') as HTMLButtonElement;

    expect(toggle.getAttribute('aria-expanded')).toBe('false');
    expect(toggle.textContent).toContain('Abrir menú');

    toggle.click();
    await fixture.whenStable();

    expect(toggle.getAttribute('aria-expanded')).toBe('true');
    expect(toggle.textContent).toContain('Cerrar menú');
    expect(page.querySelector('nav')?.classList).toContain('site-header__nav--open');
  });

  it('cierra el menú al elegir una sección', async () => {
    const fixture = await render();
    const page = fixture.nativeElement as HTMLElement;
    (page.querySelector('button') as HTMLButtonElement).click();
    await fixture.whenStable();

    (page.querySelector('nav a') as HTMLAnchorElement).click();
    await fixture.whenStable();

    expect(page.querySelector('button')?.getAttribute('aria-expanded')).toBe('false');
  });
});
