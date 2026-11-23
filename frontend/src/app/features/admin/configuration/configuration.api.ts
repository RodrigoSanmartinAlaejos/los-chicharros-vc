import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  Category,
  CategoryRequest,
  Season,
  SeasonRequest,
  Venue,
  VenueRequest,
} from './configuration.models';

/** Operaciones de un recurso con CRUD completo en la API. */
export interface CrudResource<T, R> {
  list(): Observable<T[]>;
  create(request: R): Observable<T>;
  update(id: number, request: R): Observable<T>;
  remove(id: number): Observable<void>;
}

function crudResource<T, R>(http: HttpClient, path: string): CrudResource<T, R> {
  return {
    list: () => http.get<T[]>(path),
    create: (request) => http.post<T>(path, request),
    update: (id, request) => http.put<T>(`${path}/${id}`, request),
    remove: (id) => http.delete<void>(`${path}/${id}`),
  };
}

/** Temporadas, categorías e instalaciones (H15). */
@Injectable({ providedIn: 'root' })
export class ConfigurationApi {
  private readonly http = inject(HttpClient);

  readonly seasons = crudResource<Season, SeasonRequest>(this.http, '/api/v1/seasons');
  readonly categories = crudResource<Category, CategoryRequest>(this.http, '/api/v1/categories');
  readonly venues = crudResource<Venue, VenueRequest>(this.http, '/api/v1/venues');
}
