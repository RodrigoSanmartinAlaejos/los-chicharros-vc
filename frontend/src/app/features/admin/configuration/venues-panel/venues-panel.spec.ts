import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { VenuesPanel } from './venues-panel';

const URL = '/api/v1/venues';

describe('VenuesPanel', () => {
  let http: HttpTestingController;

  afterEach(() => http.verify());

  it('crea una instalación sin coordenadas', async () => {
    TestBed.configureTestingModule({
      imports: [VenuesPanel],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    http = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(VenuesPanel);
    await fixture.whenStable();
    http.expectOne(URL).flush([]);
    await fixture.whenStable();
    const page = fixture.nativeElement as HTMLElement;

    (page.querySelector('.panel__header button') as HTMLButtonElement).click();
    await fixture.whenStable();
    for (const [selector, value] of [
      ['#venue-name', 'Polideportivo La Marina'],
      ['#venue-address', 'Calle La Marina, 3'],
    ]) {
      const input = page.querySelector(selector) as HTMLInputElement;
      input.value = value;
      input.dispatchEvent(new Event('input'));
    }
    (page.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));

    const create = http.expectOne({ method: 'POST', url: URL });
    expect(create.request.body).toEqual({
      name: 'Polideportivo La Marina',
      address: 'Calle La Marina, 3',
      latitude: null,
      longitude: null,
    });
    const venue = { id: 2, ...create.request.body };
    create.flush(venue);
    http.expectOne(URL).flush([venue]);
    await fixture.whenStable();

    expect(page.querySelector('tbody tr')?.textContent).toContain('Calle La Marina, 3');
  });
});
