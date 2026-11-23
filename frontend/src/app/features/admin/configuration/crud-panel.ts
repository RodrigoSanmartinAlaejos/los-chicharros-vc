import { HttpErrorResponse } from '@angular/common/http';
import { Directive, OnInit, signal } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { CrudResource } from './configuration.api';
import { applyProblem, errorMessage } from './problem';

/**
 * Lógica común de los paneles de configuración: cargar la lista, crear y editar con un formulario,
 * y borrar con confirmación. Cada panel define su formulario y su plantilla.
 */
@Directive()
export abstract class CrudPanel<T extends { id: number }, R> implements OnInit {
  protected abstract readonly resource: CrudResource<T, R>;
  protected abstract readonly form: FormGroup;
  /** Nombre del recurso para los mensajes: «la temporada», «la categoría»… */
  protected abstract readonly noun: string;

  protected readonly status = signal<'loading' | 'ready' | 'error'>('loading');
  protected readonly items = signal<T[]>([]);
  protected readonly formOpen = signal(false);
  protected readonly editingId = signal<number | null>(null);
  protected readonly confirmingId = signal<number | null>(null);
  protected readonly saving = signal(false);
  protected readonly formError = signal<string | null>(null);
  protected readonly listError = signal<string | null>(null);
  /** Aviso para lectores de pantalla tras guardar o borrar. */
  protected readonly notice = signal('');

  /** Valores del formulario vacío. */
  protected abstract emptyValue(): object;
  /** Valores del formulario para editar una fila. */
  protected abstract toFormValue(item: T): object;
  /** Petición a partir del formulario. */
  protected abstract toRequest(): R;
  /** Cómo se nombra una fila en los mensajes («2026-27», «Cadete femenino»…). */
  protected abstract label(item: T): string;

  ngOnInit(): void {
    this.load();
  }

  protected load(): void {
    this.status.set('loading');
    this.resource.list().subscribe({
      next: (items) => {
        this.items.set(items);
        this.status.set('ready');
      },
      error: () => this.status.set('error'),
    });
  }

  protected startCreate(): void {
    this.openForm(null, this.emptyValue());
  }

  protected startEdit(item: T): void {
    this.openForm(item.id, this.toFormValue(item));
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
    const id = this.editingId();
    const request = this.toRequest();
    const call: Observable<T> =
      id === null ? this.resource.create(request) : this.resource.update(id, request);
    this.saving.set(true);
    this.formError.set(null);
    call.subscribe({
      next: (saved) => {
        this.saving.set(false);
        this.cancel();
        this.notice.set(`Se ha guardado ${this.noun} ${this.label(saved)}.`);
        // Recarga la lista: guardar puede cambiar otras filas (la temporada activa) y el orden
        this.load();
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.formError.set(applyProblem(this.form, error));
      },
    });
  }

  protected askDelete(item: T): void {
    this.listError.set(null);
    this.confirmingId.set(item.id);
  }

  protected confirmDelete(item: T): void {
    this.resource.remove(item.id).subscribe({
      next: () => {
        this.confirmingId.set(null);
        this.items.update((items) => items.filter((other) => other.id !== item.id));
        this.notice.set(`Se ha borrado ${this.noun} ${this.label(item)}.`);
      },
      error: (error: unknown) => {
        this.confirmingId.set(null);
        this.listError.set(
          error instanceof HttpErrorResponse && error.error?.detail
            ? error.error.detail
            : `No se ha podido borrar ${this.noun} ${this.label(item)}.`,
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
