import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { SiteFooter } from './site-footer';

describe('SiteFooter', () => {
  it('enlaza las páginas legales', async () => {
    await TestBed.configureTestingModule({
      imports: [SiteFooter],
      providers: [provideRouter([])],
    }).compileComponents();
    const fixture = TestBed.createComponent(SiteFooter);
    await fixture.whenStable();
    const links = (fixture.nativeElement as HTMLElement).querySelectorAll('a');

    expect(Array.from(links, (a) => a.getAttribute('href'))).toEqual([
      '/privacidad',
      '/aviso-legal',
      '/cookies',
    ]);
  });
});
