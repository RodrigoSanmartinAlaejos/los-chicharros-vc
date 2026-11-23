/** Entorno en el que corre la web; decide, por ejemplo, si los buscadores la indexan. */
export type AppEnv = 'dev' | 'staging' | 'prod';

const APP_ENVS: readonly AppEnv[] = ['dev', 'staging', 'prod'];

/** Configuración del servidor SSR, leída de las variables de entorno al arrancar. */
export interface ServerConfig {
  /** URL pública de la API, sin barra final (`API_URL`). */
  readonly apiUrl: string;
  /** Entorno (`APP_ENV`); `dev` si no se indica. */
  readonly appEnv: AppEnv;
  /**
   * Proxies delante del servidor cuyo `X-Forwarded-For` se acepta (`TRUST_PROXY`): `1` en Render,
   * que pone uno; `false` en local, donde el cliente llega directo.
   */
  readonly trustProxy: number | false;
}

type Env = Record<string, string | undefined>;

/**
 * Lee y valida la configuración. Un único build sirve para todos los entornos: lo que cambia entre
 * ellos llega por variables (reglas técnicas, «SSR», regla 7).
 *
 * @throws Error si falta `API_URL` o algún valor no es válido
 */
export function readServerConfig(env: Env): ServerConfig {
  const apiUrl = env['API_URL']?.trim().replace(/\/+$/, '');
  if (!apiUrl) {
    throw new Error('Falta la variable API_URL (en local, en el .env de la raíz)');
  }
  if (!/^https?:\/\//.test(apiUrl)) {
    throw new Error(`API_URL no es una URL http(s): ${apiUrl}`);
  }

  return {
    apiUrl,
    appEnv: parseAppEnv(env['APP_ENV']),
    trustProxy: parseTrustProxy(env['TRUST_PROXY']),
  };
}

/**
 * Entorno de `APP_ENV`; `dev` si no se indica.
 *
 * @throws Error si no es dev, staging ni prod
 */
export function parseAppEnv(value: string | undefined): AppEnv {
  const appEnv = (value?.trim() || 'dev') as AppEnv;
  if (!APP_ENVS.includes(appEnv)) {
    throw new Error(`APP_ENV debe ser dev, staging o prod, no ${appEnv}`);
  }
  return appEnv;
}

/**
 * Número de proxies de confianza delante del servidor, o `false` si no hay ninguno.
 *
 * @throws Error si el valor no es un número entero positivo
 */
export function parseTrustProxy(value: string | undefined): number | false {
  const trimmed = value?.trim();
  if (!trimmed) {
    return false;
  }
  const hops = Number(trimmed);
  if (!Number.isInteger(hops) || hops < 0) {
    throw new Error(`TRUST_PROXY debe ser un número de proxies, no ${trimmed}`);
  }
  return hops;
}
