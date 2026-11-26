import { Component, inject } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ConfigurationApi } from '../configuration.api';
import {
  CATEGORY_GENDER,
  Category,
  CategoryRequest,
  GENDER_LABELS,
  Gender,
} from '../configuration.models';
import { CrudPanel } from '../crud-panel';

/** Categorías por edad y sexo. No se pueden borrar si tienen equipos (409). */
@Component({
  selector: 'cv-categories-panel',
  imports: [ReactiveFormsModule],
  templateUrl: './categories-panel.html',
  styleUrl: '../../../../shared/admin/crud-panel.scss',
})
export class CategoriesPanel extends CrudPanel<Category, CategoryRequest> {
  protected readonly resource = inject(ConfigurationApi).categories;
  protected readonly noun = 'la categoría';
  protected readonly genders = Object.entries(GENDER_LABELS) as [Gender, string][];
  protected readonly genderLabels = GENDER_LABELS;
  protected readonly form = inject(NonNullableFormBuilder).group({
    name: ['', [Validators.required, Validators.maxLength(30)]],
    gender: ['FEMALE' as Gender, Validators.required],
    sortOrder: [0, [Validators.required, Validators.min(0), Validators.max(32767)]],
  });

  protected override emptyValue() {
    const next = Math.max(0, ...this.items().map((category) => category.sortOrder)) + 1;
    return { name: '', gender: 'FEMALE' as Gender, sortOrder: next };
  }

  protected override toFormValue(category: Category) {
    const { name, gender, sortOrder } = category;
    return { name, gender, sortOrder };
  }

  protected override toRequest(): CategoryRequest {
    return this.form.getRawValue();
  }

  protected override label(category: Category): string {
    return `${category.name} ${CATEGORY_GENDER[category.gender]}`;
  }
}
