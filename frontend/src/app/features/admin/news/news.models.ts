/** Tipo de noticia (contrato OpenAPI, `NewsType`). */
export type NewsType = 'MATCH_REPORT' | 'EVENT' | 'STATEMENT';

export const NEWS_TYPES: readonly { value: NewsType; label: string }[] = [
  { value: 'MATCH_REPORT', label: 'Crónica' },
  { value: 'EVENT', label: 'Evento' },
  { value: 'STATEMENT', label: 'Comunicado' },
];

export const NEWS_TYPE_LABELS: Record<NewsType, string> = Object.fromEntries(
  NEWS_TYPES.map(({ value, label }) => [value, label]),
) as Record<NewsType, string>;

/** Filtro de estado del listado: `draft` y `all` son solo del administrador. */
export type NewsStatusFilter = 'all' | 'published' | 'draft';

/** Noticia sin el cuerpo, para los listados (`NewsSummary`). */
export interface NewsSummary {
  id: number;
  title: string;
  slug: string;
  summary: string;
  imageUrl: string | null;
  type: NewsType;
  featured: boolean;
  /** Sin fecha es un borrador; con una fecha futura, está programada. */
  publishedAt: string | null;
}

/** Noticia completa (`NewsResponse`); el panel recibe también el Markdown. */
export interface News extends NewsSummary {
  contentHtml: string;
  contentMarkdown?: string;
}

/** Lo que se escribe en el editor (`NewsRequest`). */
export interface NewsRequest {
  title: string;
  summary: string;
  content: string;
  type: NewsType;
  featured: boolean;
}

/** Publicar (`publishedAt` con fecha), retirar (`null`) o destacar (`NewsPatchRequest`). */
export interface NewsPatch {
  publishedAt?: string | null;
  featured?: boolean;
}

/** Estado que se muestra: no hay columna de estado, se deduce de la fecha de publicación. */
export type NewsState = 'draft' | 'scheduled' | 'published';

export function newsState(news: Pick<NewsSummary, 'publishedAt'>, now = new Date()): NewsState {
  if (news.publishedAt === null) {
    return 'draft';
  }
  return new Date(news.publishedAt) > now ? 'scheduled' : 'published';
}

export const NEWS_STATE_LABELS: Record<NewsState, string> = {
  draft: 'Borrador',
  scheduled: 'Programada',
  published: 'Publicada',
};
