import { HttpErrorResponse } from '@angular/common/http';
import { AbstractControl, FormGroup } from '@angular/forms';

/** Error de un campo en una respuesta RFC 7807 (docs/05-api/README.md, «Errores»). */
interface FieldViolation {
  field: string;
  message: string;
}

/**
 * Lleva los errores de la API al formulario: los de cada campo se ponen en su control (error
 * `server`) y devuelve el mensaje general que hay que mostrar, o `null` si todo encaja en campos.
 */
export function applyProblem(form: FormGroup, error: unknown): string | null {
  if (!(error instanceof HttpErrorResponse) || error.status === 0) {
    return 'No se ha podido conectar con el servidor. Inténtalo de nuevo.';
  }
  const problem = error.error as { detail?: string; errors?: FieldViolation[] } | null;
  const unmatched: string[] = [];
  for (const violation of problem?.errors ?? []) {
    const control = form.get(violation.field);
    if (control) {
      control.setErrors({ server: violation.message });
      control.markAsTouched();
    } else {
      unmatched.push(violation.message);
    }
  }
  if (problem?.errors?.length && unmatched.length === 0) {
    return null;
  }
  return problem?.detail ?? 'Ha ocurrido un error inesperado.';
}

/** Mensaje del primer error de un control, en español, o `null` si no hay que mostrar nada. */
export function errorMessage(control: AbstractControl | null): string | null {
  if (!control || !control.touched || !control.errors) {
    return null;
  }
  const errors = control.errors;
  if (errors['server']) return errors['server'] as string;
  if (errors['required']) return 'Este campo es obligatorio';
  if (errors['maxlength']) return `Como máximo ${errors['maxlength'].requiredLength} caracteres`;
  if (errors['min']) return `Debe ser ${errors['min'].min} o más`;
  if (errors['max']) return `Debe ser ${errors['max'].max} o menos`;
  if (errors['pattern']) return 'El formato no es válido';
  return 'El valor no es válido';
}
