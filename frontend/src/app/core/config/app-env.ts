import {
  EnvironmentProviders,
  InjectionToken,
  TransferState,
  inject,
  makeEnvironmentProviders,
  makeStateKey,
  provideAppInitializer,
} from '@angular/core';

import type { AppEnv } from '../../../server/server-config';

export type { AppEnv };

const APP_ENV_KEY = makeStateKey<AppEnv>('appEnv');

/**
 * Entorno de la web. El servidor lo lee de `APP_ENV` y se lo pasa al navegador con
 * `TransferState` dentro del HTML: un único build sirve para todos los entornos.
 */
export const APP_ENV = new InjectionToken<AppEnv>('APP_ENV', {
  providedIn: 'root',
  factory: () => inject(TransferState).get(APP_ENV_KEY, 'dev'),
});

/** Solo en el servidor: fija el entorno y lo guarda para el navegador. */
export function provideServerAppEnv(appEnv: AppEnv): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: APP_ENV, useValue: appEnv },
    provideAppInitializer(() => inject(TransferState).set(APP_ENV_KEY, appEnv)),
  ]);
}
