import { ApplicationConfig, mergeApplicationConfig } from '@angular/core';
import { provideServerRendering, withRoutes } from '@angular/ssr';
import { parseAppEnv, readServerConfig } from '../server/server-config';
import { appConfig } from './app.config';
import { serverRoutes } from './app.routes.server';
import { API_ORIGIN } from './core/config/api-origin';
import { provideServerAppEnv } from './core/config/app-env';

const env = process.env;

const serverConfig: ApplicationConfig = {
  providers: [
    provideServerRendering(withRoutes(serverRoutes)),
    // Al compilar no hay API_URL: solo se exige al renderizar una petición
    { provide: API_ORIGIN, useFactory: () => readServerConfig(env).apiUrl },
    provideServerAppEnv(parseAppEnv(env['APP_ENV'])),
  ],
};

export const config = mergeApplicationConfig(appConfig, serverConfig);
