import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Page } from '../../../shared/admin/page';
import { Person, PersonRequest } from './people.models';

const URL = '/api/v1/people';

/** Personas por página en el panel. */
export const PEOPLE_PAGE_SIZE = 20;

/** Jugadores y entrenadores (H16). Todas las operaciones son solo del administrador. */
@Injectable({ providedIn: 'root' })
export class PeopleApi {
  private readonly http = inject(HttpClient);

  /** Página de personas cuyo nombre o apellidos contienen el texto, ordenadas por apellidos. */
  search(text: string, page: number): Observable<Page<Person>> {
    let params = new HttpParams().set('page', page).set('size', PEOPLE_PAGE_SIZE);
    if (text) {
      params = params.set('search', text);
    }
    return this.http.get<Page<Person>>(URL, { params });
  }

  create(request: PersonRequest): Observable<Person> {
    return this.http.post<Person>(URL, request);
  }

  update(id: number, request: PersonRequest): Observable<Person> {
    return this.http.put<Person>(`${URL}/${id}`, request);
  }

  /** Sube la foto (multipart, campo `file`); la anterior la borra la API (H14). */
  uploadPhoto(id: number, file: File): Observable<Person> {
    const body = new FormData();
    body.append('file', file);
    return this.http.put<Person>(`${URL}/${id}/photo`, body);
  }

  remove(id: number): Observable<void> {
    return this.http.delete<void>(`${URL}/${id}`);
  }
}
