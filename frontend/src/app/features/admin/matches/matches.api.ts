import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Match, MatchPatchRequest, MatchRequest, MatchdaySchedule } from './matches.models';

const URL = '/api/v1/matches';

/** Partidos (H19). Leer es público; escribir, solo del administrador. */
@Injectable({ providedIn: 'root' })
export class MatchesApi {
  private readonly http = inject(HttpClient);

  /** Jornadas de una competición, cada una con sus partidos. */
  schedule(competitionId: number): Observable<MatchdaySchedule[]> {
    return this.http.get<MatchdaySchedule[]>(`/api/v1/competitions/${competitionId}/matchdays`);
  }

  create(request: MatchRequest): Observable<Match> {
    return this.http.post<Match>(URL, request);
  }

  update(id: number, request: MatchRequest): Observable<Match> {
    return this.http.put<Match>(`${URL}/${id}`, request);
  }

  change(id: number, request: MatchPatchRequest): Observable<Match> {
    return this.http.patch<Match>(`${URL}/${id}`, request);
  }

  remove(id: number): Observable<void> {
    return this.http.delete<void>(`${URL}/${id}`);
  }
}
