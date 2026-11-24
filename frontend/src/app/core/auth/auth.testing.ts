/** Access token con la forma de un JWT y la claim `email`, para los tests (sin firma válida). */
export function fakeAccessToken(email = 'admin@loschicharros.example'): string {
  const encode = (value: object) =>
    btoa(JSON.stringify(value)).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
  return `${encode({ alg: 'HS256' })}.${encode({ sub: '1', email, roles: ['ADMIN'] })}.firma`;
}
