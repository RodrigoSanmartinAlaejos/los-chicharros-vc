/**
 * En local, carga el `.env` de la raíz del repositorio (el mismo que usa el backend). En Render no
 * existe: las variables vienen del panel. Se importa antes que nada en server.ts, porque Angular lee
 * `NG_ALLOWED_HOSTS` al crear su motor.
 */
for (const file of ['../.env', '.env']) {
  try {
    process.loadEnvFile(file);
    break;
  } catch {
    // No existe: se prueba el siguiente o se sigue con las variables del sistema
  }
}
