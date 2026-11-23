import { parseTrustProxy, readServerConfig } from './server-config';

describe('readServerConfig', () => {
  it('lee la API sin barra final, el entorno y los proxies de confianza', () => {
    const config = readServerConfig({
      API_URL: 'https://api.example.com/',
      APP_ENV: 'staging',
      TRUST_PROXY: '1',
    });

    expect(config).toEqual({ apiUrl: 'https://api.example.com', appEnv: 'staging', trustProxy: 1 });
  });

  it('usa dev y ningún proxy de confianza si no se indican', () => {
    const config = readServerConfig({ API_URL: 'http://localhost:8080' });

    expect(config.appEnv).toBe('dev');
    expect(config.trustProxy).toBe(false);
  });

  it('falla si falta API_URL o no es una URL http', () => {
    expect(() => readServerConfig({})).toThrow('Falta la variable API_URL');
    expect(() => readServerConfig({ API_URL: 'api.example.com' })).toThrow('no es una URL');
  });

  it('falla si el entorno o los proxies no son válidos', () => {
    expect(() => readServerConfig({ API_URL: 'http://api', APP_ENV: 'pre' })).toThrow('APP_ENV');
    expect(() => parseTrustProxy('-1')).toThrow('TRUST_PROXY');
  });
});
