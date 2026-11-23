import { wakeApi } from './wake-api';

describe('wakeApi', () => {
  beforeEach(() => {
    vi.spyOn(console, 'log').mockImplementation(() => undefined);
    vi.spyOn(console, 'warn').mockImplementation(() => undefined);
  });

  afterEach(() => vi.restoreAllMocks());

  it('pide el health de la API sin duplicar la barra final', async () => {
    const fetchFn = vi.fn().mockResolvedValue(new Response('{"status":"UP"}'));

    await wakeApi('http://localhost:8080/', fetchFn);

    expect(fetchFn).toHaveBeenCalledOnce();
    expect(fetchFn.mock.calls[0][0]).toBe('http://localhost:8080/actuator/health');
    expect(fetchFn.mock.calls[0][1].signal).toBeInstanceOf(AbortSignal);
  });

  it('no falla si la API no responde', async () => {
    const fetchFn = vi.fn().mockRejectedValue(new Error('ECONNREFUSED'));

    await expect(wakeApi('http://api.invalid', fetchFn)).resolves.toBeUndefined();
    expect(console.warn).toHaveBeenCalledWith(
      'La API no respondió a http://api.invalid/actuator/health: Error: ECONNREFUSED',
    );
  });
});
