/** Tamaño máximo del cuerpo que se reenvía a la API: el mismo que acepta la API
 * (`spring.servlet.multipart.max-request-size` en backend/src/main/resources/application.yml). */
export const API_MAX_BODY_BYTES = 6 * 1024 * 1024;

/** Cabeceras de conexión entre dos saltos: no se reenvían (RFC 9110, sección 7.6.1). */
const HOP_BY_HOP = new Set([
  'connection',
  'keep-alive',
  'proxy-authenticate',
  'proxy-authorization',
  'te',
  'trailer',
  'transfer-encoding',
  'upgrade',
]);

type Headers = Record<string, string | string[] | undefined>;

/** Quién hizo la petición original, según el servidor SSR. */
export interface ClientInfo {
  /** IP real del cliente (Express la calcula con `trust proxy`). */
  readonly ip: string;
  readonly protocol: string;
  readonly host: string;
}

/**
 * Cabeceras de la petición que se reenvía a la API.
 *
 * `X-Forwarded-For` se sustituye por la IP que ha calculado este servidor en lugar de añadirse a la
 * que mande el navegador: así un cliente no puede hacerse pasar por otra IP (bloqueo de login, logs).
 */
export function forwardedRequestHeaders(incoming: Headers, client: ClientInfo): Headers {
  const headers = withoutHopByHop(incoming);
  delete headers['host'];
  delete headers['forwarded'];
  delete headers['x-forwarded-port'];
  headers['x-forwarded-for'] = client.ip;
  headers['x-forwarded-proto'] = client.protocol;
  headers['x-forwarded-host'] = client.host;
  // Sin el puerto, la API construye URL absolutas (la cabecera Location de un 201) con el puerto por
  // defecto del protocolo, y en local falta el :4000
  headers['x-forwarded-port'] = forwardedPort(client);
  return headers;
}

function forwardedPort(client: ClientInfo): string {
  return /:(\d+)$/.exec(client.host)?.[1] ?? (client.protocol === 'https' ? '443' : '80');
}

/** Cabeceras de la respuesta de la API que se devuelven al navegador. */
export function forwardedResponseHeaders(incoming: Headers): Headers {
  return withoutHopByHop(incoming);
}

/** `true` si la petición declara un cuerpo mayor que el que acepta la API. */
export function isBodyTooLarge(contentLength: string | undefined, maxBytes: number): boolean {
  return contentLength !== undefined && Number(contentLength) > maxBytes;
}

function withoutHopByHop(incoming: Headers): Headers {
  // Además de las fijas, las que la propia cabecera Connection marca como de un solo salto
  const connection = incoming['connection'];
  const listed = (Array.isArray(connection) ? connection.join(',') : (connection ?? ''))
    .split(',')
    .map((name) => name.trim().toLowerCase())
    .filter(Boolean);
  const headers: Headers = {};
  for (const [name, value] of Object.entries(incoming)) {
    const key = name.toLowerCase();
    if (!HOP_BY_HOP.has(key) && !listed.includes(key) && value !== undefined) {
      headers[key] = value;
    }
  }
  return headers;
}
