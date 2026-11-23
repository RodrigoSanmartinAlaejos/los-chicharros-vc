import { TestBed } from '@angular/core/testing';
import { Meta } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  it('muestra la cabecera, el contenido y el pie', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const page = fixture.nativeElement as HTMLElement;

    expect(page.querySelector('header')).toBeTruthy();
    expect(page.querySelector('main#contenido')).toBeTruthy();
    expect(page.querySelector('footer')).toBeTruthy();
  });

  it('ofrece un enlace para saltar al contenido', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const skip = (fixture.nativeElement as HTMLElement).querySelector('a.skip-link');

    expect(skip?.getAttribute('href')).toBe('#contenido');
  });

  it('pide a los buscadores que no indexen la web fuera de producción', () => {
    TestBed.createComponent(App);

    expect(TestBed.inject(Meta).getTag('name="robots"')?.content).toBe('noindex, nofollow');
  });
});
