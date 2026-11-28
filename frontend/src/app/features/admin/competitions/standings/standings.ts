import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin, switchMap } from 'rxjs';
import { errorMessage } from '../../../../shared/admin/problem';
import { TeamsApi } from '../../teams/teams.api';
import { TeamSummary } from '../../teams/teams.models';
import { CompetitionsApi } from '../competitions.api';
import { Competition, Standing, StandingRequest } from '../competitions.models';

/** Columnas numéricas de cada fila, en el orden de la tabla. */
const COLUMNS = [
  { name: 'played', label: 'PJ', title: 'Partidos jugados' },
  { name: 'won', label: 'PG', title: 'Partidos ganados' },
  { name: 'lost', label: 'PP', title: 'Partidos perdidos' },
  { name: 'setsWon', label: 'SG', title: 'Sets ganados' },
  { name: 'setsLost', label: 'SP', title: 'Sets perdidos' },
  { name: 'penaltyPoints', label: 'Sanción', title: 'Puntos de sanción' },
  { name: 'points', label: 'Puntos', title: 'Puntos' },
] as const;

type Column = (typeof COLUMNS)[number]['name'];

type RowForm = FormGroup<Record<Column, FormControl<number | null>>>;

/** Los ganados y los perdidos suman los jugados (en voleibol no hay empates). */
function consistentRow(group: AbstractControl): ValidationErrors | null {
  const { played, won, lost } = group.value;
  if (played === null || won === null || lost === null) {
    return null;
  }
  return won + lost === played ? null : { inconsistent: true };
}

/**
 * Clasificación de una competición escrita a mano (H21): una fila por cada equipo de su categoría
 * y temporada. Al guardar, la API la devuelve ordenada por puntos, ganados y cociente de sets. En
 * la fase 3 se calculará sola con los resultados.
 */
@Component({
  selector: 'cv-standings',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './standings.html',
  styleUrls: ['../../../../shared/admin/crud-panel.scss', './standings.scss'],
})
export class Standings implements OnInit {
  private readonly api = inject(CompetitionsApi);
  private readonly teamsApi = inject(TeamsApi);
  private readonly route = inject(ActivatedRoute);

  protected readonly columns = COLUMNS;
  protected readonly status = signal<'loading' | 'ready' | 'error' | 'not-found'>('loading');
  protected readonly competition = signal<Competition | null>(null);
  /** Equipo y puesto de cada fila del formulario, en el mismo orden. */
  protected readonly rows = signal<{ team: TeamSummary; position: number | null }[]>([]);
  protected readonly form = new FormArray<RowForm>([]);
  /** Envoltorio para que el formulario de la plantilla tenga un `formGroup` (y `ngSubmit`). */
  protected readonly sheet = new FormGroup({ rows: this.form });
  protected readonly saving = signal(false);
  protected readonly formError = signal<string | null>(null);
  protected readonly notice = signal('');

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.api
      .get(id)
      .pipe(
        switchMap((competition) => {
          this.competition.set(competition);
          return forkJoin([
            this.api.standings(id),
            this.teamsApi.list(competition.seasonId, competition.categoryId),
          ]);
        }),
      )
      .subscribe({
        next: ([standings, teams]) => {
          this.fill(standings, teams);
          this.status.set('ready');
        },
        error: (error: unknown) =>
          this.status.set(
            error instanceof HttpErrorResponse && error.status === 404 ? 'not-found' : 'error',
          ),
      });
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.formError.set('Revisa las filas marcadas antes de guardar.');
      return;
    }
    const competition = this.competition() as Competition;
    const request: StandingRequest[] = this.rows().map(({ team }, i) => ({
      teamId: team.id,
      ...(this.form.at(i).getRawValue() as Omit<StandingRequest, 'teamId'>),
    }));
    this.saving.set(true);
    this.formError.set(null);
    this.api.replaceStandings(competition.id, request).subscribe({
      next: (saved) => {
        this.saving.set(false);
        this.fill(
          saved,
          this.rows().map((row) => row.team),
        );
        this.notice.set('Se ha guardado la clasificación.');
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.formError.set(this.applyRowProblem(error));
      },
    });
  }

  protected errorOf(row: number, column: Column): string | null {
    return errorMessage(this.form.at(row).controls[column]);
  }

  protected rowError(row: number): string | null {
    const group = this.form.at(row);
    if (group.hasError('server')) {
      return group.getError('server');
    }
    return group.hasError('inconsistent') && (group.touched || group.dirty)
      ? 'Los ganados y los perdidos tienen que sumar los jugados'
      : null;
  }

  /**
   * Una fila por equipo: primero los que ya están en la clasificación, en su orden, y después los
   * que aún no tienen fila, con todo a cero.
   */
  private fill(standings: Standing[], teams: TeamSummary[]): void {
    const byTeam = new Map(standings.map((standing) => [standing.team.id, standing]));
    const ordered = [
      ...standings.map((standing) => standing.team),
      ...teams.filter((team) => !byTeam.has(team.id)),
    ];
    this.form.clear();
    for (const team of ordered) {
      const standing = byTeam.get(team.id);
      this.form.push(this.row(standing));
    }
    this.rows.set(
      ordered.map((team) => ({ team, position: byTeam.get(team.id)?.position ?? null })),
    );
  }

  private row(standing?: Standing): RowForm {
    const control = (name: Column) =>
      new FormControl<number | null>(standing?.[name] ?? 0, [
        Validators.required,
        ...(name === 'points' ? [] : [Validators.min(0)]),
      ]);
    return new FormGroup(
      {
        played: control('played'),
        won: control('won'),
        lost: control('lost'),
        setsWon: control('setsWon'),
        setsLost: control('setsLost'),
        penaltyPoints: control('penaltyPoints'),
        points: control('points'),
      },
      { validators: consistentRow },
    );
  }

  /** Lleva el error de la API («[2].played») a su fila; devuelve el aviso general. */
  private applyRowProblem(error: unknown): string {
    const problem = error instanceof HttpErrorResponse ? error.error : null;
    const violation = problem?.errors?.[0];
    const match = /^\[(\d+)\]\.?(\w*)$/.exec(violation?.field ?? '');
    if (match) {
      const group = this.form.at(Number(match[1]));
      const control = group?.get(match[2]);
      if (control) {
        control.setErrors({ server: violation.message });
        control.markAsTouched();
      } else {
        group?.setErrors({ server: violation.message });
      }
      return 'Revisa las filas marcadas antes de guardar.';
    }
    return problem?.detail ?? 'No se ha podido guardar la clasificación.';
  }
}
