import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { Subject, catchError, map, of, switchMap } from 'rxjs';
import { formatClubDate } from '../../../shared/admin/club-time';
import { Page } from '../../../shared/admin/page';
import { NewsApi } from './news.api';
import {
  NEWS_STATE_LABELS,
  NEWS_TYPE_LABELS,
  NewsStatusFilter,
  NewsSummary,
  newsState,
} from './news.models';

/** Lo que pide el listado: estado y página. */
interface Query {
  status: NewsStatusFilter;
  page: number;
}

const FILTERS: readonly { value: NewsStatusFilter; label: string }[] = [
  { value: 'all', label: 'Todas' },
  { value: 'published', label: 'Publicadas' },
  { value: 'draft', label: 'Borradores' },
];

/**
 * Noticias del panel (H12, wireframe 08): todas, las publicadas o los borradores, por páginas. Se
 * crean y se editan en el editor (`/gestion/noticias/nueva` y `/gestion/noticias/:id`).
 */
@Component({
  selector: 'cv-news',
  imports: [RouterLink],
  templateUrl: './news.html',
  styleUrls: ['../../../shared/admin/crud-panel.scss', './news.scss'],
})
export class NewsList implements OnInit {
  private readonly api = inject(NewsApi);
  private readonly queries = new Subject<Query>();
  private query: Query = { status: 'all', page: 0 };

  protected readonly filters = FILTERS;
  protected readonly typeLabels = NEWS_TYPE_LABELS;
  protected readonly stateLabels = NEWS_STATE_LABELS;
  protected readonly state = newsState;
  protected readonly formatDate = formatClubDate;
  protected readonly status = signal<'loading' | 'ready' | 'error'>('loading');
  protected readonly result = signal<Page<NewsSummary> | null>(null);
  protected readonly filter = signal<NewsStatusFilter>('all');

  constructor() {
    // switchMap descarta la respuesta de un filtro anterior que llegue tarde
    this.queries
      .pipe(
        switchMap((query) =>
          this.api.list(query.status, query.page).pipe(
            map((page): Page<NewsSummary> | null => page),
            catchError((error: unknown) => {
              if (!(error instanceof HttpErrorResponse)) throw error;
              return of(null);
            }),
          ),
        ),
        takeUntilDestroyed(),
      )
      .subscribe((page) => {
        if (page === null) {
          this.status.set('error');
          return;
        }
        this.result.set(page);
        this.status.set('ready');
      });
  }

  ngOnInit(): void {
    this.go(this.query);
  }

  protected reload(): void {
    this.go(this.query);
  }

  protected show(status: NewsStatusFilter): void {
    this.filter.set(status);
    this.go({ status, page: 0 });
  }

  protected goToPage(page: number): void {
    this.go({ ...this.query, page });
  }

  private go(query: Query): void {
    this.query = query;
    this.status.set('loading');
    this.queries.next(query);
  }
}
