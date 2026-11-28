import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Competition } from '../competitions/competitions.models';
import { Category, Season, Venue } from '../configuration/configuration.models';
import { TeamSummary } from '../teams/teams.models';
import { Matches } from './matches';
import { Match, MatchdaySchedule } from './matches.models';

const SEASONS: Season[] = [
  { id: 2, name: '2026-27', startDate: '2026-09-01', endDate: '2027-06-30', active: true },
];
const VENUES: Venue[] = [
  {
    id: 1,
    name: 'Pabellón del Puerto',
    address: 'Avenida Marítima, 12',
    latitude: null,
    longitude: null,
  },
];
const CADETE_F: Category = { id: 6, name: 'Cadete', gender: 'FEMALE', sortOrder: 6 };
const LIGA: Competition = {
  id: 3,
  seasonId: 2,
  categoryId: 6,
  name: 'Liga Insular Cadete femenina',
  federation: null,
  forfeitPenaltyPoints: 1,
};
const OWN: TeamSummary = {
  id: 5,
  name: 'Los Chicharros V.C.',
  ownClub: true,
  crestUrl: null,
  category: CADETE_F,
  seasonId: 2,
};
const RIVAL: TeamSummary = { ...OWN, id: 6, name: 'Roque Vóley', ownClub: false };
const MATCH: Match = {
  id: 20,
  matchdayId: 10,
  matchdayNumber: 1,
  competitionId: 3,
  homeTeam: OWN,
  awayTeam: RIVAL,
  venue: VENUES[0],
  startsAt: '2026-10-03T09:30:00Z',
  status: 'SCHEDULED',
  homeSets: null,
  awaySets: null,
};
const J1: MatchdaySchedule = {
  id: 10,
  competitionId: 3,
  number: 1,
  scheduledDate: '2026-10-03',
  matches: [MATCH],
};
const J2: MatchdaySchedule = { ...J1, id: 11, number: 2, scheduledDate: '2026-10-17', matches: [] };

