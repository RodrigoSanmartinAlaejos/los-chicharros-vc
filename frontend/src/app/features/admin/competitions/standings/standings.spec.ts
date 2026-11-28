import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { Category } from '../../configuration/configuration.models';
import { TeamSummary } from '../../teams/teams.models';
import { Competition, Standing } from '../competitions.models';
import { Standings } from './standings';

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
const ROQUE: TeamSummary = { ...OWN, id: 6, name: 'Roque Vóley', ownClub: false };
const TEIDE: TeamSummary = { ...OWN, id: 7, name: 'Teide Vóley', ownClub: false };

function standing(position: number, team: TeamSummary, won: number, lost: number): Standing {
  return {
    position,
    team,
    played: won + lost,
    won,
    lost,
    setsWon: won * 3,
    setsLost: lost * 3,
    penaltyPoints: 0,
    points: won * 3,
  };
}

describe('Standings', () => {
  let http: HttpTestingController;

  async function render(standings: Standing[]) {
    TestBed.configureTestingModule({
      imports: [Standings],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id: '3' }) } },
        },
      ],
    });
    http = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(Standings);
    await fixture.whenStable();
    http.expectOne('/api/v1/competitions/3').flush(LIGA);
    await fixture.whenStable();
    http.expectOne('/api/v1/competitions/3/standings').flush(standings);
    http
      .expectOne(
        (request) =>
          request.url === '/api/v1/teams' &&
          request.params.get('seasonId') === '2' &&
          request.params.get('categoryId') === '6',
      )
      .flush([OWN, ROQUE, TEIDE]);
    await fixture.whenStable();
    fixture.detectChanges();
    return { fixture, page: fixture.nativeElement as HTMLElement };
  }

  function type(input: HTMLInputElement, value: string) {
    input.value = value;
    input.dispatchEvent(new Event('input'));
  }

  function cells(page: HTMLElement, row: number): HTMLInputElement[] {
    return Array.from(
      page.querySelectorAll('tbody tr:not(.row-error)')[row].querySelectorAll('input'),
    );
  }

  afterEach(() => http.verify());

  it('pone primero los equipos clasificados y después los demás a cero', async () => {
    const { page } = await render([standing(1, ROQUE, 2, 0), standing(2, OWN, 1, 1)]);

    const names = Array.from(page.querySelectorAll('tbody th')).map((th) => th.textContent);
    expect(names).toEqual(['Roque Vóley', 'Los Chicharros V.C.', 'Teide Vóley']);
    const positions = Array.from(page.querySelectorAll('tbody tr td:first-child')).map((td) =>
      td.textContent?.trim(),
    );
    expect(positions).toEqual(['1', '2', '—']);
    expect(cells(page, 1).map((input) => input.value)).toEqual(['2', '1', '1', '3', '3', '0', '3']);
    expect(cells(page, 2).every((input) => input.value === '0')).toBe(true);
  });

  it('guarda todas las filas y las reordena con lo que devuelve la API', async () => {
    const { fixture, page } = await render([]);

    const teide = cells(page, 2);
    type(teide[0], '1');
    type(teide[1], '1');
    type(teide[3], '3');
    type(teide[6], '3');
    (page.querySelector('button[type="submit"]') as HTMLButtonElement).click();

    const put = http.expectOne('/api/v1/competitions/3/standings');
    expect(put.request.method).toBe('PUT');
    expect(put.request.body).toHaveLength(3);
    expect(put.request.body[2]).toEqual({
      teamId: 7,
      played: 1,
      won: 1,
      lost: 0,
      setsWon: 3,
      setsLost: 0,
      penaltyPoints: 0,
      points: 3,
    });
    put.flush([
      standing(1, TEIDE, 1, 0),
      { ...standing(2, OWN, 0, 0) },
      { ...standing(3, ROQUE, 0, 0) },
    ]);
    await fixture.whenStable();
    fixture.detectChanges();

    const names = Array.from(page.querySelectorAll('tbody th')).map((th) => th.textContent);
    expect(names).toEqual(['Teide Vóley', 'Los Chicharros V.C.', 'Roque Vóley']);
    expect(page.textContent).toContain('Se ha guardado la clasificación.');
  });

  it('no envía filas cuyos ganados y perdidos no suman los jugados', async () => {
    const { fixture, page } = await render([]);

    type(cells(page, 0)[0], '2');
    (page.querySelector('button[type="submit"]') as HTMLButtonElement).click();
    await fixture.whenStable();
    fixture.detectChanges();

    http.expectNone('/api/v1/competitions/3/standings');
    expect(page.querySelector('.alert')?.textContent).toContain('Revisa las filas marcadas');
    expect(page.querySelector('.row-error')?.textContent).toContain(
      'Los Chicharros V.C.: Los ganados y los perdidos tienen que sumar los jugados',
    );
  });

  it('lleva el error de la API a la fila y el campo que toca', async () => {
    const { fixture, page } = await render([]);

    (page.querySelector('button[type="submit"]') as HTMLButtonElement).click();
    http.expectOne('/api/v1/competitions/3/standings').flush(
      {
        status: 400,
        detail: 'Hay datos que no son válidos',
        errors: [{ field: '[1].setsWon', message: 'No puede ser negativo' }],
      },
      { status: 400, statusText: 'Bad Request' },
    );
    await fixture.whenStable();
    fixture.detectChanges();

    expect(cells(page, 1)[3].getAttribute('aria-invalid')).toBe('true');
    expect(cells(page, 0)[3].getAttribute('aria-invalid')).toBeNull();
    expect(page.querySelector('.alert')?.textContent).toContain('Revisa las filas marcadas');
  });

  it('avisa si la competición no existe', async () => {
    TestBed.configureTestingModule({
      imports: [Standings],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id: '99' }) } },
        },
      ],
    });
    http = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(Standings);
    await fixture.whenStable();
    http
      .expectOne('/api/v1/competitions/99')
      .flush({ status: 404 }, { status: 404, statusText: 'Not Found' });
    await fixture.whenStable();
    fixture.detectChanges();

    expect((fixture.nativeElement as HTMLElement).textContent).toContain(
      'Esta competición no existe.',
    );
  });
});
