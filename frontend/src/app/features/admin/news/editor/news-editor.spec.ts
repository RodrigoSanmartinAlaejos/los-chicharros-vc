import { Location } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap, provideRouter } from '@angular/router';
import { News } from '../news.models';
import { NewsEditor } from './news-editor';

const DRAFT: News = {
  id: 7,
  title: 'Previa: sénior femenino ante Roque Vóley',
  slug: 'previa-senior-femenino-ante-roque-voley',
  summary: 'Partido clave para la segunda plaza.',
  imageUrl: null,
  type: 'MATCH_REPORT',
  featured: false,
  publishedAt: null,
  contentHtml: '<p>El <strong>sábado</strong>.</p>',
  contentMarkdown: 'El **sábado**.',
};
const PUBLISHED: News = { ...DRAFT, publishedAt: '2026-11-14T10:00:00Z' };

describe('NewsEditor', () => {
  let http: HttpTestingController;

  async function render(id: string, loaded?: News) {
    TestBed.configureTestingModule({
      imports: [NewsEditor],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id }) } },
        },
      ],
    });
    http = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(NewsEditor);
    await fixture.whenStable();
    if (loaded) {
      http.expectOne(`/api/v1/news/${loaded.id}`).flush(loaded);
      await fixture.whenStable();
    }
    return { fixture, page: fixture.nativeElement as HTMLElement };
  }

  function type(page: HTMLElement, selector: string, value: string) {
    const element = page.querySelector(selector) as HTMLInputElement | HTMLSelectElement;
    element.value = value;
    element.dispatchEvent(new Event(element instanceof HTMLSelectElement ? 'change' : 'input'));
  }

  function fill(page: HTMLElement) {
    type(page, '#news-title', '  Jornada de puertas abiertas ');
    type(page, '#news-summary', 'Ven a probar el voleibol.');
    type(page, '#news-type', 'EVENT');
    type(page, '#news-content', 'El **sábado** a las 10:00.');
  }

  function button(page: HTMLElement, text: string): HTMLButtonElement {
    const found = Array.from(page.querySelectorAll<HTMLButtonElement>('button')).find(
      (b) => b.textContent?.trim() === text,
    );
    if (!found) throw new Error(`No hay botón «${text}»`);
    return found;
  }

  afterEach(() => http.verify());

  it('no envía nada si faltan campos y marca cada error junto a su campo', async () => {
    const { fixture, page } = await render('nueva');
    expect(page.querySelector('h1')?.textContent).toBe('Nueva noticia');
    expect(page.querySelector('.badge')?.textContent).toBe('Borrador');

    button(page, 'Guardar borrador').click();
    await fixture.whenStable();

    expect(page.querySelector('#news-title-error')?.textContent).toBe('Este campo es obligatorio');
    expect(page.querySelector('#news-type-error')?.textContent).toBe('Este campo es obligatorio');
    expect(page.querySelector('.alert')?.textContent).toContain('Revisa los campos marcados');
  });

  it('guarda una noticia nueva como borrador y pasa a editarla', async () => {
    const { fixture, page } = await render('nueva');
    fill(page);
    button(page, 'Guardar borrador').click();

    const post = http.expectOne('/api/v1/news');
    expect(post.request.method).toBe('POST');
    expect(post.request.body).toEqual({
      title: 'Jornada de puertas abiertas',
      summary: 'Ven a probar el voleibol.',
      type: 'EVENT',
      featured: false,
      content: 'El **sábado** a las 10:00.',
    });
    post.flush({ ...DRAFT, id: 9, title: 'Jornada de puertas abiertas' });
    await fixture.whenStable();

    expect(page.querySelector('h1')?.textContent).toBe('Jornada de puertas abiertas');
    expect(page.querySelector('.notice')?.textContent).toContain('Se ha guardado el borrador.');
    expect(TestBed.inject(Location).path()).toBe('/gestion/noticias/9');

    // Lo siguiente ya es editar: PUT sobre la misma noticia
    button(page, 'Guardar borrador').click();
    expect(http.expectOne('/api/v1/news/9').request.method).toBe('PUT');
  });

  it('publica guardando antes los cambios', async () => {
    const { fixture, page } = await render('7', DRAFT);
    expect((page.querySelector('#news-content') as HTMLTextAreaElement).value).toBe(
      'El **sábado**.',
    );

    button(page, 'Publicar').click();
    const put = http.expectOne('/api/v1/news/7');
    expect(put.request.method).toBe('PUT');
    put.flush(DRAFT);
    const patch = http.expectOne('/api/v1/news/7');
    expect(patch.request.method).toBe('PATCH');
    expect(Date.parse(patch.request.body.publishedAt)).toBeLessThanOrEqual(Date.now());
    patch.flush(PUBLISHED);
    await fixture.whenStable();

    expect(page.querySelector('.badge')?.textContent).toBe('Publicada');
    expect(page.querySelector('.published')?.textContent).toContain('14/11/2026');
    expect(page.querySelector('.notice')?.textContent).toContain('ya se ve en la web');
    expect(button(page, 'Retirar')).toBeTruthy();
  });

  it('retira una noticia publicada', async () => {
    const { fixture, page } = await render('7', PUBLISHED);

    button(page, 'Retirar').click();
    http.expectOne({ method: 'PUT', url: '/api/v1/news/7' }).flush(PUBLISHED);
    const patch = http.expectOne({ method: 'PATCH', url: '/api/v1/news/7' });
    expect(patch.request.body).toEqual({ publishedAt: null });
    patch.flush(DRAFT);
    await fixture.whenStable();

    expect(page.querySelector('.badge')?.textContent).toBe('Borrador');
    expect(page.querySelector('.notice')?.textContent).toContain('vuelve a ser un borrador');
  });

  it('lleva los errores de la API a su campo', async () => {
    const { fixture, page } = await render('7', DRAFT);

    button(page, 'Guardar borrador').click();
    http.expectOne('/api/v1/news/7').flush(
      {
        status: 400,
        detail: 'Hay datos que no son válidos',
        errors: [{ field: 'summary', message: 'Como máximo 300 caracteres' }],
      },
      { status: 400, statusText: 'Bad Request' },
    );
    await fixture.whenStable();

    expect(page.querySelector('#news-summary-error')?.textContent).toBe(
      'Como máximo 300 caracteres',
    );
  });

  it('borra la noticia tras confirmarlo y vuelve al listado', async () => {
    const { fixture, page } = await render('7', DRAFT);
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);

    button(page, 'Borrar noticia').click();
    await fixture.whenStable();
    button(page, 'Sí, borrar').click();
    const remove = http.expectOne('/api/v1/news/7');
    expect(remove.request.method).toBe('DELETE');
    remove.flush(null, { status: 204, statusText: 'No Content' });
    await fixture.whenStable();

    expect(navigate).toHaveBeenCalledWith(['/gestion/noticias']);
  });

  it('sube la imagen sin perder lo que se está escribiendo', async () => {
    const { fixture, page } = await render('7', DRAFT);
    type(page, '#news-title', 'Título sin guardar');

    const file = new File(['png'], 'foto.png', { type: 'image/png' });
    const input = page.querySelector('input[type=file]') as HTMLInputElement;
    Object.defineProperty(input, 'files', { value: [file] });
    input.dispatchEvent(new Event('change'));
    const put = http.expectOne('/api/v1/news/7/image');
    expect(put.request.method).toBe('PUT');
    expect((put.request.body as FormData).get('file')).toBe(file);
    put.flush({ ...DRAFT, imageUrl: '/api/images/news/x-noticia-7.png' });
    await fixture.whenStable();

    expect(page.querySelector('.picker__preview')?.getAttribute('src')).toBe(
      '/api/images/news/x-noticia-7.png',
    );
    expect((page.querySelector('#news-title') as HTMLInputElement).value).toBe(
      'Título sin guardar',
    );
    expect(page.querySelector('.notice')?.textContent).toContain('Se ha cambiado la imagen.');
  });

  it('pide guardar antes de poner la imagen a una noticia nueva', async () => {
    const { page } = await render('nueva');

    expect(page.querySelector('input[type=file]')).toBeNull();
    expect(page.textContent).toContain('Guarda el borrador para poder añadirle la imagen');
  });

  it('muestra el error de la API al subir la imagen', async () => {
    const { fixture, page } = await render('7', DRAFT);
    const input = page.querySelector('input[type=file]') as HTMLInputElement;
    Object.defineProperty(input, 'files', {
      value: [new File(['x'], 'foto.png', { type: 'image/png' })],
    });
    input.dispatchEvent(new Event('change'));
    http.expectOne('/api/v1/news/7/image').flush(
      {
        status: 400,
        errors: [{ field: 'file', message: 'Tiene que ser una imagen JPG, PNG o WebP' }],
      },
      { status: 400, statusText: 'Bad Request' },
    );
    await fixture.whenStable();

    expect(page.querySelector('.picker .field__error')?.textContent).toContain('JPG, PNG o WebP');
  });

  it('avisa si la noticia no existe', async () => {
    const { fixture, page } = await render('99');
    http
      .expectOne('/api/v1/news/99')
      .flush({ status: 404 }, { status: 404, statusText: 'Not Found' });
    await fixture.whenStable();

    expect(page.querySelector('h1')?.textContent).toBe('Noticia no encontrada');
  });
});
