import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  FormControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { forkJoin } from 'rxjs';
import { ImagePicker, imageUploadError } from '../../../shared/admin/image-picker/image-picker';
import { applyProblem, errorMessage } from '../../../shared/admin/problem';
import { ConfigurationApi } from '../configuration/configuration.api';
import { CATEGORY_GENDER, Category, Season } from '../configuration/configuration.models';
import { Roster } from './roster/roster';
import { TeamsApi } from './teams.api';
import { TeamRequest, TeamSummary } from './teams.models';

/**
 * Equipos del club y rivales (H17), filtrados por temporada y categoría, con su plantilla. Los
 * rivales hacen falta para los partidos y la clasificación, pero no salen en la web del club.
 */
@Component({
  selector: 'cv-teams',
  imports: [ReactiveFormsModule, Roster, ImagePicker],
  templateUrl: './teams.html',
  styleUrls: ['../../../shared/admin/crud-panel.scss', './teams.scss'],
})
export class Teams implements OnInit {
  private readonly api = inject(TeamsApi);
  private readonly configuration = inject(ConfigurationApi);

  protected readonly seasons = signal<Season[]>([]);
  protected readonly categories = signal<Category[]>([]);
  protected readonly seasonFilter = new FormControl<number | null>(null);
  protected readonly categoryFilter = new FormControl<number | null>(null);

  protected readonly status = signal<'loading' | 'ready' | 'error'>('loading');
  protected readonly teams = signal<TeamSummary[]>([]);
  protected readonly selected = signal<TeamSummary | null>(null);
  protected readonly formOpen = signal(false);
  protected readonly editingId = signal<number | null>(null);
  protected readonly confirmingId = signal<number | null>(null);
  /** Escudo del equipo que se edita (H14): se sube aparte, en cuanto se elige. */
  protected readonly crestUrl = signal<string | null>(null);
  protected readonly uploadingCrest = signal(false);
  protected readonly crestError = signal<string | null>(null);
  protected readonly saving = signal(false);
  protected readonly formError = signal<string | null>(null);
  protected readonly listError = signal<string | null>(null);
  /** Aviso para lectores de pantalla tras guardar o borrar. */
  protected readonly notice = signal('');

  protected readonly form = inject(NonNullableFormBuilder).group({
    name: ['', [Validators.required, Validators.maxLength(100)]],
    seasonId: new FormControl<number | null>(null, Validators.required),
    categoryId: new FormControl<number | null>(null, Validators.required),
    ownClub: [false],
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
    this.api.list(this.seasonFilter.value, this.categoryFilter.value).subscribe({
      next: (teams) => {
        this.teams.set(teams);
        this.status.set('ready');
      },
      error: () => this.status.set('error'),
    });
  }

  protected categoryLabel(category: Category): string {
    return `${category.name} ${CATEGORY_GENDER[category.gender]}`;
  }

  protected seasonName(id: number): string {
    return this.seasons().find((season) => season.id === id)?.name ?? '';
  }

  protected showRoster(team: TeamSummary): void {
    this.selected.set(this.selected()?.id === team.id ? null : team);
  }

  protected startCreate(): void {
    this.openForm(null, {
      name: '',
      seasonId: this.seasonFilter.value,
      categoryId: this.categoryFilter.value,
      ownClub: false,
    });
  }

  protected startEdit(team: TeamSummary): void {
    this.crestUrl.set(team.crestUrl);
    this.openForm(team.id, {
      name: team.name,
      seasonId: team.seasonId,
      categoryId: team.category.id,
      ownClub: team.ownClub,
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
    const { name, seasonId, categoryId, ownClub } = this.form.getRawValue();
    const request: TeamRequest = {
      name: name.trim(),
      seasonId: seasonId as number,
      categoryId: categoryId as number,
      ownClub,
    };
    const id = this.editingId();
    const call = id === null ? this.api.create(request) : this.api.update(id, request);
    this.saving.set(true);
    this.formError.set(null);
    call.subscribe({
      next: (saved) => {
        this.saving.set(false);
        this.cancel();
        this.notice.set(`Se ha guardado el equipo ${saved.name}.`);
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

  /** Sube el escudo sin tocar el formulario: lo que se esté escribiendo no se pierde. */
  protected uploadCrest(file: File): void {
    const id = this.editingId();
    if (id === null) {
      return;
    }
    this.uploadingCrest.set(true);
    this.crestError.set(null);
    this.api.uploadCrest(id, file).subscribe({
      next: (saved) => {
        this.uploadingCrest.set(false);
        this.crestUrl.set(saved.crestUrl);
        this.teams.update((teams) =>
          teams.map((team) =>
            team.id === saved.id ? { ...team, crestUrl: saved.crestUrl } : team,
          ),
        );
        this.notice.set(`Se ha cambiado el escudo de ${saved.name}.`);
      },
      error: (error: unknown) => {
        this.uploadingCrest.set(false);
        this.crestError.set(imageUploadError(error));
      },
    });
  }

  protected askDelete(team: TeamSummary): void {
    this.listError.set(null);
    this.confirmingId.set(team.id);
  }

  protected confirmDelete(team: TeamSummary): void {
    this.api.remove(team.id).subscribe({
      next: () => {
        this.confirmingId.set(null);
        if (this.selected()?.id === team.id) {
          this.selected.set(null);
        }
        this.teams.update((teams) => teams.filter((other) => other.id !== team.id));
        this.notice.set(`Se ha borrado el equipo ${team.name}.`);
      },
      error: (error: unknown) => {
        this.confirmingId.set(null);
        this.listError.set(
          error instanceof HttpErrorResponse && error.error?.detail
            ? error.error.detail
            : `No se ha podido borrar el equipo ${team.name}.`,
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
    this.crestError.set(null);
    this.listError.set(null);
    this.confirmingId.set(null);
    this.editingId.set(id);
    this.formOpen.set(true);
  }
}
