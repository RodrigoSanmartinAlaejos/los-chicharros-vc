import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Page } from '../../../shared/admin/page';
import { News, NewsPatch, NewsRequest, NewsStatusFilter, NewsSummary } from './news.models';

const URL = '/api/v1/news';

/** Noticias por página en el panel. */
export const NEWS_PAGE_SIZE = 20;

/** Noticias (H12). El panel ve también los borradores; escribir es solo del administrador. */
@Injectable({ providedIn: 'root' })
export class NewsApi {
  private readonly http = inject(HttpClient);

  /** Página de noticias con ese estado: todas por fecha de alta, publicadas por fecha. */
  list(status: NewsStatusFilter, page: number): Observable<Page<NewsSummary>> {
    const params = new HttpParams()
      .set('status', status)
      .set('page', page)
      .set('size', NEWS_PAGE_SIZE);
    return this.http.get<Page<NewsSummary>>(URL, { params });
  }

  get(id: number): Observable<News> {
    return this.http.get<News>(`${URL}/${id}`);
  }

  create(request: NewsRequest): Observable<News> {
    return this.http.post<News>(URL, request);
  }

  update(id: number, request: NewsRequest): Observable<News> {
    return this.http.put<News>(`${URL}/${id}`, request);
  }

  /** Publica, retira o destaca: solo cambia lo que se envía. */
  change(id: number, patch: NewsPatch): Observable<News> {
    return this.http.patch<News>(`${URL}/${id}`, patch);
  }

  remove(id: number): Observable<void> {
    return this.http.delete<void>(`${URL}/${id}`);
  }
}
