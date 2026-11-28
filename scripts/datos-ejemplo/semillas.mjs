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

// ---------- teams y team_memberships (H17) ----------
// Los códigos de los CSV se convierten en subconsultas: la categoría por nombre y sexo, la
// temporada por nombre y la persona por nombre, apellidos y fecha de nacimiento.
const categories = Object.fromEntries(readCsv('categories.csv').map((c) => [c.code, c]));
const categoryId = (code) =>
  `(SELECT id FROM categories WHERE name = ${text(categories[code].name)}` +
  ` AND gender = ${text(categories[code].gender)})`;
const seasonId = (name) => `(SELECT id FROM seasons WHERE name = ${text(name)})`;

const teamRows = readCsv('teams.csv');
const teams = teamRows.map(
  (t) =>
    'INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at,' +
    ' updated_at)\n' +
    `  SELECT ${seasonId(t.season)}, ${categoryId(t.category)}, ${text(t.name)},` +
    ` ${bool(t.own_club)}, ${text(t.crest_url)}, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);`,
);

const teamByCode = Object.fromEntries(teamRows.map((t) => [t.code, t]));
const personByCode = Object.fromEntries(readCsv('people.csv').map((p) => [p.code, p]));
const memberships = readCsv('team_memberships.csv').map((m) => {
  const t = teamByCode[m.team];
  const p = personByCode[m.person];
  const shirt = m.shirt_number === '' ? 'NULL' : m.shirt_number;
  return (
    'INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position,' +
    ' created_at, updated_at)\n' +
    `  SELECT t.id, p.id, ${text(m.type)}, ${shirt}, ${text(m.position)}, UTC_TIMESTAMP(6),` +
    ' UTC_TIMESTAMP(6)\n' +
    `  FROM teams t, people p WHERE t.season_id = ${seasonId(t.season)}` +
    ` AND t.category_id = ${categoryId(t.category)} AND t.name = ${text(t.name)}` +
    ` AND p.first_name = ${text(p.first_name)} AND p.last_name = ${text(p.last_name)}` +
    ` AND p.birth_date <=> ${text(p.birth_date)} AND p.deleted_at IS NULL;`
  );
});
writeSeed(
  'R__seed_03_teams.sql',
  [
    'Semilla de dev y staging: equipos y plantillas de teams.csv y team_memberships.csv',
    '(docs/04-datos/datos-ejemplo/). No se edita a mano: la escribe scripts/datos-ejemplo/semillas.mjs.',
    'Es repetible e idempotente: INSERT IGNORE sobre las claves únicas de los equipos y las plantillas.',
  ],
  [...teams, ...memberships],
);

// ---------- competitions y matchdays (H18) ----------
// La competición se reconoce por temporada, categoría y nombre (clave única); la jornada, por su
// competición y su número.
const competitionRows = readCsv('competitions.csv');
const competitionByCode = Object.fromEntries(competitionRows.map((c) => [c.code, c]));
const competitionWhere = (c) =>
  `c.season_id = ${seasonId(c.season)} AND c.category_id = ${categoryId(c.category)}` +
  ` AND c.name = ${text(c.name)}`;
const competitions = competitionRows.map(
  (c) =>
    'INSERT IGNORE INTO competitions (season_id, category_id, name, federation,' +
    ' forfeit_penalty_points, created_at, updated_at)\n' +
    `  SELECT ${seasonId(c.season)}, ${categoryId(c.category)}, ${text(c.name)},` +
    ` ${text(c.federation)}, ${c.forfeit_penalty_points}, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);`,
);
const matchdays = readCsv('matchdays.csv').map((m) => {
  const c = competitionByCode[m.competition];
  return (
    'INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at,' +
    ' updated_at)\n' +
    `  SELECT c.id, ${m.number}, ${text(m.scheduled_date)}, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)` +
    ` FROM competitions c WHERE ${competitionWhere(c)};`
  );
});
writeSeed(
  'R__seed_04_competitions.sql',
  [
    'Semilla de dev y staging: competiciones y jornadas de competitions.csv y matchdays.csv',
    '(docs/04-datos/datos-ejemplo/). No se edita a mano: la escribe scripts/datos-ejemplo/semillas.mjs.',
    'Es repetible e idempotente: INSERT IGNORE sobre las claves únicas de las competiciones y las jornadas.',
  ],
  [...competitions, ...matchdays],
);

