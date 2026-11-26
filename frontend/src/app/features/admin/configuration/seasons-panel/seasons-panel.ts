import { Component, inject } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ConfigurationApi } from '../configuration.api';
import { Season, SeasonRequest } from '../configuration.models';
import { CrudPanel } from '../crud-panel';

/** Temporadas: solo una puede estar activa; la API desactiva la anterior al activar otra. */
@Component({
  selector: 'cv-seasons-panel',
  imports: [ReactiveFormsModule],
  templateUrl: './seasons-panel.html',
  styleUrl: '../../../../shared/admin/crud-panel.scss',
})
export class SeasonsPanel extends CrudPanel<Season, SeasonRequest> {
  protected readonly resource = inject(ConfigurationApi).seasons;
  protected readonly noun = 'la temporada';
  protected readonly form = inject(NonNullableFormBuilder).group({
    name: ['', [Validators.required, Validators.pattern(/^\d{4}-\d{2}$/)]],
    startDate: ['', Validators.required],
    endDate: ['', Validators.required],
    active: [false],
  });

  protected override emptyValue() {
    return { name: '', startDate: '', endDate: '', active: false };
  }

  protected override toFormValue(season: Season) {
    const { name, startDate, endDate, active } = season;
    return { name, startDate, endDate, active };
  }

  protected override toRequest(): SeasonRequest {
    return this.form.getRawValue();
  }

  protected override label(season: Season): string {
    return season.name;
  }

  protected override errorOf(name: string): string | null {
    if (
      name === 'name' &&
      this.form.controls.name.touched &&
      this.form.controls.name.hasError('pattern')
    ) {
      return 'Usa el formato 2026-27';
    }
    return super.errorOf(name);
  }

  /** «1 sept 2026»: fecha corta en español. */
  protected formatDate(iso: string): string {
    return new Date(`${iso}T00:00:00Z`).toLocaleDateString('es-ES', {
      day: 'numeric',
      month: 'short',
      year: 'numeric',
      timeZone: 'UTC',
    });
  }
}
