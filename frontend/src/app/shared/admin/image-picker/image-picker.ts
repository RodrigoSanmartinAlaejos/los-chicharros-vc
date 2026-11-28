import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, input, output, signal } from '@angular/core';

/** Tipos que acepta la API (contrato OpenAPI: JPG, PNG o WebP de 5 MB como máximo). */
export const IMAGE_TYPES = ['image/jpeg', 'image/png', 'image/webp'] as const;
export const MAX_IMAGE_BYTES = 5 * 1024 * 1024;

let nextId = 0;

/** Mensaje de un error de la API al subir una imagen: el del campo, el general o uno genérico. */
export function imageUploadError(error: unknown): string {
  if (error instanceof HttpErrorResponse && error.status === 413) {
    return 'La imagen pesa más de 5 MB: elige una más pequeña.';
  }
  const problem = error instanceof HttpErrorResponse ? error.error : null;
  return problem?.errors?.[0]?.message ?? problem?.detail ?? 'No se ha podido subir la imagen.';
}

/**
 * Imagen de una noticia, una persona o un equipo (H14, wireframe 09): muestra la actual y deja
 * cambiarla eligiendo un fichero o arrastrándolo. Comprueba el tipo y el tamaño antes de avisar con
 * `picked`; quien lo usa la sube y le pasa `uploading` y, si falla, `error`.
 */
@Component({
  selector: 'cv-image-picker',
  templateUrl: './image-picker.html',
  styleUrl: './image-picker.scss',
})
export class ImagePicker {
  /** Texto de la etiqueta del campo, por ejemplo «Imagen» o «Escudo». */
  readonly label = input.required<string>();
  /** URL de la imagen actual, o `null` si aún no tiene. */
  readonly imageUrl = input<string | null>(null);
  /** Texto alternativo de la imagen actual. */
  readonly alt = input('');
  /** Si se está subiendo: desactiva el campo y lo dice. */
  readonly uploading = input(false);
  /** Error de la API al subirla, para mostrarlo junto al campo. */
  readonly error = input<string | null>(null);
  /** Fichero elegido, ya comprobado (tipo y tamaño). */
  readonly picked = output<File>();

  protected readonly id = `image-picker-${nextId++}`;
  protected readonly dragging = signal(false);
  private readonly localError = signal<string | null>(null);
  protected readonly message = computed(() => this.localError() ?? this.error());

  protected choose(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.check(input.files?.[0]);
    // Permite volver a elegir el mismo fichero
    input.value = '';
  }

  protected drop(event: DragEvent): void {
    event.preventDefault();
    this.dragging.set(false);
    if (!this.uploading()) {
      this.check(event.dataTransfer?.files[0]);
    }
  }

  protected dragOver(event: DragEvent): void {
    event.preventDefault();
    this.dragging.set(true);
  }

  private check(file: File | undefined): void {
    if (!file) {
      return;
    }
    if (!(IMAGE_TYPES as readonly string[]).includes(file.type)) {
      this.localError.set('Tiene que ser una imagen JPG, PNG o WebP.');
      return;
    }
    if (file.size > MAX_IMAGE_BYTES) {
      this.localError.set('La imagen pesa más de 5 MB: elige una más pequeña.');
      return;
    }
    this.localError.set(null);
    this.picked.emit(file);
  }
}
