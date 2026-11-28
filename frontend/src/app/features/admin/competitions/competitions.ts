import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  FormControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { forkJoin } from 'rxjs';
import { applyProblem, errorMessage } from '../../../shared/admin/problem';
import { ConfigurationApi } from '../configuration/configuration.api';
import { CATEGORY_GENDER, Category, Season } from '../configuration/configuration.models';
import { CompetitionsApi } from './competitions.api';
import { Competition, CompetitionRequest } from './competitions.models';
import { Matchdays } from './matchdays/matchdays';

/**
 * Competiciones de cada temporada y categoría (H18), con sus jornadas. Son la base del calendario
 * (partidos, H19) y de la clasificación (H21).
 */
@Component({
  selector: 'cv-competitions',
  imports: [ReactiveFormsModule, Matchdays],
  templateUrl: './competitions.html',
  styleUrls: ['../../../shared/admin/crud-panel.scss', './competitions.scss'],
})
export class Competitions implements OnInit {
  private readonly api = inject(CompetitionsApi);
  private readonly configuration = inject(ConfigurationApi);

  protected readonly seasons = signal<Season[]>([]);
  protected readonly categories = signal<Category[]>([]);
  protected readonly seasonFilter = new FormControl<number | null>(null);
  protected readonly categoryFilter = new FormControl<number | null>(null);

  protected readonly status = signal<'loading' | 'ready' | 'error'>('loading');
  protected readonly competitions = signal<Competition[]>([]);
  protected readonly selected = signal<Competition | null>(null);
  /** Temporada de la competición abierta, para limitar las fechas de sus jornadas. */
  protected readonly selectedSeason = computed(() => {
    const competition = this.selected();
    return this.seasons().find((season) => season.id === competition?.seasonId) ?? null;
  });
  protected readonly formOpen = signal(false);
  protected readonly editingId = signal<number | null>(null);
  protected readonly confirmingId = signal<number | null>(null);
  protected readonly saving = signal(false);
  protected readonly formError = signal<string | null>(null);
  protected readonly listError = signal<string | null>(null);
  /** Aviso para lectores de pantalla tras guardar o borrar. */
  protected readonly notice = signal('');

  protected readonly form = inject(NonNullableFormBuilder).group({
    name: ['', [Validators.required, Validators.maxLength(100)]],
    seasonId: new FormControl<number | null>(null, Validators.required),
    categoryId: new FormControl<number | null>(null, Validators.required),
    federation: ['', Validators.maxLength(100)],
    forfeitPenaltyPoints: new FormControl<number | null>(1, [
      Validators.required,
      Validators.min(0),
      Validators.max(10),
    ]),
  });

  constructor() {
    for (const filter of [this.seasonFilter, this.categoryFilter]) {
      filter.valueChanges.pipe(takeUntilDestroyed()).subscribe(() => {
        this.selected.set(null);
        this.load();
      });
    }
  }

  ngOnInit(): void {
    forkJoin([this.configuration.seasons.list(), this.configuration.categories.list()]).subscribe({
      next: ([seasons, categories]) => {
        this.seasons.set(seasons);
        this.categories.set(categories);
        // Por defecto, la temporada activa (o la primera si no hay ninguna activa)
        const current = seasons.find((season) => season.active) ?? seasons[0];
        this.seasonFilter.setValue(current?.id ?? null, { emitEvent: false });
        this.load();
      },
      error: () => this.status.set('error'),
    });
  }

  protected load(): void {
    this.listError.set(null);
    if (this.seasonFilter.value === null) {
      this.competitions.set([]);
      this.status.set('ready');
      return;
    }
    this.api.list(this.seasonFilter.value, this.categoryFilter.value).subscribe({
      next: (competitions) => {
        this.competitions.set(competitions);
        this.status.set('ready');
      },
      error: () => this.status.set('error'),
    });
  }

  protected categoryLabel(id: number): string {
    const category = this.categories().find((other) => other.id === id);
    return category ? this.categoryName(category) : '';
  }

  protected categoryName(category: Category): string {
    return `${category.name} ${CATEGORY_GENDER[category.gender]}`;
  }

  protected seasonName(id: number): string {
    return this.seasons().find((season) => season.id === id)?.name ?? '';
  }

  protected showMatchdays(competition: Competition): void {
    this.selected.set(this.selected()?.id === competition.id ? null : competition);
  }

  protected startCreate(): void {
    this.openForm(null, {
      name: '',
      seasonId: this.seasonFilter.value,
      categoryId: this.categoryFilter.value,
      federation: '',
      forfeitPenaltyPoints: 1,
    });
  }

  protected startEdit(competition: Competition): void {
    this.openForm(competition.id, {
      name: competition.name,
      seasonId: competition.seasonId,
      categoryId: competition.categoryId,
      federation: competition.federation ?? '',
      forfeitPenaltyPoints: competition.forfeitPenaltyPoints,
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
    const { name, seasonId, categoryId, federation, forfeitPenaltyPoints } =
      this.form.getRawValue();
    const request: CompetitionRequest = {
      name: name.trim(),
      seasonId: seasonId as number,
      categoryId: categoryId as number,
      federation: federation.trim() || null,
      forfeitPenaltyPoints: forfeitPenaltyPoints as number,
    };
    const id = this.editingId();
    const call = id === null ? this.api.create(request) : this.api.update(id, request);
    this.saving.set(true);
    this.formError.set(null);
    call.subscribe({
      next: (saved) => {
        this.saving.set(false);
        this.cancel();
        this.notice.set(`Se ha guardado la competición ${saved.name}.`);
        if (this.selected()?.id === saved.id) {
          this.selected.set(saved);
        }
        this.load();
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.formError.set(applyProblem(this.form, error));
      },
    });
  }

  protected askDelete(competition: Competition): void {
    this.listError.set(null);
    this.confirmingId.set(competition.id);
  }

  protected confirmDelete(competition: Competition): void {
    this.api.remove(competition.id).subscribe({
      next: () => {
        this.confirmingId.set(null);
        if (this.selected()?.id === competition.id) {
          this.selected.set(null);
        }
        this.competitions.update((all) => all.filter((other) => other.id !== competition.id));
        this.notice.set(`Se ha borrado la competición ${competition.name}.`);
      },
      error: (error: unknown) => {
        this.confirmingId.set(null);
        this.listError.set(
          error instanceof HttpErrorResponse && error.error?.detail
            ? error.error.detail
            : `No se ha podido borrar la competición ${competition.name}.`,
        );
      },
    });
  }

  protected errorOf(name: string): string | null {
    return errorMessage(this.form.get(name));
  }

  private openForm(id: number | null, value: object): void {
    this.form.reset(value);
    this.formError.set(null);
    this.listError.set(null);
    this.confirmingId.set(null);
    this.editingId.set(id);
    this.formOpen.set(true);
  }
}
