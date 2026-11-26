import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Category, Season } from '../configuration/configuration.models';
import { Teams } from './teams';
import { Team, TeamSummary } from './teams.models';

const SEASONS: Season[] = [
  { id: 1, name: '2025-26', startDate: '2025-09-01', endDate: '2026-06-30', active: false },
  { id: 2, name: '2026-27', startDate: '2026-09-01', endDate: '2027-06-30', active: true },
];
const CADETE_F: Category = { id: 6, name: 'Cadete', gender: 'FEMALE', sortOrder: 6 };
const OWN: TeamSummary = {
  id: 5,
  name: 'Los Chicharros V.C.',
  ownClub: true,
  crestUrl: null,
  category: CADETE_F,
  seasonId: 2,
};
const RIVAL: TeamSummary = { ...OWN, id: 6, name: 'Roque Vóley', ownClub: false };

describe('Teams', () => {
  let http: HttpTestingController;

  async function render() {
    TestBed.configureTestingModule({
      imports: [Teams],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    http = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(Teams);
    await fixture.whenStable();
    http.expectOne('/api/v1/seasons').flush(SEASONS);
    http.expectOne('/api/v1/categories').flush([CADETE_F]);
    await fixture.whenStable();
    return { fixture, page: fixture.nativeElement as HTMLElement };
  }

  function expectTeams(seasonId: number, categoryId: number | null) {
    return http.expectOne(
      (request) =>
        request.url === '/api/v1/teams' &&
        request.params.get('seasonId') === String(seasonId) &&
        request.params.get('categoryId') === (categoryId === null ? null : String(categoryId)),
    );
  }

  afterEach(() => http.verify());

  it('muestra los equipos de la temporada activa y distingue los del club', async () => {
    const { fixture, page } = await render();
    expectTeams(2, null).flush([OWN, RIVAL]);
    await fixture.whenStable();

    expect(
      (page.querySelector('#teams-season') as HTMLSelectElement).selectedOptions[0].text,
    ).toContain('2026-27 (activa)');
    const rows = page.querySelectorAll('tbody tr');
    expect(rows[0].textContent).toContain('Cadete femenina');
    expect(rows[0].querySelector('.badge')?.textContent).toBe('Del club');
    expect(rows[1].textContent).toContain('Rival');
  });

  it('filtra por categoría', async () => {
    const { fixture, page } = await render();
    expectTeams(2, null).flush([OWN, RIVAL]);
    await fixture.whenStable();

    const select = page.querySelector('#teams-category') as HTMLSelectElement;
    select.value = select.options[1].value;
    select.dispatchEvent(new Event('change'));
    expectTeams(2, 6).flush([OWN]);
    await fixture.whenStable();

    expect(page.querySelectorAll('tbody tr').length).toBe(1);
  });

  it('crea un equipo del club en la temporada y la categoría elegidas', async () => {
    const { fixture, page } = await render();
    expectTeams(2, null).flush([]);
    await fixture.whenStable();
    expect(page.textContent).toContain('No hay equipos con estos filtros');

    (page.querySelector('.panel__header .button--primary') as HTMLButtonElement).click();
    await fixture.whenStable();
    const name = page.querySelector('#team-name') as HTMLInputElement;
    name.value = 'Los Chicharros V.C.';
    name.dispatchEvent(new Event('input'));
    const category = page.querySelector('#team-category') as HTMLSelectElement;
    category.value = category.options[0].value;
    category.dispatchEvent(new Event('change'));
    (page.querySelector('#team-own-club') as HTMLInputElement).click();
    (page.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));

    const create = http.expectOne({ method: 'POST', url: '/api/v1/teams' });
    expect(create.request.body).toEqual({
      name: 'Los Chicharros V.C.',
      seasonId: 2,
      categoryId: 6,
      ownClub: true,
    });
    create.flush({ ...OWN, players: [], coaches: [] } satisfies Team);
    expectTeams(2, null).flush([OWN]);
    await fixture.whenStable();

    expect(page.querySelector('form')).toBeNull();
    expect(page.querySelector('tbody tr')?.textContent).toContain('Los Chicharros V.C.');
  });

  it('abre la plantilla del equipo', async () => {
    const { fixture, page } = await render();
    expectTeams(2, null).flush([OWN]);
    await fixture.whenStable();

    (page.querySelector('.panel__actions .link-button') as HTMLButtonElement).click();
    await fixture.whenStable();
    http.expectOne('/api/v1/teams/5').flush({ ...OWN, players: [], coaches: [] } satisfies Team);
    await fixture.whenStable();

    expect(page.querySelector('#roster-title')?.textContent).toContain(
      'Los Chicharros V.C. · Cadete femenina · 2026-27',
    );
  });
});
