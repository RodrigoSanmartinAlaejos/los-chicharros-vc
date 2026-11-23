// Tareas del pre-commit sobre los ficheros preparados para el commit.
// Cada herramienta se ejecuta desde su carpeta (frontend o backend) con scripts/pre-commit.mjs,
// que funciona igual en Windows (PowerShell) y en Linux o macOS.
// Los ficheros que cambian se vuelven a añadir al commit automáticamente.
export default {
  'frontend/**/*.{ts,html}': 'node scripts/pre-commit.mjs eslint',
  'frontend/**/*': 'node scripts/pre-commit.mjs prettier',
  'backend/src/**/*.java': 'node scripts/pre-commit.mjs spotless',
};
