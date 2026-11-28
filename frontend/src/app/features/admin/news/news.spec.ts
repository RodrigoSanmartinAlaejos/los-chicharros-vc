import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Page } from '../../../shared/admin/page';
import { NewsList } from './news';
import { NewsSummary } from './news.models';

const PUBLISHED: NewsSummary = {
  id: 1,
  title: 'Las cadetes remontan ante el CV Alisios',
  slug: 'las-cadetes-remontan-ante-el-cv-alisios',
  summary: 'Ganaron 3-2.',
  imageUrl: null,
  type: 'MATCH_REPORT',
  featured: true,
  publishedAt: '2026-11-14T10:00:00Z',
};
const DRAFT: NewsSummary = {
  ...PUBLISHED,
  id: 2,
  title: 'Previa: sénior femenino ante Roque Vóley',
  featured: false,
  publishedAt: null,
};
const SCHEDULED: NewsSummary = {
  ...PUBLISHED,
  id: 3,
  title: 'Campus de Navidad',
  type: 'EVENT',
  featured: false,
  publishedAt: '2099-12-01T09:00:00Z',
};

function page(content: NewsSummary[], totalPages = 1): Page<NewsSummary> {
  return { content, page: 0, size: 20, totalElements: content.length, totalPages };
}

describe('NewsList', () => {
  let http: HttpTestingController;

  async function render(first: Page<NewsSummary>) {
    TestBed.configureTestingModule({
      imports: [NewsList],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    });
    http = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(NewsList);
    await fixture.whenStable();
    expectList('all', 0).flush(first);
    await fixture.whenStable();
    return { fixture, page: fixture.nativeElement as HTMLElement };
  }

  function expectList(status: string, pageNumber: number) {
    return http.expectOne(
      (request) =>
        request.url === '/api/v1/news' &&
        request.params.get('status') === status &&
        request.params.get('page') === String(pageNumber),
    );
  }

  afterEach(() => http.verify());

  it('muestra todas las noticias con su tipo, su estado y su fecha', async () => {
    const { page: view } = await render(page([PUBLISHED, DRAFT, SCHEDULED]));

    const rows = Array.from(view.querySelectorAll('tbody tr')).map((row) =>
      Array.from(row.querySelectorAll('td'))
        .slice(0, 4)
        .map((cell) => cell.textContent?.replace(/\s+/g, ' ').trim()),
    );
    expect(rows).toEqual([
      [
        'Las cadetes remontan ante el CV Alisios★ (destacada)',
        'Crónica',
        'Publicada',
        '14/11/2026',
      ],
      ['Previa: sénior femenino ante Roque Vóley', 'Crónica', 'Borrador', '—'],
      ['Campus de Navidad', 'Evento', 'Programada', '01/12/2099'],
    ]);
    expect(view.querySelector('tbody tr a')?.getAttribute('href')).toBe('/1');
    expect(view.querySelector('a.button--primary')?.getAttribute('href')).toBe('/nueva');
  });

  it('filtra por publicadas y por borradores', async () => {
    const { fixture, page: view } = await render(page([PUBLISHED, DRAFT]));

    const [, published, drafts] = Array.from(view.querySelectorAll<HTMLButtonElement>('.chip'));
    drafts.click();
    expectList('draft', 0).flush(page([]));
    await fixture.whenStable();
    expect(drafts.getAttribute('aria-pressed')).toBe('true');
    expect(view.querySelector('.state')?.textContent).toContain('No hay borradores.');

    published.click();
    expectList('published', 0).flush(page([PUBLISHED]));
    await fixture.whenStable();
    expect(view.querySelectorAll('tbody tr')).toHaveLength(1);
  });

  it('pasa de página', async () => {
    const { fixture, page: view } = await render(page([PUBLISHED], 2));

    view.querySelectorAll<HTMLButtonElement>('.pager button')[1].click();
    expectList('all', 1).flush({ ...page([DRAFT], 2), page: 1 });
    await fixture.whenStable();
    expect(view.querySelector('.pager span')?.textContent).toBe('Página 2 de 2');
  });

  it('avisa si no se puede cargar y deja reintentar', async () => {
    TestBed.configureTestingModule({
      imports: [NewsList],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    });
    http = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(NewsList);
    await fixture.whenStable();
    expectList('all', 0).flush(null, { status: 500, statusText: 'Error' });
    await fixture.whenStable();
    const view = fixture.nativeElement as HTMLElement;
    expect(view.textContent).toContain('No se han podido cargar las noticias.');

    (view.querySelector('.state button') as HTMLButtonElement).click();
    expectList('all', 0).flush(page([]));
    await fixture.whenStable();
    expect(view.textContent).toContain('Todavía no hay noticias.');
  });
});
