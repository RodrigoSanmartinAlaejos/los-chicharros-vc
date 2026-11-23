import './server/load-env';
import {
  AngularNodeAppEngine,
  createNodeRequestHandler,
  isMainModule,
  writeResponseToNodeResponse,
} from '@angular/ssr/node';
import express from 'express';
import { join } from 'node:path';
import { createApiProxy } from './server/api-proxy';
import { ServerConfig, parseTrustProxy, readServerConfig } from './server/server-config';
import { wakeApi } from './server/wake-api';

const browserDistFolder = join(import.meta.dirname, '../browser');

const app = express();
const angularApp = new AngularNodeAppEngine();

// La configuración se lee al usarla: si falta API_URL, el resto de la web sigue funcionando y el
// error sale en el log y en las peticiones a la API.
let config: ServerConfig | undefined;
function serverConfig(): ServerConfig {
  config ??= readServerConfig(process.env);
  return config;
}

// IP real del cliente detrás del proxy de Render (TRUST_PROXY); en local, la de la conexión
app.set('trust proxy', parseTrustProxy(process.env['TRUST_PROXY']));

/** Proxy de la API (ADR-004). En `pnpm start` lo hace antes proxy.conf.json. */
app.use(
  '/api',
  createApiProxy(() => serverConfig().apiUrl),
);

/**
 * Serve static files from /browser
 */
app.use(
  express.static(browserDistFolder, {
    maxAge: '1y',
    index: false,
    redirect: false,
  }),
);

/**
 * Handle all other requests by rendering the Angular application.
 */
app.use((req, res, next) => {
  angularApp
    .handle(req)
    .then((response) => (response ? writeResponseToNodeResponse(response, res) : next()))
    .catch(next);
});

/**
 * Start the server if this module is the main entry point, or it is ran via PM2.
 * The server listens on the port defined by the `PORT` environment variable, or defaults to 4000.
 */
if (isMainModule(import.meta.url) || process.env['pm_id']) {
  const { apiUrl, appEnv } = serverConfig();
  const port = process.env['PORT'] || 4000;
  app.listen(port, (error) => {
    if (error) {
      throw error;
    }

    console.log(`Node Express server listening on http://localhost:${port} (${appEnv})`);
    // Sin await: el servidor ya atiende mientras la API despierta
    void wakeApi(apiUrl);
  });
}

/**
 * Request handler used by the Angular CLI (for dev-server and during build) or Firebase Cloud Functions.
 */
export const reqHandler = createNodeRequestHandler(app);
