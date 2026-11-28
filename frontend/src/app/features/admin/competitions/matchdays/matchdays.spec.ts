import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Season } from '../../configuration/configuration.models';
import { Matchday } from '../competitions.models';
import { Matchdays } from './matchdays';

const SEASON: Season = {
  id: 2,
  name: '2026-27',
  startDate: '2026-09-01',
  endDate: '2027-06-30',
  active: true,
};
const J1: Matchday = { id: 10, competitionId: 3, number: 1, scheduledDate: '2026-10-03' };
const J2: Matchday = { id: 11, competitionId: 3, number: 2, scheduledDate: '2026-10-17' };

describe('Matchdays', () => {
  let http: HttpTestingController;

  async function render(matchdays: Matchday[]) {
    TestBed.configureTestingModule({
      imports: [Matchdays],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    http = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(Matchdays);
    fixture.componentRef.setInput('competitionId', 3);
    fixture.componentRef.setInput('title', 'Liga Insular · 2026-27');
    fixture.componentRef.setInput('season', SEASON);
    await fixture.whenStable();
    http.expectOne('/api/v1/competitions/3/matchdays').flush(matchdays);
    await fixture.whenStable();
    return { fixture, page: fixture.nativeElement as HTMLElement };
  }

  function value(page: HTMLElement, selector: string): string {
    return (page.querySelector(selector) as HTMLInputElement).value;
  }

  afterEach(() => http.verify());

  it('muestra las jornadas por número con su fecha', async () => {
    const { page } = await render([J1, J2]);

    const rows = page.querySelectorAll('tbody tr');
    expect(rows.length).toBe(2);
    expect(rows[0].textContent).toContain('1');
    expect(rows[0].textContent).toContain('03/10/2026');
  });

  it('propone la siguiente jornada y se queda abierto para añadir otra', async () => {
    const { fixture, page } = await render([J1, J2]);

    (page.querySelector('.panel__header .button--primary') as HTMLButtonElement).click();
    await fixture.whenStable();
    // Mismo intervalo que entre las dos últimas: dos semanas
    expect(value(page, '#matchday-number')).toBe('3');
    expect(value(page, '#matchday-date')).toBe('2026-10-31');
    (page.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));

    const create = http.expectOne({ method: 'POST', url: '/api/v1/matchdays' });
    expect(create.request.body).toEqual({
      competitionId: 3,
      number: 3,
      scheduledDate: '2026-10-31',
    });
    create.flush({ id: 12, competitionId: 3, number: 3, scheduledDate: '2026-10-31' });
    await fixture.whenStable();

    expect(page.querySelectorAll('tbody tr').length).toBe(3);
    expect(value(page, '#matchday-number')).toBe('4');
    expect(value(page, '#matchday-date')).toBe('2026-11-14');
  });

  it('empieza por la jornada 1 sin fecha si no hay ninguna', async () => {
    const { fixture, page } = await render([]);
    expect(page.textContent).toContain('Todavía no hay jornadas');

    (page.querySelector('.panel__header .button--primary') as HTMLButtonElement).click();
    await fixture.whenStable();

    expect(value(page, '#matchday-number')).toBe('1');
    expect(value(page, '#matchday-date')).toBe('');
    expect(page.querySelector('#matchday-date-hint')?.textContent).toContain(
      'Dentro de la temporada 2026-27',
    );
  });

  it('muestra junto al campo el error de la fecha fuera de la temporada', async () => {
    const { fixture, page } = await render([]);
    (page.querySelector('.panel__header .button--primary') as HTMLButtonElement).click();
    await fixture.whenStable();
    const date = page.querySelector('#matchday-date') as HTMLInputElement;
    date.value = '2027-07-03';
    date.dispatchEvent(new Event('input'));
    (page.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));

    http.expectOne({ method: 'POST', url: '/api/v1/matchdays' }).flush(
      {
        detail: 'Hay campos con errores',
        errors: [
          {
            field: 'scheduledDate',
            message:
              'La fecha tiene que estar dentro de la temporada 2026-27 (del 01/09/2026 al 30/06/2027)',
          },
        ],
      },
      { status: 400, statusText: 'Bad Request' },
    );
    await fixture.whenStable();

    expect(page.querySelector('#matchday-date-error')?.textContent).toContain(
      'dentro de la temporada 2026-27',
    );
    expect(page.querySelector('form .alert')).toBeNull();
  });

  it('cambia la fecha de una jornada en la fila', async () => {
    const { fixture, page } = await render([J1]);

    (page.querySelector('tbody .panel__actions .link-button') as HTMLButtonElement).click();
    await fixture.whenStable();
    const date = page.querySelector('#edit-date-10') as HTMLInputElement;
    date.value = '2026-10-10';
    date.dispatchEvent(new Event('input'));
    (page.querySelector('.inline-form') as HTMLFormElement).dispatchEvent(new Event('submit'));

    const update = http.expectOne({ method: 'PUT', url: '/api/v1/matchdays/10' });
    expect(update.request.body).toEqual({
      competitionId: 3,
      number: 1,
      scheduledDate: '2026-10-10',
    });
    update.flush({ ...J1, scheduledDate: '2026-10-10' });
    await fixture.whenStable();

    expect(page.querySelector('.inline-form')).toBeNull();
    expect(page.querySelector('tbody tr')?.textContent).toContain('10/10/2026');
  });

  it('borra una jornada tras confirmarlo', async () => {
    const { fixture, page } = await render([J1, J2]);

    (page.querySelector('tbody .link-button--danger') as HTMLButtonElement).click();
    await fixture.whenStable();
    (page.querySelector('.confirm .button--danger') as HTMLButtonElement).click();
    http.expectOne({ method: 'DELETE', url: '/api/v1/matchdays/10' }).flush(null);
    await fixture.whenStable();

    expect(page.querySelectorAll('tbody tr').length).toBe(1);
  });
});
