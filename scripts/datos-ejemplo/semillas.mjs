// Escribe las semillas SQL de dev y staging (backend/src/main/resources/db/seed/) a partir de los
// CSV de docs/04-datos/datos-ejemplo/. Solo las de tablas que ya tienen migración: cada historia
// añade aquí la suya (docs/04-datos/datos-ejemplo.md, «De CSV a semilla»).
//
// Las semillas son migraciones repetibles de Flyway: se vuelven a aplicar cuando cambia el fichero,
// así que cada INSERT comprueba antes que la fila no existe y nunca duplica datos.
//
// Uso: node scripts/datos-ejemplo/semillas.mjs
import { readFileSync, writeFileSync } from 'node:fs';
import path from 'node:path';

const root = path.resolve(import.meta.dirname, '..', '..');
const csvDir = path.join(root, 'docs', '04-datos', 'datos-ejemplo');
const seedDir = path.join(root, 'backend', 'src', 'main', 'resources', 'db', 'seed');

/** Lee un CSV con cabecera; admite campos entre comillas con comas y comillas dobladas dentro. */
function readCsv(name) {
  const text = readFileSync(path.join(csvDir, name), 'utf8').replace(/\r\n/g, '\n').trimEnd();
  const rows = [];
  let row = [];
  let field = '';
  let quoted = false;
  for (let i = 0; i < text.length; i++) {
    const char = text[i];
    if (quoted) {
      if (char === '"' && text[i + 1] === '"') {
        field += '"';
        i++;
      } else if (char === '"') {
        quoted = false;
      } else {
        field += char;
      }
    } else if (char === '"') {
      quoted = true;
    } else if (char === ',') {
      row.push(field);
      field = '';
    } else if (char === '\n') {
      row.push(field);
      rows.push(row);
      row = [];
      field = '';
    } else {
      field += char;
    }
  }
  row.push(field);
  rows.push(row);
  const [header, ...data] = rows;
  return data.map((values) => Object.fromEntries(header.map((key, i) => [key, values[i] ?? ''])));
}

/** Literal SQL: texto entre comillas simples, o NULL si va vacío. */
const text = (value) =>
  value === '' ? 'NULL' : `'${value.replace(/\\/g, '\\\\').replace(/'/g, "''")}'`;
const bool = (value) => (value === 'true' ? 'TRUE' : 'FALSE');

function writeSeed(file, header, statements) {
  const content = `${header.map((line) => `-- ${line}`).join('\n')}\n${statements.join('\n')}\n`;
  writeFileSync(path.join(seedDir, file), content);
  console.log(`${file}: ${statements.length} filas`);
}

// ---------- people (H16) ----------
// Sin clave única natural: la persona se reconoce por nombre, apellidos y fecha de nacimiento
// (<=> también compara los NULL). Una persona borrada en el panel no se vuelve a crear.
const people = readCsv('people.csv').map(
  (p) =>
    'INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at,' +
    ' updated_at)\n' +
    `  SELECT ${text(p.first_name)}, ${text(p.last_name)}, ${text(p.birth_date)},` +
    ` ${text(p.photo_url)}, ${bool(p.image_consent)}, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)\n` +
    '  FROM DUAL\n' +
    `  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = ${text(p.first_name)}` +
    ` AND last_name = ${text(p.last_name)} AND birth_date <=> ${text(p.birth_date)});`,
);
writeSeed(
  'R__seed_02_people.sql',
  [
    'Semilla de dev y staging: los jugadores y entrenadores de docs/04-datos/datos-ejemplo/people.csv.',
    'No se edita a mano: la escribe scripts/datos-ejemplo/semillas.mjs. Es repetible e idempotente.',
  ],
  people,
);
