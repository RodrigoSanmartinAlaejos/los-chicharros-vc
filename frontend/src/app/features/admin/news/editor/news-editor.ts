import { Location } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Observable, switchMap } from 'rxjs';
import { formatClubDateTime } from '../../../../shared/admin/club-time';
import { ImagePicker, imageUploadError } from '../../../../shared/admin/image-picker/image-picker';
import { applyProblem, errorMessage } from '../../../../shared/admin/problem';
import { NewsApi } from '../news.api';
import {
  NEWS_STATE_LABELS,
  NEWS_TYPES,
  News,
  NewsPatch,
  NewsRequest,
  NewsType,
  newsState,
} from '../news.models';

/** Ruta del editor para una noticia nueva (`/gestion/noticias/nueva`). */
export const NEW_NEWS = 'nueva';

/**
 * Editor de una noticia (H12, wireframe 09): título, resumen, tipo, destacada y cuerpo en Markdown.
 * Se guarda como borrador y se publica o se retira desde aquí; publicar guarda antes los cambios.
 * La imagen (H14) se sube aparte, en cuanto se elige, cuando la noticia ya está guardada. La vista
 * previa y la copia del borrador en el navegador llegan con H13.
 */
@Component({
  selector: 'cv-news-editor',
  imports: [ReactiveFormsModule, RouterLink, ImagePicker],
  templateUrl: './news-editor.html',
  styleUrls: ['../../../../shared/admin/crud-panel.scss', './news-editor.scss'],
})
export class NewsEditor implements OnInit {
  private readonly api = inject(NewsApi);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly location = inject(Location);

  protected readonly types = NEWS_TYPES;
  protected readonly stateLabels = NEWS_STATE_LABELS;
  protected readonly form = inject(NonNullableFormBuilder).group({
    title: ['', [Validators.required, Validators.maxLength(150)]],
    summary: ['', [Validators.required, Validators.maxLength(300)]],
    type: ['' as NewsType | '', Validators.required],
    featured: [false],
    content: ['', [Validators.required, Validators.maxLength(50_000)]],
  });

  protected readonly status = signal<'loading' | 'ready' | 'error' | 'not-found'>('loading');
  /** La noticia tal como está guardada; `null` mientras es nueva. */
  protected readonly news = signal<News | null>(null);
  protected readonly state = computed(() => {
    const news = this.news();
    return news === null ? 'draft' : newsState(news);
  });
  protected readonly publishedAt = computed(() => {
    const date = this.news()?.publishedAt;
    return date ? formatClubDateTime(date) : null;
  });
  protected readonly saving = signal(false);
  protected readonly confirmingDelete = signal(false);
  protected readonly uploadingImage = signal(false);
  protected readonly imageError = signal<string | null>(null);
  protected readonly formError = signal<string | null>(null);
  /** Aviso para lectores de pantalla y para quien mira la pantalla tras guardar. */
  protected readonly notice = signal('');

  ngOnInit(): void {
    const param = this.route.snapshot.paramMap.get('id');
    if (param === NEW_NEWS) {
      this.status.set('ready');
      return;
    }
    this.api.get(Number(param)).subscribe({
      next: (news) => {
        this.load(news);
        this.status.set('ready');
      },
      error: (error: unknown) =>
        this.status.set(
          error instanceof HttpErrorResponse && error.status === 404 ? 'not-found' : 'error',
        ),
    });
  }

  /** Guarda sin cambiar si está publicada o no. */
  protected save(): void {
    const isDraft = this.state() === 'draft';
    this.run(
      this.saved(),
      isDraft ? 'Se ha guardado el borrador.' : 'Se han guardado los cambios.',
    );
  }

  /** Guarda los cambios y la publica con la fecha de ahora. */
  protected publish(): void {
    this.run(
      this.saved({ publishedAt: new Date().toISOString() }),
      'Se ha publicado la noticia: ya se ve en la web.',
    );
  }

  /** Guarda los cambios y la vuelve a borrador: deja de verse en la web. */
  protected withdraw(): void {
    this.run(
      this.saved({ publishedAt: null }),
      'Se ha retirado la noticia: vuelve a ser un borrador.',
    );
  }

  /** Sube la imagen sin tocar el formulario: lo que se esté escribiendo no se pierde. */
  protected uploadImage(file: File): void {
    const news = this.news();
    if (news === null) {
      return;
    }
    this.uploadingImage.set(true);
    this.imageError.set(null);
    this.notice.set('');
    this.api.uploadImage(news.id, file).subscribe({
      next: (saved) => {
        this.uploadingImage.set(false);
        this.news.update((current) => current && { ...current, imageUrl: saved.imageUrl });
        this.notice.set('Se ha cambiado la imagen.');
      },
      error: (error: unknown) => {
        this.uploadingImage.set(false);
        this.imageError.set(imageUploadError(error));
      },
    });
  }

  protected confirmDelete(): void {
    const news = this.news();
    if (news === null) {
      return;
    }
    this.saving.set(true);
    this.api.remove(news.id).subscribe({
      next: () => void this.router.navigate(['/gestion/noticias']),
      error: (error: unknown) => {
        this.saving.set(false);
        this.confirmingDelete.set(false);
        this.formError.set(
          error instanceof HttpErrorResponse && error.error?.detail
            ? error.error.detail
            : 'No se ha podido borrar la noticia.',
        );
      },
    });
  }

  protected errorOf(name: string): string | null {
    return errorMessage(this.form.get(name));
  }

  /**
   * Guarda el formulario (alta o edición) y, si hace falta, aplica después el cambio de
   * publicación. Si el formulario no es válido, no envía nada.
   */
  private saved(patch?: NewsPatch): Observable<News> | null {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return null;
    }
    const { title, summary, type, featured, content } = this.form.getRawValue();
    const request: NewsRequest = {
      title: title.trim(),
      summary: summary.trim(),
      type: type as NewsType,
      featured,
      content,
    };
    const current = this.news();
    const save = current === null ? this.api.create(request) : this.api.update(current.id, request);
    return patch === undefined
      ? save
      : save.pipe(switchMap((news) => this.api.change(news.id, patch)));
  }

  private run(call: Observable<News> | null, message: string): void {
    if (call === null) {
      this.formError.set('Revisa los campos marcados.');
      return;
    }
    this.saving.set(true);
    this.formError.set(null);
    this.notice.set('');
    call.subscribe({
      next: (news) => {
        const wasNew = this.news() === null;
        this.saving.set(false);
        this.load(news);
        this.notice.set(message);
        if (wasNew) {
          // A partir de ahora, editar esta noticia: la URL ya es la suya
          this.location.replaceState(`/gestion/noticias/${news.id}`);
        }
      },
      error: (error: unknown) => {
        this.saving.set(false);
        this.formError.set(applyProblem(this.form, error) ?? 'Revisa los campos marcados.');
      },
    });
  }

  private load(news: News): void {
    this.news.set(news);
    this.form.reset({
      title: news.title,
      summary: news.summary,
      type: news.type,
      featured: news.featured,
      content: news.contentMarkdown ?? '',
    });
  }
}
