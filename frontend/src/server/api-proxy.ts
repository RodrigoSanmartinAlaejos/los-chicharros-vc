import type { NextFunction, Request, RequestHandler, Response } from 'express';
import { request as httpRequest } from 'node:http';
import { request as httpsRequest } from 'node:https';
import {
  API_MAX_BODY_BYTES,
  forwardedRequestHeaders,
  forwardedResponseHeaders,
  isBodyTooLarge,
} from './proxy-headers';

/** Lo que puede tardar la API dormida de Render en despertar y responder. */
const API_TIMEOUT_MS = 120_000;

/**
 * Reenvía `/api/**` a la API (ADR-004): el navegador solo habla con el frontend, así que la cookie
 * de refresco es de primera parte y la API no necesita CORS. El cuerpo y la respuesta pasan en
 * streaming, sin guardarse en memoria.
 *
 * @param apiUrl devuelve la URL de la API; si lanza un error (falta `API_URL`), se responde 503
 */
export function createApiProxy(apiUrl: () => string): RequestHandler {
  return (req: Request, res: Response, next: NextFunction) => {
    if (isBodyTooLarge(req.headers['content-length'], API_MAX_BODY_BYTES)) {
      sendProblem(res, 413, 'La petición supera el tamaño máximo permitido');
      return;
    }

    let target: URL;
    try {
      target = new URL(req.originalUrl, apiUrl());
    } catch (error) {
      console.error(`No se puede reenviar ${req.originalUrl} a la API: ${String(error)}`);
      sendProblem(res, 503, 'La API no está configurada');
      return;
    }

    const send = target.protocol === 'https:' ? httpsRequest : httpRequest;
    const proxyReq = send(target, {
      method: req.method,
      headers: forwardedRequestHeaders(req.headers, {
        ip: req.ip ?? req.socket.remoteAddress ?? '',
        protocol: req.protocol,
        host: req.get('host') ?? '',
      }),
      timeout: API_TIMEOUT_MS,
    });

    proxyReq.on('response', (proxyRes) => {
      res.status(proxyRes.statusCode ?? 502);
      for (const [name, value] of Object.entries(forwardedResponseHeaders(proxyRes.headers))) {
        if (value !== undefined) {
          res.setHeader(name, value);
        }
      }
      proxyRes.pipe(res);
    });
    proxyReq.on('timeout', () => proxyReq.destroy(new Error('Tiempo de espera agotado')));
    proxyReq.on('error', (error) => {
      console.warn(`La API no respondió a ${req.method} ${req.originalUrl}: ${error.message}`);
      if (res.headersSent) {
        res.destroy(error);
      } else {
        sendProblem(res, 502, 'La API no está disponible en este momento');
      }
    });

    req.on('error', next);
    req.pipe(proxyReq);
  };
}

/** Error con el mismo formato RFC 7807 que los de la API. */
function sendProblem(res: Response, status: number, detail: string): void {
  res.status(status).type('application/problem+json').json({ type: 'about:blank', status, detail });
}
