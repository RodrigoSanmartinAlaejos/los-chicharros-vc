import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  Membership,
  MembershipPatchRequest,
  MembershipRequest,
  Team,
  TeamRequest,
  TeamSummary,
} from './teams.models';

const URL = '/api/v1/teams';

/** Equipos y plantillas (H17). Leer es público; escribir, solo del administrador. */
@Injectable({ providedIn: 'root' })
export class TeamsApi {
  private readonly http = inject(HttpClient);

  list(seasonId: number | null, categoryId: number | null): Observable<TeamSummary[]> {
    let params = new HttpParams();
    if (seasonId !== null) params = params.set('seasonId', seasonId);
    if (categoryId !== null) params = params.set('categoryId', categoryId);
    return this.http.get<TeamSummary[]>(URL, { params });
  }

  get(id: number): Observable<Team> {
    return this.http.get<Team>(`${URL}/${id}`);
  }

  create(request: TeamRequest): Observable<Team> {
    return this.http.post<Team>(URL, request);
  }

  update(id: number, request: TeamRequest): Observable<Team> {
    return this.http.put<Team>(`${URL}/${id}`, request);
  }

  /** Sube el escudo o la foto (multipart, campo `file`); el anterior lo borra la API (H14). */
  uploadCrest(id: number, file: File): Observable<Team> {
    const body = new FormData();
    body.append('file', file);
    return this.http.put<Team>(`${URL}/${id}/crest`, body);
  }

  remove(id: number): Observable<void> {
    return this.http.delete<void>(`${URL}/${id}`);
  }

  addMember(teamId: number, request: MembershipRequest): Observable<Membership> {
    return this.http.post<Membership>(`${URL}/${teamId}/roster`, request);
  }

  changeMember(
    teamId: number,
    membershipId: number,
    request: MembershipPatchRequest,
  ): Observable<Membership> {
    return this.http.patch<Membership>(`${URL}/${teamId}/roster/${membershipId}`, request);
  }

  removeMember(teamId: number, membershipId: number): Observable<void> {
    return this.http.delete<void>(`${URL}/${teamId}/roster/${membershipId}`);
  }
}
