import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Season } from '../configuration.models';
import { SeasonsPanel } from './seasons-panel';

const URL = '/api/v1/seasons';
const ACTIVE: Season = {
  id: 1,
  name: '2026-27',
  startDate: '2026-09-01',
  endDate: '2027-06-30',
  active: true,
};

describe('SeasonsPanel', () => {
  let http: HttpTestingController;

  async function render() {
    TestBed.configureTestingModule({
      imports: [SeasonsPanel],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    http = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(SeasonsPanel);
    await fixture.whenStable();
    return { fixture, page: fixture.nativeElement as HTMLElement };
  }

  function type(page: HTMLElement, selector: string, value: string) {
    const input = page.querySelector(selector) as HTMLInputElement;
    input.value = value;
    input.dispatchEvent(new Event('input'));
  }

  afterEach(() => http.verify());

  it('muestra las temporadas y marca la activa', async () => {
    const { fixture, page } = await render();
    expect(page.textContent).toContain('Cargando temporadas');

    http.expectOne(URL).flush([ACTIVE]);
    await fixture.whenStable();

    const row = page.querySelector('tbody tr') as HTMLElement;
    expect(row.textContent).toContain('2026-27');
    expect(row.querySelector('.badge')?.textContent).toBe('Activa');
  });

  it('crea una temporada activa y recarga la lista', async () => {
    const { fixture, page } = await render();
    http.expectOne(URL).flush([]);
    await fixture.whenStable();
    expect(page.textContent).toContain('Todavía no hay temporadas');

    (page.querySelector('.panel__header button') as HTMLButtonElement).click();
    await fixture.whenStable();
    type(page, '#season-name', '2026-27');
    type(page, '#season-start', '2026-09-01');
    type(page, '#season-end', '2027-06-30');
    (page.querySelector('#season-active') as HTMLInputElement).click();
    (page.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));

    const create = http.expectOne({ method: 'POST', url: URL });
    expect(create.request.body).toEqual({
      name: '2026-27',
      startDate: '2026-09-01',
      endDate: '2027-06-30',
      active: true,
    });
    create.flush(ACTIVE);
    http.expectOne(URL).flush([ACTIVE]);
    await fixture.whenStable();

    expect(page.querySelector('form')).toBeNull();
    expect(page.querySelector('[aria-live]')?.textContent).toBe(
      'Se ha guardado la temporada 2026-27.',
    );
  });

  it('pone junto al campo el error que devuelve la API', async () => {
    const { fixture, page } = await render();
    http.expectOne(URL).flush([]);
    await fixture.whenStable();
    (page.querySelector('.panel__header button') as HTMLButtonElement).click();
    await fixture.whenStable();
    type(page, '#season-name', '2026-27');
    type(page, '#season-start', '2027-06-30');
    type(page, '#season-end', '2026-09-01');
    (page.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));

    http.expectOne({ method: 'POST', url: URL }).flush(
      {
        status: 400,
        detail: 'La petición tiene campos no válidos',
        errors: [{ field: 'endDate', message: 'debe ser posterior a la fecha de inicio' }],
      },
      { status: 400, statusText: 'Bad Request' },
    );
    await fixture.whenStable();

    expect(page.querySelector('#season-end-error')?.textContent).toBe(
      'debe ser posterior a la fecha de inicio',
    );
    expect(page.querySelector('#season-end')?.getAttribute('aria-invalid')).toBe('true');
    expect(page.querySelector('form .alert')).toBeNull();
  });

  it('no envía el formulario si falta algún campo', async () => {
    const { fixture, page } = await render();
    http.expectOne(URL).flush([]);
    await fixture.whenStable();
    (page.querySelector('.panel__header button') as HTMLButtonElement).click();
    await fixture.whenStable();

    type(page, '#season-name', '2026');
    (page.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));
    await fixture.whenStable();

    expect(page.querySelector('#season-name-error')?.textContent).toBe('Usa el formato 2026-27');
    expect(page.querySelector('#season-start-error')?.textContent).toBe(
      'Este campo es obligatorio',
    );
  });

  it('ofrece reintentar si la lista no carga', async () => {
    const { fixture, page } = await render();
    http.expectOne(URL).flush(null, { status: 500, statusText: 'Error' });
    await fixture.whenStable();

    expect(page.textContent).toContain('No se han podido cargar las temporadas');
    (page.querySelector('.state button') as HTMLButtonElement).click();
    http.expectOne(URL).flush([ACTIVE]);
    await fixture.whenStable();

    expect(page.querySelector('tbody')?.textContent).toContain('2026-27');
  });
});
