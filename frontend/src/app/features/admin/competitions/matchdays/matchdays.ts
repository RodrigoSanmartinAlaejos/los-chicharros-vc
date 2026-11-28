import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  ElementRef,
  Injector,
  afterNextRender,
  computed,
  effect,
  inject,
  input,
  output,
  signal,
  untracked,
  viewChild,
} from '@angular/core';
import {
  FormControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { applyProblem, errorMessage } from '../../../../shared/admin/problem';
import { Season } from '../../configuration/configuration.models';
import { CompetitionsApi } from '../competitions.api';
import { Matchday } from '../competitions.models';

const DAY_MS = 24 * 60 * 60 * 1000;
const DATE_FORMAT = new Intl.DateTimeFormat('es-ES', {
  weekday: 'short',
  day: '2-digit',
  month: '2-digit',
  year: 'numeric',
  timeZone: 'UTC',
});

/**
 * Jornadas de una competición (H18). El formulario de alta propone el siguiente número y la
 * siguiente fecha (con el mismo intervalo que las dos últimas jornadas, o una semana), y se queda
 * abierto para meter las jornadas seguidas.
 */
@Component({
  selector: 'cv-matchdays',
  imports: [ReactiveFormsModule],
  templateUrl: './matchdays.html',
  styleUrls: ['../../../../shared/admin/crud-panel.scss', './matchdays.scss'],
})
export class Matchdays {
  private readonly api = inject(CompetitionsApi);
  private readonly injector = inject(Injector);

  readonly competitionId = input.required<number>();
  /** Nombre de la competición con su temporada, para el título. */
  readonly title = input.required<string>();
  /** Temporada de la competición: las fechas de las jornadas tienen que caer dentro. */
  readonly season = input<Season | null>(null);
  readonly closed = output<void>();
  private readonly heading = viewChild.required<ElementRef<HTMLElement>>('heading');
  /** Al abrir otra competición, el foco va a su título (en móvil queda bajo una lista larga). */
  private focusPending = true;

  protected readonly status = signal<'loading' | 'ready' | 'error'>('loading');
  protected readonly matchdays = signal<Matchday[]>([]);
  protected readonly listError = signal<string | null>(null);
  protected readonly notice = signal('');
  protected readonly dateHint = computed(() => {
    const season = this.season();
    return season
      ? `Dentro de la temporada ${season.name}: del ${this.formatDate(season.startDate)} al ${this.formatDate(season.endDate)}.`
      : null;
  });

  // Alta
  protected readonly addOpen = signal(false);
  protected readonly adding = signal(false);
  protected readonly addError = signal<string | null>(null);
  protected readonly addForm = inject(NonNullableFormBuilder).group({
    number: new FormControl<number | null>(null, [
      Validators.required,
      Validators.min(1),
      Validators.max(99),
    ]),
    scheduledDate: [''],
  });

  // Edición en la fila y borrado
  protected readonly editingId = signal<number | null>(null);
  protected readonly confirmingId = signal<number | null>(null);
  protected readonly editError = signal<string | null>(null);
  protected readonly editForm = inject(NonNullableFormBuilder).group({
    number: new FormControl<number | null>(null, [
      Validators.required,
      Validators.min(1),
      Validators.max(99),
    ]),
    scheduledDate: [''],
  });

  constructor() {
    effect(() => {
      const id = this.competitionId();
      untracked(() => {
        this.addOpen.set(false);
        this.editingId.set(null);
        this.confirmingId.set(null);
        this.status.set('loading');
        this.focusPending = true;
        this.load(id);
      });
    });
  }

  protected load(id = this.competitionId()): void {
    this.api.matchdays(id).subscribe({
      next: (matchdays) => {
        this.matchdays.set(matchdays);
        this.status.set('ready');
        if (this.focusPending) {
          this.focusPending = false;
          afterNextRender(
            () => {
              const heading = this.heading().nativeElement;
              heading.focus({ preventScroll: true });
              heading.scrollIntoView?.({ block: 'start' });
            },
            { injector: this.injector },
          );
        }
      },
      error: () => this.status.set('error'),
    });
  }

  protected openAdd(): void {
    this.addForm.reset(this.suggestion());
    this.addError.set(null);
    this.addOpen.set(true);
  }

  protected add(): void {
    if (this.addForm.invalid) {
      this.addForm.markAllAsTouched();
      return;
    }
    const { number, scheduledDate } = this.addForm.getRawValue();
    this.adding.set(true);
    this.addError.set(null);
    this.api
      .createMatchday({
        competitionId: this.competitionId(),
        number: number as number,
        scheduledDate: scheduledDate || null,
      })
      .subscribe({
        next: (created) => {
          this.adding.set(false);
          this.matchdays.update((all) =>
            [...all, created].sort((one, other) => one.number - other.number),
          );
          this.notice.set(`Se ha añadido la jornada ${created.number}.`);
          // El formulario sigue abierto con la siguiente jornada propuesta
          this.addForm.reset(this.suggestion());
        },
        error: (error: unknown) => {
          this.adding.set(false);
          this.addError.set(applyProblem(this.addForm, error));
        },
      });
  }

  protected startEdit(matchday: Matchday): void {
    this.confirmingId.set(null);
    this.editError.set(null);
    this.editForm.reset({
      number: matchday.number,
      scheduledDate: matchday.scheduledDate ?? '',
    });
    this.editingId.set(matchday.id);
  }

  protected saveEdit(matchday: Matchday): void {
    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }
    const { number, scheduledDate } = this.editForm.getRawValue();
    this.api
      .updateMatchday(matchday.id, {
        competitionId: this.competitionId(),
        number: number as number,
        scheduledDate: scheduledDate || null,
      })
      .subscribe({
        next: (saved) => {
          this.editingId.set(null);
          this.matchdays.update((all) =>
            all
              .map((other) => (other.id === saved.id ? saved : other))
              .sort((one, other) => one.number - other.number),
          );
          this.notice.set(`Se ha guardado la jornada ${saved.number}.`);
        },
        error: (error: unknown) => this.editError.set(applyProblem(this.editForm, error)),
      });
  }

  protected askRemove(matchday: Matchday): void {
    this.editingId.set(null);
    this.listError.set(null);
    this.confirmingId.set(matchday.id);
  }

  protected confirmRemove(matchday: Matchday): void {
    this.api.removeMatchday(matchday.id).subscribe({
      next: () => {
        this.confirmingId.set(null);
        this.matchdays.update((all) => all.filter((other) => other.id !== matchday.id));
        this.notice.set(`Se ha borrado la jornada ${matchday.number}.`);
      },
      error: (error: unknown) => {
        this.confirmingId.set(null);
        this.listError.set(
          error instanceof HttpErrorResponse && error.error?.detail
            ? error.error.detail
            : `No se ha podido borrar la jornada ${matchday.number}.`,
        );
      },
    });
  }

  protected addErrorOf(name: string): string | null {
    return errorMessage(this.addForm.get(name));
  }

  protected editErrorOf(name: string): string | null {
    return errorMessage(this.editForm.get(name));
  }

  /** «sáb, 03/10/2026», o una raya si la jornada no tiene fecha. */
  protected formatDate(iso: string | null): string {
    return iso ? DATE_FORMAT.format(new Date(`${iso}T00:00:00Z`)) : '—';
  }

  /**
   * Siguiente jornada: el número tras la última y, si la última tiene fecha, la misma distancia
   * que entre las dos últimas (una semana si no se sabe), siempre que no se salga de la temporada.
   */
  private suggestion(): { number: number | null; scheduledDate: string } {
    const all = this.matchdays();
    const last = all.at(-1);
    const number = last ? Math.min(last.number + 1, 99) : 1;
    if (!last?.scheduledDate) {
      return { number, scheduledDate: '' };
    }
    const before = all.at(-2)?.scheduledDate;
    const lastTime = Date.parse(`${last.scheduledDate}T00:00:00Z`);
    const step = before ? lastTime - Date.parse(`${before}T00:00:00Z`) : 7 * DAY_MS;
    const next = new Date(lastTime + (step > 0 ? step : 7 * DAY_MS)).toISOString().slice(0, 10);
    const end = this.season()?.endDate;
    return { number, scheduledDate: end && next > end ? '' : next };
  }
}
