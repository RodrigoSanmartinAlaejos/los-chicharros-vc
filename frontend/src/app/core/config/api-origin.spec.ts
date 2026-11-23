import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { API_ORIGIN, apiOriginInterceptor } from './api-origin';

describe('apiOriginInterceptor', () => {
  function setUp(origin?: string) {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([apiOriginInterceptor])),
        provideHttpClientTesting(),
        ...(origin === undefined ? [] : [{ provide: API_ORIGIN, useValue: origin }]),
      ],
    });
    return { http: TestBed.inject(HttpClient), backend: TestBed.inject(HttpTestingController) };
  }

  afterEach(() => TestBed.inject(HttpTestingController).verify());

  it('en el navegador deja la ruta relativa para que la reenvíe el servidor', () => {
    const { http, backend } = setUp();

    http.get('/api/v1/news').subscribe();

    backend.expectOne('/api/v1/news').flush([]);
  });

  it('en el servidor llama a la URL de la API', () => {
    const { http, backend } = setUp('https://api.example.com');

    http.get('/api/v1/news').subscribe();

    backend.expectOne('https://api.example.com/api/v1/news').flush([]);
  });

  it('no toca las peticiones que no son de la API', () => {
    const { http, backend } = setUp('https://api.example.com');

    http.get('/escudo.svg').subscribe();

    backend.expectOne('/escudo.svg').flush('');
  });
});
