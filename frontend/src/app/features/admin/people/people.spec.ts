import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  TestRequest,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Page } from '../../../shared/admin/page';
import { People } from './people';
import { Person } from './people.models';

const URL = '/api/v1/people';
const LUCIA: Person = {
  id: 1,
  firstName: 'Lucía',
  lastName: 'Marrero Cabrera',
  birthDate: '2011-03-14',
  imageConsent: true,
  photoUrl: null,
};
const COACH: Person = {
  id: 2,
  firstName: 'Carmen',
  lastName: 'Lucas Pérez',
  birthDate: null,
  imageConsent: false,
  photoUrl: null,
};

function pageOf(content: Person[], page = 0, totalElements = content.length): Page<Person> {
  return { content, page, size: 20, totalElements, totalPages: Math.ceil(totalElements / 20) };
}

describe('People', () => {
  let http: HttpTestingController;

  async function render() {
    TestBed.configureTestingModule({
      imports: [People],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    http = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(People);
    await fixture.whenStable();
    return { fixture, page: fixture.nativeElement as HTMLElement };
  }

  /** La petición del listado con esa búsqueda y esa página. */
  function expectSearch(search: string | null, page: number): TestRequest {
    return http.expectOne(
      (request) =>
        request.method === 'GET' &&
        request.url === URL &&
        request.params.get('search') === search &&
        request.params.get('page') === String(page) &&
        request.params.get('size') === '20',
    );
  }

  function type(page: HTMLElement, selector: string, value: string) {
    const input = page.querySelector(selector) as HTMLInputElement;
    input.value = value;
    input.dispatchEvent(new Event('input'));
  }

  function button(page: HTMLElement, text: string): HTMLButtonElement {
    const found = Array.from(page.querySelectorAll('button')).find(
      (b) => b.textContent?.trim() === text,
    );
    if (!found) throw new Error(`No hay botón «${text}»`);
    return found;
  }

  afterEach(() => http.verify());

  it('muestra las personas con su fecha y su consentimiento', async () => {
    const { fixture, page } = await render();
    expect(page.textContent).toContain('Cargando personas');

    expectSearch(null, 0).flush(pageOf([COACH, LUCIA]));
    await fixture.whenStable();

    const rows = page.querySelectorAll('tbody tr');
    expect(page.querySelector('.count')?.textContent).toContain('2 personas');
    expect(rows[0].textContent).toContain('Lucas Pérez, Carmen');
    expect(rows[0].textContent).toContain('—');
    expect(rows[0].textContent).toContain('Sin consentimiento');
    expect(rows[1].textContent).toContain('14/03/2011');
    expect(rows[1].querySelector('.badge')?.textContent).toBe('Con consentimiento');
    expect(page.querySelector('.pager')).toBeNull();
  });

  it('busca al escribir, sin esperar a que termine de teclear cada letra', async () => {
    const { fixture, page } = await render();
    expectSearch(null, 0).flush(pageOf([COACH, LUCIA]));
    await fixture.whenStable();

    type(page, '#people-search', 'l');
    type(page, '#people-search', ' luc ');
    await new Promise((resolve) => setTimeout(resolve, 350));
    expectSearch('luc', 0).flush(pageOf([]));
    await fixture.whenStable();

    expect(page.textContent).toContain('Ninguna persona coincide con «luc»');
  });

  it('pasa de página', async () => {
    const { fixture, page } = await render();
    expectSearch(null, 0).flush(pageOf([LUCIA], 0, 45));
    await fixture.whenStable();
    expect(page.querySelector('.pager')?.textContent).toContain('Página 1 de 3');
    expect(button(page, 'Anterior').disabled).toBe(true);

    button(page, 'Siguiente').click();
    expectSearch(null, 1).flush(pageOf([COACH], 1, 45));
    await fixture.whenStable();

    expect(page.querySelector('.pager')?.textContent).toContain('Página 2 de 3');
    expect(page.querySelector('tbody tr')?.textContent).toContain('Lucas Pérez');
  });

  it('da de alta una persona sin fecha de nacimiento y recarga la lista', async () => {
    const { fixture, page } = await render();
    expectSearch(null, 0).flush(pageOf([]));
    await fixture.whenStable();
    expect(page.textContent).toContain('Todavía no hay personas');

    button(page, '+ Nueva persona').click();
    await fixture.whenStable();
    type(page, '#person-first-name', ' Carmen ');
    type(page, '#person-last-name', 'Lucas Pérez');
    (page.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));

    const create = http.expectOne({ method: 'POST', url: URL });
    expect(create.request.body).toEqual({
      firstName: 'Carmen',
      lastName: 'Lucas Pérez',
      birthDate: null,
      imageConsent: false,
    });
    create.flush(COACH);
    expectSearch(null, 0).flush(pageOf([COACH]));
    await fixture.whenStable();

    expect(page.querySelector('form')).toBeNull();
    expect(page.querySelector('tbody tr')?.textContent).toContain('Lucas Pérez');
  });

  it('sube la foto de una persona al editarla y la muestra en la lista', async () => {
    const { fixture, page } = await render();
    expectSearch(null, 0).flush(pageOf([LUCIA]));
    await fixture.whenStable();
    expect(page.querySelector('.avatar')).toBeNull();

    button(page, 'Editar a Lucía Marrero Cabrera').click();
    await fixture.whenStable();
    const input = page.querySelector('form input[type=file]') as HTMLInputElement;
    const file = new File(['jpg'], 'lucia.jpg', { type: 'image/jpeg' });
    Object.defineProperty(input, 'files', { value: [file] });
    input.dispatchEvent(new Event('change'));

    const upload = http.expectOne({ method: 'PUT', url: `${URL}/1/photo` });
    expect((upload.request.body as FormData).get('file')).toBe(file);
    const withPhoto = { ...LUCIA, photoUrl: '/api/images/people/x-persona-1.jpg' };
    upload.flush(withPhoto);
    expectSearch(null, 0).flush(pageOf([withPhoto]));
    await fixture.whenStable();

    expect(page.querySelector('form .picker__preview')?.getAttribute('src')).toBe(
      withPhoto.photoUrl,
    );
    expect(page.querySelector('tbody .avatar')?.getAttribute('src')).toBe(withPhoto.photoUrl);
  });

  it('no deja guardar una fecha de nacimiento futura', async () => {
    const { fixture, page } = await render();
    expectSearch(null, 0).flush(pageOf([]));
    await fixture.whenStable();

    button(page, '+ Nueva persona').click();
    await fixture.whenStable();
    type(page, '#person-first-name', 'Lucía');
    type(page, '#person-last-name', 'Marrero');
    type(page, '#person-birth-date', '2999-01-01');
    (page.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));
    await fixture.whenStable();

    expect(page.querySelector('#person-birth-date-error')?.textContent).toBe(
      'No puede ser una fecha futura',
    );
  });

  it('borra una persona tras confirmarlo', async () => {
    const { fixture, page } = await render();
    expectSearch(null, 0).flush(pageOf([LUCIA]));
    await fixture.whenStable();

    (page.querySelector('.link-button--danger') as HTMLButtonElement).click();
    await fixture.whenStable();
    expect(page.querySelector('.confirm')?.textContent).toContain(
      '¿Borrar a Lucía Marrero Cabrera?',
    );
    button(page, 'Sí, borrar').click();

    http.expectOne({ method: 'DELETE', url: `${URL}/1` }).flush(null);
    expectSearch(null, 0).flush(pageOf([]));
    await fixture.whenStable();

    expect(page.textContent).toContain('Todavía no hay personas');
  });
});