describe('Matches', () => {
  let http: HttpTestingController;

  async function render(schedule: MatchdaySchedule[]) {
    TestBed.configureTestingModule({
      imports: [Matches],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    http = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(Matches);
    await fixture.whenStable();
    http.expectOne('/api/v1/seasons').flush(SEASONS);
    http.expectOne('/api/v1/venues').flush(VENUES);
    http.expectOne((request) => request.url === '/api/v1/competitions').flush([LIGA]);
    await fixture.whenStable();
    flushSchedule(schedule);
    await fixture.whenStable();
    return { fixture, page: fixture.nativeElement as HTMLElement };
  }

  function flushSchedule(schedule: MatchdaySchedule[]) {
    http.expectOne('/api/v1/competitions/3/matchdays').flush(schedule);
    http
      .expectOne(
        (request) =>
          request.url === '/api/v1/teams' &&
          request.params.get('seasonId') === '2' &&
          request.params.get('categoryId') === '6',
      )
      .flush([OWN, RIVAL]);
  }

  function select(page: HTMLElement, selector: string, index: number) {
    const element = page.querySelector(selector) as HTMLSelectElement;
    element.value = element.options[index].value;
    element.dispatchEvent(new Event('change'));
  }

  afterEach(() => http.verify());

  it('muestra cada jornada con sus partidos en la hora de Canarias', async () => {
    const { page } = await render([J1, J2]);

    const headings = Array.from(page.querySelectorAll('.matchday h2')).map((h) => h.textContent);
    expect(headings).toEqual(['Jornada 1 · 03/10/2026', 'Jornada 2 · 17/10/2026']);
    const row = page.querySelector('tbody tr') as HTMLElement;
    // 09:30 UTC son las 10:30 en Canarias en octubre
    expect(row.textContent).toContain('10:30');
    expect(row.textContent).toContain('Los Chicharros V.C.');
    expect(row.textContent).toContain('Pabellón del Puerto');
    expect(row.querySelector('.badge')?.textContent).toContain('Programado');
    expect(page.textContent).toContain('Todavía no hay partidos en esta jornada');
  });

  it('crea un partido en una jornada con la hora de Canarias', async () => {
    const { fixture, page } = await render([J1, J2]);

    (page.querySelectorAll('.matchday .panel__header .button')[1] as HTMLButtonElement).click();
    await fixture.whenStable();
    expect((page.querySelector('#match-starts-at') as HTMLInputElement).value).toBe(
      '2026-10-17T10:00',
    );
    select(page, '#match-home', 2);
    select(page, '#match-away', 1);
    (page.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));

    const create = http.expectOne({ method: 'POST', url: '/api/v1/matches' });
    expect(create.request.body).toEqual({
      matchdayId: 11,
      homeTeamId: 6,
      awayTeamId: 5,
      startsAt: '2026-10-17T09:00:00.000Z',
      venueId: null,
    });
    create.flush({ ...MATCH, id: 21, matchdayId: 11, homeTeam: RIVAL, awayTeam: OWN });
    flushSchedule([J1, J2]);
    await fixture.whenStable();

    expect(page.querySelector('form')).toBeNull();
  });

  it('no deja poner el mismo equipo de local y de visitante', async () => {
    const { fixture, page } = await render([J1]);

    (page.querySelector('.matchday .panel__header .button') as HTMLButtonElement).click();
    await fixture.whenStable();
    select(page, '#match-home', 1);
    select(page, '#match-away', 1);
    (page.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));
    await fixture.whenStable();

    expect(page.querySelector('#match-away-error')?.textContent).toContain('distinto del local');
  });

  it('aplaza un partido y deja volver a programarlo', async () => {
    const { fixture, page } = await render([J1]);

    const aplazar = Array.from(page.querySelectorAll('tbody .link-button')).find((button) =>
      button.textContent?.trim().startsWith('Aplazar'),
    ) as HTMLButtonElement;
    aplazar.click();
    const postpone = http.expectOne({ method: 'PATCH', url: '/api/v1/matches/20' });
    expect(postpone.request.body).toEqual({ status: 'POSTPONED' });
    postpone.flush({ ...MATCH, status: 'POSTPONED' });
    flushSchedule([{ ...J1, matches: [{ ...MATCH, status: 'POSTPONED' }] }]);
    await fixture.whenStable();

    expect(page.querySelector('tbody .badge')?.textContent).toContain('Aplazado');
    const reprogramar = Array.from(page.querySelectorAll('tbody .link-button')).find((button) =>
      button.textContent?.trim().startsWith('Reprogramar'),
    ) as HTMLButtonElement;
    reprogramar.click();
    await fixture.whenStable();
    const date = page.querySelector('#reschedule-20') as HTMLInputElement;
    date.value = '2026-10-21T19:00';
    date.dispatchEvent(new Event('input'));
    (page.querySelector('.inline-form') as HTMLFormElement).dispatchEvent(new Event('submit'));

    const reschedule = http.expectOne({ method: 'PATCH', url: '/api/v1/matches/20' });
    expect(reschedule.request.body).toEqual({
      status: 'SCHEDULED',
      startsAt: '2026-10-21T18:00:00.000Z',
    });
    reschedule.flush(MATCH);
    flushSchedule([J1]);
    await fixture.whenStable();
  });

  it('cancela un partido tras confirmarlo', async () => {
    const { fixture, page } = await render([J1]);

    const cancelar = Array.from(page.querySelectorAll('tbody .link-button')).find((button) =>
      button.textContent?.trim().startsWith('Cancelar'),
    ) as HTMLButtonElement;
    cancelar.click();
    await fixture.whenStable();
    (page.querySelector('.confirm .button--danger') as HTMLButtonElement).click();

    const cancel = http.expectOne({ method: 'PATCH', url: '/api/v1/matches/20' });
    expect(cancel.request.body).toEqual({ status: 'CANCELLED' });
    cancel.flush({ ...MATCH, status: 'CANCELLED' });
    flushSchedule([{ ...J1, matches: [{ ...MATCH, status: 'CANCELLED' }] }]);
    await fixture.whenStable();

    expect(page.querySelector('tbody .badge')?.classList).toContain('status--cancelled');
  });

  it('avisa si la competición no tiene jornadas', async () => {
    const { page } = await render([]);

    expect(page.textContent).toContain('todavía no tiene jornadas');
  });
});
