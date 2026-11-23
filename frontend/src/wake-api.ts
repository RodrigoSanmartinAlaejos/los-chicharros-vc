/** Tiempo máximo de espera: lo que tarda en despertar la API dormida en Render, con margen. */
export const WAKE_TIMEOUT_MS = 120_000;

/**
 * Lanza una petición a `/actuator/health` de la API para despertarla.
 *
 * En Render, los dos servicios se duermen sin visitas. Si el frontend acaba de arrancar, la API
 * seguramente también está dormida: así despiertan a la vez y la primera visita no espera dos
 * arranques seguidos (ADR-004 y reglas técnicas, «Despliegue»). Nunca falla: el resultado solo se
 * registra en el log, y el servidor no espera a la respuesta.
 */
export function wakeApi(apiUrl: string, fetchFn: typeof fetch = fetch): Promise<void> {
  const url = `${apiUrl.replace(/\/+$/, '')}/actuator/health`;
  return fetchFn(url, { signal: AbortSignal.timeout(WAKE_TIMEOUT_MS) })
    .then((response) => console.log(`API despierta: ${url} respondió ${response.status}`))
    .catch((error: unknown) => console.warn(`La API no respondió a ${url}: ${String(error)}`));
}
