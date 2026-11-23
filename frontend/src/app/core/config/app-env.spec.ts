import { TestBed } from '@angular/core/testing';
import { ApplicationInitStatus, TransferState, makeStateKey } from '@angular/core';
import { APP_ENV, provideServerAppEnv } from './app-env';

describe('APP_ENV', () => {
  it('en el navegador vale dev si el servidor no lo ha pasado', () => {
    expect(TestBed.inject(APP_ENV)).toBe('dev');
  });

  it('en el navegador lee el entorno que dejó el servidor en el HTML', () => {
    TestBed.inject(TransferState).set(makeKey(), 'staging');

    expect(TestBed.inject(APP_ENV)).toBe('staging');
  });

  it('en el servidor lo guarda para el navegador al arrancar', async () => {
    TestBed.configureTestingModule({ providers: [provideServerAppEnv('prod')] });
    await TestBed.inject(ApplicationInitStatus).donePromise;

    expect(TestBed.inject(APP_ENV)).toBe('prod');
    expect(TestBed.inject(TransferState).get(makeKey(), 'dev')).toBe('prod');
  });
});

/** La misma clave que usa app-env.ts. */
function makeKey() {
  return makeStateKey<string>('appEnv');
}
