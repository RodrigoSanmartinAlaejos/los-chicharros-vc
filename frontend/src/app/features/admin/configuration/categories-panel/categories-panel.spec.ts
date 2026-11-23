import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Category } from '../configuration.models';
import { CategoriesPanel } from './categories-panel';

const URL = '/api/v1/categories';
const CADETE: Category = { id: 6, name: 'Cadete', gender: 'FEMALE', sortOrder: 6 };

describe('CategoriesPanel', () => {
  let http: HttpTestingController;

  async function render() {
    TestBed.configureTestingModule({
      imports: [CategoriesPanel],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    http = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(CategoriesPanel);
    await fixture.whenStable();
    http.expectOne(URL).flush([CADETE]);
    await fixture.whenStable();
    return { fixture, page: fixture.nativeElement as HTMLElement };
  }

  function buttonWith(page: HTMLElement, text: string) {
    return Array.from(page.querySelectorAll('button')).find((b) =>
      b.textContent?.trim().startsWith(text),
    ) as HTMLButtonElement;
  }

  afterEach(() => http.verify());

  it('muestra el sexo en español', async () => {
    const { page } = await render();

    expect(page.querySelector('tbody tr')?.textContent).toContain('Femenino');
  });

  it('pide confirmación antes de borrar y quita la fila', async () => {
    const { fixture, page } = await render();

    buttonWith(page, 'Borrar').click();
    await fixture.whenStable();
    expect(page.textContent).toContain('¿Borrar Cadete femenina?');

    buttonWith(page, 'Sí, borrar').click();
    http
      .expectOne({ method: 'DELETE', url: `${URL}/6` })
      .flush(null, { status: 204, statusText: 'No Content' });
    await fixture.whenStable();

    expect(page.querySelector('tbody')).toBeNull();
    expect(page.querySelector('[aria-live]')?.textContent).toBe(
      'Se ha borrado la categoría Cadete femenina.',
    );
  });

  it('explica por qué no se puede borrar una categoría con equipos', async () => {
    const { fixture, page } = await render();
    buttonWith(page, 'Borrar').click();
    await fixture.whenStable();

    buttonWith(page, 'Sí, borrar').click();
    http
      .expectOne({ method: 'DELETE', url: `${URL}/6` })
      .flush(
        { status: 409, detail: 'No se puede borrar la categoría: tiene equipos' },
        { status: 409, statusText: 'Conflict' },
      );
    await fixture.whenStable();

    expect(page.querySelector('[role="alert"]')?.textContent).toBe(
      'No se puede borrar la categoría: tiene equipos',
    );
    expect(page.querySelectorAll('tbody tr')).toHaveLength(1);
  });

  it('edita una categoría con sus datos cargados en el formulario', async () => {
    const { fixture, page } = await render();
    buttonWith(page, 'Editar').click();
    await fixture.whenStable();

    expect((page.querySelector('#category-name') as HTMLInputElement).value).toBe('Cadete');
    (page.querySelector('form') as HTMLFormElement).dispatchEvent(new Event('submit'));

    const update = http.expectOne({ method: 'PUT', url: `${URL}/6` });
    expect(update.request.body).toEqual({ name: 'Cadete', gender: 'FEMALE', sortOrder: 6 });
    update.flush(
      { status: 409, detail: 'Ya existe una categoría con ese nombre y sexo' },
      { status: 409, statusText: 'Conflict' },
    );
    await fixture.whenStable();

    expect(page.querySelector('form .alert')?.textContent).toBe(
      'Ya existe una categoría con ese nombre y sexo',
    );
  });
});