// ---------- matches (H19) ----------
// Sin clave única natural: el partido se reconoce por su jornada y sus dos equipos (un equipo
// juega una vez por jornada). El pabellón, por su nombre.
const teamId = (code) => {
  const t = teamByCode[code];
  return (
    `(SELECT id FROM teams WHERE season_id = ${seasonId(t.season)}` +
    ` AND category_id = ${categoryId(t.category)} AND name = ${text(t.name)})`
  );
};
const venueByCode = Object.fromEntries(readCsv('venues.csv').map((v) => [v.code, v]));
const venueId = (code) =>
  code === '' ? 'NULL' : `(SELECT id FROM venues WHERE name = ${text(venueByCode[code].name)})`;
const sets = (value) => (value === '' ? 'NULL' : value);
const matchRows = readCsv('matches.csv').map((m) => {
  const c = competitionByCode[m.competition];
  const startsAt = m.starts_at.replace('T', ' ').replace('Z', '');
  return (
    'INSERT INTO matches (matchday_id, home_team_id, away_team_id, venue_id, starts_at, status,' +
    ' home_sets, away_sets, created_at, updated_at)\n' +
    `  SELECT md.id, ${teamId(m.home_team)}, ${teamId(m.away_team)}, ${venueId(m.venue)},` +
    ` ${text(startsAt)}, ${text(m.status)}, ${sets(m.home_sets)}, ${sets(m.away_sets)},` +
    ' UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)\n' +
    `  FROM matchdays md JOIN competitions c ON c.id = md.competition_id` +
    ` WHERE ${competitionWhere(c)} AND md.number = ${m.matchday}\n` +
    `  AND NOT EXISTS (SELECT 1 FROM matches x WHERE x.matchday_id = md.id` +
    ` AND x.home_team_id = ${teamId(m.home_team)} AND x.away_team_id = ${teamId(m.away_team)});`
  );
});
writeSeed(
  'R__seed_05_matches.sql',
  [
    'Semilla de dev y staging: los partidos de matches.csv (docs/04-datos/datos-ejemplo/), con su',
    'estado y su marcador. No se edita a mano: la escribe scripts/datos-ejemplo/semillas.mjs.',
    'Es repetible e idempotente: cada partido se inserta solo si su jornada no lo tiene ya.',
  ],
  matchRows,
);

// ---------- standings (H21) ----------
// Clave única competición + equipo: INSERT IGNORE no repite una fila que ya esté.
const standingRows = readCsv('standings.csv').map((s) => {
  const c = competitionByCode[s.competition];
  return (
    'INSERT IGNORE INTO standings (competition_id, team_id, points, played, won, lost, sets_won,' +
    ' sets_lost, penalty_points, created_at, updated_at)\n' +
    `  SELECT c.id, ${teamId(s.team)}, ${s.points}, ${s.played}, ${s.won}, ${s.lost},` +
    ` ${s.sets_won}, ${s.sets_lost}, ${s.penalty_points}, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)` +
    ` FROM competitions c WHERE ${competitionWhere(c)};`
  );
});
writeSeed(
  'R__seed_06_standings.sql',
  [
    'Semilla de dev y staging: la clasificación de cada competición de standings.csv',
    '(docs/04-datos/datos-ejemplo/). No se edita a mano: la escribe scripts/datos-ejemplo/semillas.mjs.',
    'Es repetible e idempotente: INSERT IGNORE sobre la clave única competición + equipo.',
  ],
  standingRows,
);

// ---------- news_articles (H12) ----------
// Clave única slug: INSERT IGNORE no repite una noticia que ya esté, y una borrada en el panel no
// vuelve (su fila sigue ahí con deleted_at). La que no tiene published_at es un borrador.
const datetime = (value) => (value === '' ? 'NULL' : text(value.replace('T', ' ').replace('Z', '')));
const newsRows = readCsv('news_articles.csv').map(
  (n) =>
    'INSERT IGNORE INTO news_articles (title, slug, summary, content, image_url, type, featured,' +
    ' published_at, created_at, updated_at)\n' +
    `  VALUES (${text(n.title)}, ${text(n.slug)}, ${text(n.summary)}, ${text(n.content)},` +
    ` ${text(n.image_url)}, ${text(n.type)}, ${bool(n.featured)}, ${datetime(n.published_at)},` +
    ' UTC_TIMESTAMP(6), UTC_TIMESTAMP(6));',
);
writeSeed(
  'R__seed_07_news.sql',
  [
    'Semilla de dev y staging: las noticias de news_articles.csv (docs/04-datos/datos-ejemplo/).',
    'No se edita a mano: la escribe scripts/datos-ejemplo/semillas.mjs.',
    'Es repetible e idempotente: INSERT IGNORE sobre la clave única del slug.',
  ],
  newsRows,
);
