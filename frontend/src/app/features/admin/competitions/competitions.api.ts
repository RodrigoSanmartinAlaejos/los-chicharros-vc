import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  Competition,
  CompetitionRequest,
  Matchday,
  MatchdayRequest,
  Standing,
  StandingRequest,
} from './competitions.models';

const COMPETITIONS = '/api/v1/competitions';
const MATCHDAYS = '/api/v1/matchdays';

/** Competiciones y jornadas (H18). Leer es público; escribir, solo del administrador. */
@Injectable({ providedIn: 'root' })
export class CompetitionsApi {
  private readonly http = inject(HttpClient);

  list(seasonId: number | null, categoryId: number | null): Observable<Competition[]> {
    let params = new HttpParams();
    if (seasonId !== null) params = params.set('seasonId', seasonId);
    if (categoryId !== null) params = params.set('categoryId', categoryId);
    return this.http.get<Competition[]>(COMPETITIONS, { params });
  }

  get(id: number): Observable<Competition> {
    return this.http.get<Competition>(`${COMPETITIONS}/${id}`);
  }

  create(request: CompetitionRequest): Observable<Competition> {
    return this.http.post<Competition>(COMPETITIONS, request);
  }

  update(id: number, request: CompetitionRequest): Observable<Competition> {
    return this.http.put<Competition>(`${COMPETITIONS}/${id}`, request);
  }

  remove(id: number): Observable<void> {
    return this.http.delete<void>(`${COMPETITIONS}/${id}`);
  }

  standings(competitionId: number): Observable<Standing[]> {
    return this.http.get<Standing[]>(`${COMPETITIONS}/${competitionId}/standings`);
  }

  /** Sustituye la clasificación entera (solo en la fase 1). */
  replaceStandings(competitionId: number, rows: StandingRequest[]): Observable<Standing[]> {
    return this.http.put<Standing[]>(`${COMPETITIONS}/${competitionId}/standings`, rows);
  }

  matchdays(competitionId: number): Observable<Matchday[]> {
    return this.http.get<Matchday[]>(`${COMPETITIONS}/${competitionId}/matchdays`);
  }

  createMatchday(request: MatchdayRequest): Observable<Matchday> {
    return this.http.post<Matchday>(MATCHDAYS, request);
  }

  updateMatchday(id: number, request: MatchdayRequest): Observable<Matchday> {
    return this.http.put<Matchday>(`${MATCHDAYS}/${id}`, request);
  }

  removeMatchday(id: number): Observable<void> {
    return this.http.delete<void>(`${MATCHDAYS}/${id}`);
  }
}
