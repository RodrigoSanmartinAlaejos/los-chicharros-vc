import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Person } from '../../people/people.models';
import { Membership, Team } from '../teams.models';
import { Roster } from './roster';

const URL = '/api/v1/teams/5';
const LUCIA: Membership = {
  id: 11,
  person: { id: 1, displayName: 'Lucía M.', photoUrl: null },
  type: 'PLAYER',
  shirtNumber: 7,
  position: 'colocador',
};
const COACH: Membership = {
  id: 12,
  person: { id: 2, displayName: 'Carmen L.', photoUrl: null },
  type: 'COACH',
  shirtNumber: null,
  position: null,
};
const TEAM: Team = {
  id: 5,
  name: 'Los Chicharros V.C.',
  ownClub: true,
  crestUrl: null,
  category: { id: 6, name: 'Cadete', gender: 'FEMALE', sortOrder: 6 },
  seasonId: 2,
  players: [LUCIA],
  coaches: [COACH],
};
const TANIA: Person = {
  id: 3,
  firstName: 'Tania',
  lastName: 'Mesa Ramos',
  birthDate: '2011-05-16',
  imageConsent: true,
  photoUrl: null,
};

describe('Roster', () => {
  let http: HttpTestingController;

  async function render() {
    TestBed.configureTestingModule({
      imports: [Roster],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    http = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(Roster);
    fixture.componentRef.setInput('teamId', 5);
    fixture.componentRef.setInput('title', 'Los Chicharros V.C.');
    await fixture.whenStable();
    http.expectOne(URL).flush(TEAM);
    await fixture.whenStable();
    return { fixture, page: fixture.nativeElement as HTMLElement };
  }

  function button(page: HTMLElement, text: string): HTMLButtonElement {
    const found = Array.from(page.querySelectorAll('button')).find((b) =>
      b.textContent?.trim().startsWith(text),
    );
    if (!found) throw new Error(`No hay botón «${text}»`);
    return found;
  }

  function type(page: HTMLElement, selector: string, value: string) {
    const input = page.querySelector(selector) as HTMLInputElement;
    input.value = value;
    input.dispatchEvent(new Event('input'));
  }

  afterEach(() => http.verify());

  it('muestra los jugadores con dorsal y posición y los entrenadores', async () => {
    const { page } = await render();

    const row = page.querySelector('tbody tr') as HTMLElement;
    expect(row.querySelector('.shirt')?.textContent).toBe('7');
    expect(row.textContent).toContain('Lucía M.');
    expect(row.textContent).toContain('Colocador');
    expect(page.querySelector('.coaches')?.textContent).toContain('Carmen L.');
  });

  it('busca a una persona y la añade como jugadora con dorsal', async () => {
    const { fixture, page } = await render();

    button(page, '+ Añadir').click();
    await fixture.whenStable();
    type(page, '#roster-search', 'tania');
    await new Promise((resolve) => setTimeout(resolve, 350));
    http
      .expectOne(
        (request) => request.url === '/api/v1/people' && request.params.get('search') === 'tania',
      )
      .flush({ content: [TANIA], page: 0, size: 20, totalElements: 1, totalPages: 1 });
    await fixture.whenStable();
    button(page, 'Mesa Ramos, Tania (2011)').click();
    await fixture.whenStable();
    type(page, '#roster-shirt', '4');
    (page.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));

    const add = http.expectOne({ method: 'POST', url: `${URL}/roster` });
    expect(add.request.body).toEqual({
      personId: 3,
      type: 'PLAYER',
      shirtNumber: 4,
      position: null,
    });
    add.flush({ ...LUCIA, id: 13, person: { id: 3, displayName: 'Tania M.', photoUrl: null } });
    http.expectOne(URL).flush(TEAM);
    await fixture.whenStable();

    expect(page.querySelector('form.form--top')).toBeNull();
  });

  it('avisa si el dorsal ya está ocupado', async () => {
    const { fixture, page } = await render();

    button(page, 'Editar').click();
    await fixture.whenStable();
    type(page, '#edit-shirt-11', '9');
    (page.querySelector('.inline-form') as HTMLFormElement).dispatchEvent(new Event('submit'));
    http
      .expectOne({ method: 'PATCH', url: `${URL}/roster/11` })
      .flush(
        { status: 409, detail: 'El dorsal 9 ya lo lleva Tania M.' },
        { status: 409, statusText: 'Conflict' },
      );
    await fixture.whenStable();

    expect(page.querySelector('.inline-form [role="alert"]')?.textContent).toContain(
      'El dorsal 9 ya lo lleva Tania M.',
    );
  });

  it('quita a un entrenador tras confirmarlo', async () => {
    const { fixture, page } = await render();

    (page.querySelector('.coaches .link-button--danger') as HTMLButtonElement).click();
    await fixture.whenStable();
    button(page, 'Sí, quitar').click();
    http.expectOne({ method: 'DELETE', url: `${URL}/roster/12` }).flush(null);
    http.expectOne(URL).flush({ ...TEAM, coaches: [] });
    await fixture.whenStable();

    expect(page.textContent).toContain('Todavía no hay entrenadores');
  });
});
