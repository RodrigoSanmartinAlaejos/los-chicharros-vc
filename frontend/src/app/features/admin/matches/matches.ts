import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  AbstractControl,
  FormControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { forkJoin } from 'rxjs';
import { formatClubDateTime, fromClubInput, toClubInput } from '../../../shared/admin/club-time';
import { applyProblem, errorMessage } from '../../../shared/admin/problem';
import { CompetitionsApi } from '../competitions/competitions.api';
import { Competition } from '../competitions/competitions.models';
import { ConfigurationApi } from '../configuration/configuration.api';
import { Season, Venue } from '../configuration/configuration.models';
import { TeamsApi } from '../teams/teams.api';
import { TeamSummary } from '../teams/teams.models';
import { MatchesApi } from './matches.api';
import {
  Match,
  MatchRequest,
  MatchStatus,
  MatchdaySchedule,
  STATUS_LABELS,
} from './matches.models';

/** El visitante no puede ser el mismo equipo que el local. */
function differentTeams(group: AbstractControl): ValidationErrors | null {
  const home = group.get('homeTeamId');
  const away = group.get('awayTeamId');
  const same = home?.value !== null && home?.value === away?.value;
  if (same) {
    away?.setErrors({ ...away.errors, sameTeam: true });
  } else if (away?.hasError('sameTeam')) {
    const others = { ...away.errors };
    delete others['sameTeam'];
    away.setErrors(Object.keys(others).length ? others : null);
  }
  return null;
}

/** Marcador posible: uno de los dos llega a 3 sets y el otro se queda por debajo. */
function validScore(group: AbstractControl): ValidationErrors | null {
  const home = group.get('homeSets')?.value;
  const away = group.get('awaySets')?.value;
  if (home === null || away === null) {
    return null;
  }
  return (home === 3 && away < 3) || (away === 3 && home < 3) ? null : { impossibleScore: true };
}

/**
 * Calendario de una competición (H19): sus jornadas con los partidos, el alta y la edición de
 * partidos, aplazarlos, cancelarlos o volver a programarlos, y su resultado en sets (H20). Las
 * horas son las de Canarias.
 */
@Component({
  selector: 'cv-matches',
  imports: [ReactiveFormsModule],
  templateUrl: './matches.html',
  styleUrls: ['../../../shared/admin/crud-panel.scss', './matches.scss'],
})
export class Matches implements OnInit {
  private readonly api = inject(MatchesApi);
  private readonly competitionsApi = inject(CompetitionsApi);
  private readonly teamsApi = inject(TeamsApi);
  private readonly configuration = inject(ConfigurationApi);

  protected readonly statusLabels = STATUS_LABELS;
  protected readonly seasons = signal<Season[]>([]);
  protected readonly venues = signal<Venue[]>([]);
  protected readonly competitions = signal<Competition[]>([]);
  protected readonly teams = signal<TeamSummary[]>([]);
  protected readonly seasonFilter = new FormControl<number | null>(null);
  protected readonly competitionFilter = new FormControl<number | null>(null);

  protected readonly status = signal<'loading' | 'ready' | 'error'>('loading');
  protected readonly schedule = signal<MatchdaySchedule[]>([]);
  protected readonly competition = computed(
    () => this.competitions().find((one) => one.id === this.competitionId()) ?? null,
  );
  private readonly competitionId = signal<number | null>(null);

  protected readonly formOpen = signal(false);
  protected readonly editingId = signal<number | null>(null);
  protected readonly saving = signal(false);
  protected readonly formError = signal<string | null>(null);
  protected readonly listError = signal<string | null>(null);
  /** Partido con una acción pendiente de confirmar: borrarlo o cancelarlo. */
  protected readonly confirming = signal<{ id: number; action: 'delete' | 'cancel' } | null>(null);
  /** Partido aplazado o cancelado al que se le está poniendo nueva fecha. */
  protected readonly reschedulingId = signal<number | null>(null);
  protected readonly rescheduleForm = inject(NonNullableFormBuilder).group({
    startsAt: ['', Validators.required],
  });
  protected readonly rescheduleError = signal<string | null>(null);
  /** Partido al que se le está poniendo el resultado. */
  protected readonly scoringId = signal<number | null>(null);
  protected readonly scoreForm = inject(NonNullableFormBuilder).group(
    {
      homeSets: new FormControl<number | null>(null, [
        Validators.required,
        Validators.min(0),
        Validators.max(3),
      ]),
      awaySets: new FormControl<number | null>(null, [
        Validators.required,
        Validators.min(0),
        Validators.max(3),
      ]),
    },
    { validators: validScore },
  );
  protected readonly scoreError = signal<string | null>(null);
  /** Aviso para lectores de pantalla tras cada cambio. */
  protected readonly notice = signal('');

