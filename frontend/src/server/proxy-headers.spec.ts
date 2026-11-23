import { forwardedRequestHeaders, forwardedResponseHeaders, isBodyTooLarge } from './proxy-headers';

describe('forwardedRequestHeaders', () => {
  const client = { ip: '203.0.113.7', protocol: 'https', host: 'chicharros.example.com' };

  it('sustituye X-Forwarded-For por la IP real, sin fiarse de la que manda el navegador', () => {
    const headers = forwardedRequestHeaders(
      { 'x-forwarded-for': '1.2.3.4', host: 'chicharros.example.com', accept: 'application/json' },
      client,
    );

    expect(headers).toEqual({
      accept: 'application/json',
      'x-forwarded-for': '203.0.113.7',
      'x-forwarded-proto': 'https',
      'x-forwarded-host': 'chicharros.example.com',
    });
  });

  it('mantiene las cookies y la autorización, y quita las cabeceras de un solo salto', () => {
    const headers = forwardedRequestHeaders(
      {
        cookie: 'refresh=abc',
        authorization: 'Bearer token',
        connection: 'keep-alive, x-salto',
        'keep-alive': 'timeout=5',
        'x-salto': '1',
        'transfer-encoding': 'chunked',
      },
      client,
    );

    expect(headers['cookie']).toBe('refresh=abc');
    expect(headers['authorization']).toBe('Bearer token');
    expect(headers).not.toHaveProperty('connection');
    expect(headers).not.toHaveProperty('keep-alive');
    expect(headers).not.toHaveProperty('x-salto');
    expect(headers).not.toHaveProperty('transfer-encoding');
  });
});

describe('forwardedResponseHeaders', () => {
  it('devuelve las cabeceras de la API salvo las de un solo salto', () => {
    expect(
      forwardedResponseHeaders({ 'set-cookie': ['refresh=abc'], connection: 'close' }),
    ).toEqual({ 'set-cookie': ['refresh=abc'] });
  });
});

describe('isBodyTooLarge', () => {
  it('compara el Content-Length con el máximo', () => {
    expect(isBodyTooLarge('100', 50)).toBe(true);
    expect(isBodyTooLarge('50', 50)).toBe(false);
    expect(isBodyTooLarge(undefined, 50)).toBe(false);
  });
});
