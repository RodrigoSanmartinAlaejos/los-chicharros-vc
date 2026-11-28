import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Category, Season } from '../configuration/configuration.models';
import { Competitions } from './competitions';
import { Competition } from './competitions.models';

const SEASONS: Season[] = [
  { id: 1, name: '2025-26', startDate: '2025-09-01', endDate: '2026-06-30', active: false },
  { id: 2, name: '2026-27', startDate: '2026-09-01', endDate: '2027-06-30', active: true },
];
const CADETE_F: Category = { id: 6, name: 'Cadete', gender: 'FEMALE', sortOrder: 6 };
const LIGA: Competition = {
  id: 3,
  seasonId: 2,
  categoryId: 6,
  name: 'Liga Insular Cadete femenina',
  federation: 'Federación Isleña de Voleibol',
  forfeitPenaltyPoints: 1,
};

describe('Competitions', () => {
  let http: HttpTestingController;

  async function render() {
    TestBed.configureTestingModule({
      imports: [Competitions],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    http = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(Competitions);
    await fixture.whenStable();
    http.expectOne('/api/v1/seasons').flush(SEASONS);
    http.expectOne('/api/v1/categories').flush([CADETE_F]);
    await fixture.whenStable();
    return { fixture, page: fixture.nativeElement as HTMLElement };
  }

  function expectCompetitions(seasonId: number, categoryId: number | null) {
    return http.expectOne(
      (request) =>
        request.url === '/api/v1/competitions' &&
        request.params.get('seasonId') === String(seasonId) &&
        request.params.get('categoryId') === (categoryId === null ? null : String(categoryId)),
    );
  }

  afterEach(() => http.verify());

  it('muestra las competiciones de la temporada activa con su categoría y su sanción', async () => {
    const { fixture, page } = await render();
    expectCompetitions(2, null).flush([
      LIGA,
      { ...LIGA, id: 4, name: 'Copa', federation: null, forfeitPenaltyPoints: 2 },
    ]);
    await fixture.whenStable();

    const rows = page.querySelectorAll('tbody tr');
    expect(rows[0].textContent).toContain('Federación Isleña de Voleibol');
    expect(rows[0].textContent).toContain('Cadete femenina');
    expect(rows[0].textContent).toContain('1 punto');
    expect(rows[1].textContent).toContain('2 puntos');
  });

  it('crea una competición con sus puntos de sanción', async () => {
    const { fixture, page } = await render();
    expectCompetitions(2, null).flush([]);
    await fixture.whenStable();
    expect(page.textContent).toContain('No hay competiciones con estos filtros');

    (page.querySelector('.panel__header .button--primary') as HTMLButtonElement).click();
    await fixture.whenStable();
    const name = page.querySelector('#competition-name') as HTMLInputElement;
    name.value = ' Liga Insular Cadete femenina ';
    name.dispatchEvent(new Event('input'));
    const category = page.querySelector('#competition-category') as HTMLSelectElement;
    category.value = category.options[0].value;
    category.dispatchEvent(new Event('change'));
    const penalty = page.querySelector('#competition-penalty') as HTMLInputElement;
    penalty.value = '2';
    penalty.dispatchEvent(new Event('input'));
    (page.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));

    const create = http.expectOne({ method: 'POST', url: '/api/v1/competitions' });
    expect(create.request.body).toEqual({
      name: 'Liga Insular Cadete femenina',
      seasonId: 2,
      categoryId: 6,
      federation: null,
      forfeitPenaltyPoints: 2,
    });
    create.flush({ ...LIGA, forfeitPenaltyPoints: 2 });
    expectCompetitions(2, null).flush([{ ...LIGA, forfeitPenaltyPoints: 2 }]);
    await fixture.whenStable();

    expect(page.querySelector('form')).toBeNull();
    expect(page.querySelector('tbody tr')?.textContent).toContain('Liga Insular Cadete femenina');
  });

  it('no deja guardar más de 10 puntos de sanción', async () => {
    const { fixture, page } = await render();
    expectCompetitions(2, null).flush([]);
    await fixture.whenStable();

    (page.querySelector('.panel__header .button--primary') as HTMLButtonElement).click();
    await fixture.whenStable();
    const penalty = page.querySelector('#competition-penalty') as HTMLInputElement;
    penalty.value = '11';
    penalty.dispatchEvent(new Event('input'));
    (page.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));
    await fixture.whenStable();

    expect(page.querySelector('#competition-penalty-error')?.textContent).toContain(
      'Debe ser 10 o menos',
    );
    expect(page.querySelector('#competition-name-error')?.textContent).toContain('obligatorio');
  });

  it('muestra el 409 al borrar una competición con partidos', async () => {
    const { fixture, page } = await render();
    expectCompetitions(2, null).flush([LIGA]);
    await fixture.whenStable();

    (page.querySelector('.link-button--danger') as HTMLButtonElement).click();
    await fixture.whenStable();
    (page.querySelector('.confirm .button--danger') as HTMLButtonElement).click();
    http.expectOne({ method: 'DELETE', url: '/api/v1/competitions/3' }).flush(
      {
        detail: 'No se puede borrar la competición Liga Insular Cadete femenina: tiene partidos',
      },
      { status: 409, statusText: 'Conflict' },
    );
    await fixture.whenStable();

    expect(page.querySelector('[role=alert]')?.textContent).toContain('tiene partidos');
    expect(page.querySelectorAll('tbody tr').length).toBe(1);
  });

  it('abre las jornadas de la competición', async () => {
    const { fixture, page } = await render();
    expectCompetitions(2, null).flush([LIGA]);
    await fixture.whenStable();

    (page.querySelector('.panel__actions .link-button') as HTMLButtonElement).click();
    await fixture.whenStable();
    http.expectOne('/api/v1/competitions/3/matchdays').flush([]);
    await fixture.whenStable();

    expect(page.querySelector('#matchdays-title')?.textContent).toContain(
      'Jornadas: Liga Insular Cadete femenina · 2026-27',
    );
  });
});