  protected readonly form = inject(NonNullableFormBuilder).group(
    {
      matchdayId: new FormControl<number | null>(null, Validators.required),
      homeTeamId: new FormControl<number | null>(null, Validators.required),
      awayTeamId: new FormControl<number | null>(null, Validators.required),
      startsAt: ['', Validators.required],
      venueId: new FormControl<number | null>(null),
    },
    { validators: differentTeams },
  );

  constructor() {
    this.seasonFilter.valueChanges.pipe(takeUntilDestroyed()).subscribe(() => {
      this.loadCompetitions();
    });
    this.competitionFilter.valueChanges.pipe(takeUntilDestroyed()).subscribe((id) => {
      this.competitionId.set(id);
      this.cancel();
      this.loadSchedule();
    });
  }

  ngOnInit(): void {
    forkJoin([this.configuration.seasons.list(), this.configuration.venues.list()]).subscribe({
      next: ([seasons, venues]) => {
        this.seasons.set(seasons);
        this.venues.set(venues);
        const current = seasons.find((season) => season.active) ?? seasons[0];
        this.seasonFilter.setValue(current?.id ?? null, { emitEvent: false });
        this.loadCompetitions();
      },
      error: () => this.status.set('error'),
    });
  }

  protected loadCompetitions(): void {
    const seasonId = this.seasonFilter.value;
    if (seasonId === null) {
      this.status.set('ready');
      return;
    }
    this.status.set('loading');
    this.competitionsApi.list(seasonId, null).subscribe({
      next: (competitions) => {
        this.competitions.set(competitions);
        // Se queda en la misma competición si sigue en la lista; si no, la primera
        const keep = competitions.find((one) => one.id === this.competitionFilter.value);
        this.competitionFilter.setValue(keep?.id ?? competitions[0]?.id ?? null);
      },
      error: () => this.status.set('error'),
    });
  }

  protected loadSchedule(): void {
    const competition = this.competition();
    this.listError.set(null);
    if (competition === null) {
      this.schedule.set([]);
      this.status.set('ready');
      return;
    }
    forkJoin([
      this.api.schedule(competition.id),
      this.teamsApi.list(competition.seasonId, competition.categoryId),
    ]).subscribe({
      next: ([schedule, teams]) => {
        this.schedule.set(schedule);
        this.teams.set(teams);
        this.status.set('ready');
      },
      error: () => this.status.set('error'),
    });
  }

  protected formatDateTime(iso: string): string {
    return formatClubDateTime(iso);
  }

  protected matchdayLabel(matchday: MatchdaySchedule): string {
    if (!matchday.scheduledDate) {
      return `Jornada ${matchday.number}`;
    }
    const [year, month, day] = matchday.scheduledDate.split('-');
    return `Jornada ${matchday.number} · ${day}/${month}/${year}`;
  }

  protected isPending(match: Match): boolean {
    return match.status === 'POSTPONED' || match.status === 'CANCELLED';
  }

  /** Se puede poner el resultado de un partido ya empezado que no esté cancelado. */
  protected canScore(match: Match): boolean {
    return match.status !== 'CANCELLED' && Date.parse(match.startsAt) <= Date.now();
  }

  protected startScore(match: Match): void {
    this.confirming.set(null);
    this.reschedulingId.set(null);
    this.scoreError.set(null);
    this.scoreForm.reset({ homeSets: match.homeSets, awaySets: match.awaySets });
    this.scoringId.set(match.id);
  }

  protected saveScore(match: Match): void {
    if (this.scoreForm.invalid) {
      this.scoreForm.markAllAsTouched();
      this.scoreError.set(
        this.scoreForm.hasError('impossibleScore')
          ? 'Uno de los dos tiene que llegar a 3 sets y el otro quedarse en menos de 3'
          : 'Indica los sets de los dos equipos, de 0 a 3',
      );
      return;
    }
    const { homeSets, awaySets } = this.scoreForm.getRawValue();
    this.api
      .change(match.id, { homeSets: homeSets as number, awaySets: awaySets as number })
      .subscribe({
        next: (saved) => {
          this.scoringId.set(null);
          this.notice.set(
            `Resultado guardado: ${this.title(match)}, ${saved.homeSets}-${saved.awaySets}.`,
          );
          this.loadSchedule();
        },
        error: (error: unknown) => {
          const problem = error instanceof HttpErrorResponse ? error.error : null;
          this.scoreError.set(
            problem?.errors?.[0]?.message ?? problem?.detail ?? 'No se ha podido guardar.',
          );
        },
      });
  }

  protected startCreate(matchday: MatchdaySchedule): void {
    this.openForm(null, {
      matchdayId: matchday.id,
      homeTeamId: null,
      awayTeamId: null,
      startsAt: matchday.scheduledDate ? `${matchday.scheduledDate}T10:00` : '',
      venueId: null,
    });
  }

