import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { PublicLayout } from './public-layout';

describe('PublicLayout', () => {
  it('muestra la cabecera, el contenido y el pie', async () => {
    await TestBed.configureTestingModule({
      imports: [PublicLayout],
      providers: [provideRouter([])],
    }).compileComponents();
    const fixture = TestBed.createComponent(PublicLayout);
    await fixture.whenStable();
    const page = fixture.nativeElement as HTMLElement;

    expect(page.querySelector('header')).toBeTruthy();
    expect(page.querySelector('main#contenido')).toBeTruthy();
    expect(page.querySelector('footer')).toBeTruthy();
  });
});
