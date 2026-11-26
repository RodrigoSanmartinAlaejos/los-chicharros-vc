import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  AbstractControl,
  FormControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { Subject, catchError, debounceTime, distinctUntilChanged, map, of, switchMap } from 'rxjs';
import { Page } from '../../../shared/admin/page';
import { applyProblem, errorMessage } from '../../../shared/admin/problem';
import { PeopleApi } from './people.api';
import { Person, PersonRequest } from './people.models';

/** Hoy en formato ISO (AAAA-MM-DD), en la hora local del navegador. */
function todayIso(): string {
  const now = new Date();
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}`;
}

/** La fecha de nacimiento no puede ser futura (la API también lo comprueba). */
function notFuture(control: AbstractControl<string>): ValidationErrors | null {
  return control.value && control.value > todayIso() ? { future: true } : null;
}

/** Lo que pide el listado: texto buscado y página. */
interface Query {
  search: string;
  page: number;
}

/**
 * Personas del club (H16): buscador por nombre o apellidos, listado por páginas, alta, edición y
 * borrado. El borrado es lógico: la persona desaparece del panel, pero sigue en las plantillas y
 * resultados de temporadas pasadas.
 */
@Component({
  selector: 'cv-people',
  imports: [ReactiveFormsModule],
  templateUrl: './people.html',
  styleUrls: ['../../../shared/admin/crud-panel.scss', './people.scss'],
})
export class People implements OnInit {
  private readonly api = inject(PeopleApi);
  private readonly queries = new Subject<Query>();
  private query: Query = { search: '', page: 0 };

  protected readonly searchControl = new FormControl('', { nonNullable: true });
  protected readonly form = inject(NonNullableFormBuilder).group({
    firstName: ['', [Validators.required, Validators.maxLength(60)]],
    lastName: ['', [Validators.required, Validators.maxLength(100)]],
    birthDate: ['', notFuture],
    imageConsent: [false],
  });
  /** Hoy, para que el calendario no deje elegir una fecha de nacimiento futura. */
  protected readonly today = todayIso();

  protected readonly status = signal<'loading' | 'ready' | 'error'>('loading');
  protected readonly result = signal<Page<Person> | null>(null);
  protected readonly searched = signal('');
  protected readonly formOpen = signal(false);
  protected readonly editingId = signal<number | null>(null);
  protected readonly confirmingId = signal<number | null>(null);
  protected readonly saving = signal(false);
  protected readonly formError = signal<string | null>(null);
  protected readonly listError = signal<string | null>(null);
  /** Aviso para lectores de pantalla tras buscar, guardar o borrar. */
  protected readonly notice = signal('');

  constructor() {
    // switchMap descarta la respuesta de una búsqueda anterior que llegue tarde
    this.queries
      .pipe(
        switchMap((query) =>
          this.api.search(query.search, query.page).pipe(
            map((page) => ({ query, page })),
            catchError(() => of({ query, page: null })),
          ),
        ),
        takeUntilDestroyed(),
      )
      .subscribe(({ query, page }) => {
        if (page === null) {
          this.status.set('error');
          return;
        }
        this.result.set(page);
        this.searched.set(query.search);
        this.status.set('ready');
      });
    this.searchControl.valueChanges
      .pipe(
        debounceTime(300),
        map((text) => text.trim()),
        distinctUntilChanged(),
        takeUntilDestroyed(),
      )
      .subscribe((search) => {
        this.go({ search, page: 0 });
        this.notice.set('');
      });
  }

  ngOnInit(): void {
    this.go(this.query);
  }

  protected reload(): void {
    this.go(this.query);
  }

  protected goToPage(page: number): void {
    this.go({ ...this.query, page });
  }

  protected startCreate(): void {
    this.openForm(null, { firstName: '', lastName: '', birthDate: '', imageConsent: false });
  }

  protected startEdit(person: Person): void {
    const { firstName, lastName, birthDate, imageConsent } = person;
    this.openForm(person.id, { firstName, lastName, birthDate: birthDate ?? '', imageConsent });
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
    const { firstName, lastName, birthDate, imageConsent } = this.form.getRawValue();
    const request: PersonRequest = {
      firstName: firstName.trim(),
      lastName: lastName.trim(),
      birthDate: birthDate || null,
      imageConsent,
    };
    const id = this.editingId();
    const call = id === null ? this.api.create(request) : this.api.update(id, request);
    this.saving.set(true);
    this.formError.set(null);
    call.subscribe({
      next: (saved) => {
        this.saving.set(false);
        this.cancel();
        this.notice.set(`Se ha guardado a ${this.fullName(saved)}.`);
        this.reload();
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.formError.set(applyProblem(this.form, error));
      },
    });
  }

  protected askDelete(person: Person): void {
    this.listError.set(null);
    this.confirmingId.set(person.id);
  }

  protected confirmDelete(person: Person): void {
    this.api.remove(person.id).subscribe({
      next: () => {
        this.confirmingId.set(null);
        this.notice.set(`Se ha borrado a ${this.fullName(person)}.`);
        // Si era la última persona de la página, vuelve a la anterior
        const page = this.result();
        const lastOnPage = page !== null && page.content.length === 1 && this.query.page > 0;
        this.go({ ...this.query, page: lastOnPage ? this.query.page - 1 : this.query.page });
      },
      error: (error: unknown) => {
        this.confirmingId.set(null);
        this.listError.set(
          error instanceof HttpErrorResponse && error.error?.detail
            ? error.error.detail
            : `No se ha podido borrar a ${this.fullName(person)}.`,
        );
      },
    });
  }

  protected errorOf(name: string): string | null {
    const control = this.form.get(name);
    if (control?.touched && control.errors?.['future']) {
      return 'No puede ser una fecha futura';
    }
    return errorMessage(control);
  }

  protected fullName(person: Person): string {
    return `${person.firstName} ${person.lastName}`;
  }

  /** «14/03/2011 (15 años)»; sin fecha, un guion. */
  protected formatBirthDate(iso: string | null): string {
    if (!iso) {
      return '—';
    }
    const [year, month, day] = iso.split('-');
    return `${day}/${month}/${year} (${this.age(iso)} años)`;
  }

  private age(iso: string): number {
    const [year, month, day] = iso.split('-').map(Number);
    const now = new Date();
    const birthdayPassed =
      now.getMonth() + 1 > month || (now.getMonth() + 1 === month && now.getDate() >= day);
    return now.getFullYear() - year - (birthdayPassed ? 0 : 1);
  }

  private go(query: Query): void {
    this.query = query;
    this.listError.set(null);
    if (this.result() === null) {
      this.status.set('loading');
    }
    this.queries.next(query);
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