  protected startEdit(match: Match): void {
    this.openForm(match.id, {
      matchdayId: match.matchdayId,
      homeTeamId: match.homeTeam.id,
      awayTeamId: match.awayTeam.id,
      startsAt: toClubInput(match.startsAt),
      venueId: match.venue?.id ?? null,
    });
  }

  protected cancel(): void {
    this.formOpen.set(false);
    this.editingId.set(null);
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { matchdayId, homeTeamId, awayTeamId, startsAt, venueId } = this.form.getRawValue();
    const request: MatchRequest = {
      matchdayId: matchdayId as number,
      homeTeamId: homeTeamId as number,
      awayTeamId: awayTeamId as number,
      startsAt: fromClubInput(startsAt),
      venueId,
    };
    const id = this.editingId();
    const call = id === null ? this.api.create(request) : this.api.update(id, request);
    this.saving.set(true);
    this.formError.set(null);
    call.subscribe({
      next: (saved) => {
        this.saving.set(false);
        this.cancel();
        this.notice.set(`Se ha guardado el partido ${this.title(saved)}.`);
        this.loadSchedule();
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.formError.set(applyProblem(this.form, error));
      },
    });
  }

  protected postpone(match: Match): void {
    this.changeStatus(match, 'POSTPONED', `Se ha aplazado el partido ${this.title(match)}.`);
  }

  protected askCancelMatch(match: Match): void {
    this.listError.set(null);
    this.reschedulingId.set(null);
    this.confirming.set({ id: match.id, action: 'cancel' });
  }

  protected askDelete(match: Match): void {
    this.listError.set(null);
    this.reschedulingId.set(null);
    this.confirming.set({ id: match.id, action: 'delete' });
  }

  protected confirm(match: Match): void {
    const action = this.confirming()?.action;
    this.confirming.set(null);
    if (action === 'cancel') {
      this.changeStatus(match, 'CANCELLED', `Se ha cancelado el partido ${this.title(match)}.`);
      return;
    }
    this.api.remove(match.id).subscribe({
      next: () => {
        this.notice.set(`Se ha borrado el partido ${this.title(match)}.`);
        this.loadSchedule();
      },
      error: (error: unknown) => this.listError.set(this.detail(error, 'borrar', match)),
    });
  }

  protected startReschedule(match: Match): void {
    this.confirming.set(null);
    this.scoringId.set(null);
    this.rescheduleError.set(null);
    this.rescheduleForm.reset({ startsAt: toClubInput(match.startsAt) });
    this.reschedulingId.set(match.id);
  }

  protected reschedule(match: Match): void {
    if (this.rescheduleForm.invalid) {
      this.rescheduleForm.markAllAsTouched();
      this.rescheduleError.set('Indica la nueva fecha y hora');
      return;
    }
    this.api
      .change(match.id, {
        status: 'SCHEDULED',
        startsAt: fromClubInput(this.rescheduleForm.getRawValue().startsAt),
      })
      .subscribe({
        next: () => {
          this.reschedulingId.set(null);
          this.notice.set(`Se ha vuelto a programar el partido ${this.title(match)}.`);
          this.loadSchedule();
        },
        error: (error: unknown) => {
          const problem = error instanceof HttpErrorResponse ? error.error : null;
          this.rescheduleError.set(
            problem?.errors?.[0]?.message ?? problem?.detail ?? 'No se ha podido guardar.',
          );
        },
      });
  }

  protected errorOf(name: string): string | null {
    const control = this.form.get(name);
    if (control?.touched && control.hasError('sameTeam')) {
      return 'El visitante tiene que ser un equipo distinto del local';
    }
    return errorMessage(control);
  }

  protected title(match: Match): string {
    return `${match.homeTeam.name} - ${match.awayTeam.name}`;
  }

  private changeStatus(match: Match, status: MatchStatus, notice: string): void {
    this.listError.set(null);
    this.api.change(match.id, { status }).subscribe({
      next: () => {
        this.notice.set(notice);
        this.loadSchedule();
      },
      error: (error: unknown) => this.listError.set(this.detail(error, 'cambiar', match)),
    });
  }

  private detail(error: unknown, verb: string, match: Match): string {
    return error instanceof HttpErrorResponse && error.error?.detail
      ? error.error.detail
      : `No se ha podido ${verb} el partido ${this.title(match)}.`;
  }

  private openForm(id: number | null, value: object): void {
    this.form.reset(value);
    this.formError.set(null);
    this.listError.set(null);
    this.confirming.set(null);
    this.reschedulingId.set(null);
    this.scoringId.set(null);
    this.editingId.set(id);
    this.formOpen.set(true);
  }
}
