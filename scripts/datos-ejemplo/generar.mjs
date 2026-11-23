// Genera los datos de ejemplo de la temporada 2026-27 en CSV (docs/04-datos/datos-ejemplo/).
// Son ficticios y coherentes con la ficha del club (docs/01-vision/club.md) y el diccionario de
// datos de la fase 1. El resultado es siempre el mismo: usa un generador pseudoaleatorio con semilla.
// La semilla SQL de db/seed se escribirá a partir de estos CSV cuando existan las migraciones.
//
// Uso: node scripts/datos-ejemplo/generar.mjs [--corte=2026-12-20]
// --corte: los partidos anteriores a esa fecha salen jugados (FINISHED) y los demás programados.
import { mkdirSync, writeFileSync } from 'node:fs';
import path from 'node:path';

const root = path.resolve(import.meta.dirname, '..', '..');
const outDir = path.join(root, 'docs', '04-datos', 'datos-ejemplo');
const cutArg = process.argv.find((a) => a.startsWith('--corte='));
const CUT_DATE = cutArg ? cutArg.slice('--corte='.length) : '2026-12-20';
const SEASON = '2026-27';
const OWN = 'LCV';

// Generador pseudoaleatorio determinista (mulberry32)
let seed = 20262027;
function random() {
  seed = (seed + 0x6d2b79f5) | 0;
  let t = Math.imul(seed ^ (seed >>> 15), 1 | seed);
  t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t;
  return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
}
const pick = (list) => list[Math.floor(random() * list.length)];
const between = (min, max) => min + Math.floor(random() * (max - min + 1));

// ---------- Fechas: hora local de Canarias (WET/WEST) a UTC ----------
function lastSunday(year, monthIndex) {
  const d = new Date(Date.UTC(year, monthIndex + 1, 0));
  d.setUTCDate(d.getUTCDate() - d.getUTCDay());
  return d;
}
// Horario de verano en Canarias: del último domingo de marzo al último de octubre, a la 01:00 UTC
function canaryOffsetHours(utcGuess) {
  const y = utcGuess.getUTCFullYear();
  const start = lastSunday(y, 2);
  start.setUTCHours(1);
  const end = lastSunday(y, 9);
  end.setUTCHours(1);
  return utcGuess >= start && utcGuess < end ? 1 : 0;
}
function localToUtc(date, time) {
  const guess = new Date(`${date}T${time}:00Z`);
  const utc = new Date(guess.getTime() - canaryOffsetHours(guess) * 3600_000);
  return utc.toISOString().replace('.000Z', 'Z');
}
function addDays(date, days) {
  const d = new Date(`${date}T00:00:00Z`);
  d.setUTCDate(d.getUTCDate() + days);
  return d.toISOString().slice(0, 10);
}

// ---------- Catálogo ----------
const CATEGORY_DEFS = [
  { key: 'ALEVIN', name: 'Alevín', years: [2015, 2016], teams: 6, players: 10 },
  { key: 'INFANTIL', name: 'Infantil', years: [2013, 2014], teams: 6, players: 12 },
  { key: 'CADETE', name: 'Cadete', years: [2011, 2012], teams: 8, players: 12 },
  { key: 'JUVENIL', name: 'Juvenil', years: [2009, 2010], teams: 6, players: 12 },
  { key: 'SENIOR', name: 'Sénior', years: [1990, 2008], teams: 8, players: 12 },
];
const GENDERS = [
  { key: 'M', value: 'MALE', label: 'masculina', team: 'masculino' },
  { key: 'F', value: 'FEMALE', label: 'femenina', team: 'femenino' },
];
const categories = [];
CATEGORY_DEFS.forEach((c, i) =>
  GENDERS.forEach((g, j) =>
    categories.push({
      code: `${c.key}_${g.key}`,
      name: c.name,
      gender: g.value,
      sort_order: i * 2 + j + 1,
      def: c,
      g,
    }),
  ),
);

const RIVALS = [
  { sigla: 'CVA', name: 'Club Voleibol Alisios' },
  { sigla: 'VBA', name: 'Vóley Barlovento' },
  { sigla: 'CVM', name: 'Club Voleibol Malpaís' },
  { sigla: 'RQV', name: 'Roque Vóley' },
  { sigla: 'CVT', name: 'Club Voleibol Tabaiba' },
  { sigla: 'ANV', name: 'Atlántico Norte Vóley' },
  { sigla: 'CCA', name: 'Club Voleibol Cardonal Alto' },
];

const venues = [
  {
    code: 'PUERTO',
    name: 'Pabellón del Puerto',
    address: 'Avenida Marítima, 12, Santa Cruz de Tenerife',
    latitude: '28.468200',
    longitude: '-16.246600',
  },
  {
    code: 'MARINA',
    name: 'Polideportivo La Marina',
    address: 'Calle La Marina, 3, Santa Cruz de Tenerife',
    latitude: '28.471500',
    longitude: '-16.250300',
  },
];
const isCantera = (cat) => !['JUVENIL', 'SENIOR'].includes(cat.def.key);

const seasons = [{ name: SEASON, start_date: '2026-09-01', end_date: '2027-06-30', active: 'true' }];

// ---------- Equipos y competiciones ----------
const competitions = [];
const teams = [];
const strength = new Map();
// Nivel orientativo de los equipos del club (0-1): decide los resultados
const OWN_STRENGTH = {
  ALEVIN_M: 0.55, ALEVIN_F: 0.6, INFANTIL_M: 0.5, INFANTIL_F: 0.65, CADETE_M: 0.6,
  CADETE_F: 0.7, JUVENIL_M: 0.55, JUVENIL_F: 0.6, SENIOR_M: 0.6, SENIOR_F: 0.75,
};
for (const cat of categories) {
  const compCode = `LIGA_${cat.code}`;
  competitions.push({
    code: compCode,
    season: SEASON,
    category: cat.code,
    name: `Liga Insular ${cat.name} ${cat.g.label}`,
    federation: 'Federación Isleña de Voleibol (ficticia)',
    forfeit_penalty_points: 1,
    cat,
  });
  const clubs = [{ sigla: OWN, name: 'Los Chicharros V.C.' }, ...RIVALS.slice(0, cat.def.teams - 1)];
  for (const club of clubs) {
    const code = `${cat.code}-${club.sigla}`;
    const own = club.sigla === OWN;
    teams.push({
      code,
      season: SEASON,
      category: cat.code,
      name: club.name,
      own_club: own ? 'true' : 'false',
      crest_url: own ? '/assets/imagenes/equipo.svg' : '',
      competition: compCode,
      sigla: club.sigla,
      cat,
    });
    strength.set(code, own ? OWN_STRENGTH[cat.code] : 0.3 + random() * 0.5);
  }
}

// ---------- Jornadas: cada dos semanas desde el 3 de octubre, sin el 26 de diciembre ----------
const MATCHDAY_DATES = [];
for (let d = '2026-10-03'; MATCHDAY_DATES.length < 14; d = addDays(d, 14)) {
  if (d === '2026-12-26') d = addDays(d, 14);
  MATCHDAY_DATES.push(d);
}

// Liga a doble vuelta por el método del círculo; la vuelta invierte local y visitante
function roundRobin(codes) {
  const list = [...codes];
  const n = list.length;
  const rounds = [];
  for (let r = 0; r < n - 1; r++) {
    const pairs = [];
    for (let i = 0; i < n / 2; i++) {
      const a = list[i];
      const b = list[n - 1 - i];
      pairs.push(r % 2 === 0 ? [a, b] : [b, a]);
    }
    rounds.push(pairs);
    list.splice(1, 0, list.pop());
  }
  return [...rounds, ...rounds.map((pairs) => pairs.map(([a, b]) => [b, a]))];
}

const matchdays = [];
const matches = [];
const VENUE_SLOTS = {
  MARINA: [['sat', '09:30'], ['sat', '11:00'], ['sat', '12:30'], ['sun', '10:00'], ['sun', '11:30'], ['sun', '13:00']],
  PUERTO: [['sat', '17:00'], ['sat', '19:00'], ['sun', '17:00'], ['sun', '19:00']],
};
const OTHER_SLOTS = [['sat', '10:00'], ['sat', '12:00'], ['sat', '16:30'], ['sat', '18:30'], ['sun', '11:00'], ['sun', '12:30']];
const usedSlots = new Map();

function playSets(home, away) {
  // Probabilidad de que el local gane un set: nivel de cada equipo y algo de ventaja de jugar en casa
  const p = Math.min(0.9, Math.max(0.1, 0.5 + (strength.get(home) - strength.get(away)) * 0.9 + 0.04));
  let h = 0;
  let a = 0;
  while (h < 3 && a < 3) {
    if (random() < p) h++;
    else a++;
  }
  return [h, a];
}

for (const comp of competitions) {
  const codes = teams.filter((t) => t.competition === comp.code).map((t) => t.code);
  const rounds = roundRobin(codes);
  rounds.forEach((pairs, idx) => {
    const number = idx + 1;
    const date = MATCHDAY_DATES[idx];
    matchdays.push({ competition: comp.code, number, scheduled_date: date });
    pairs.forEach(([home, away], k) => {
      const homeOwn = home.endsWith(`-${OWN}`);
      let venue = '';
      let slot;
      if (homeOwn) {
        venue = isCantera(comp.cat) ? 'MARINA' : 'PUERTO';
        const key = `${venue}-${date}`;
        const used = usedSlots.get(key) ?? 0;
        slot = VENUE_SLOTS[venue][used];
        usedSlots.set(key, used + 1);
      } else {
        slot = pick(OTHER_SLOTS);
      }
      const day = slot[0] === 'sat' ? date : addDays(date, 1);
      const startsAt = localToUtc(day, slot[1]);
      let status = day < CUT_DATE ? 'FINISHED' : 'SCHEDULED';
      let homeSets = '';
      let awaySets = '';
      // Un aplazamiento para que la demo tenga ese estado: el primer partido entre rivales de la
      // jornada 2 de la liga cadete masculina
      if (comp.code === 'LIGA_CADETE_M' && number === 2 && !homeOwn && !away.endsWith(`-${OWN}`)
        && !matches.some((m) => m.status === 'POSTPONED')) {
        status = 'POSTPONED';
      }
      if (status === 'FINISHED') [homeSets, awaySets] = playSets(home, away);
      matches.push({
        code: `${comp.code}-J${String(number).padStart(2, '0')}-${k + 1}`,
        competition: comp.code,
        matchday: number,
        home_team: home,
        away_team: away,
        venue,
        starts_at: startsAt,
        status,
        home_sets: homeSets,
        away_sets: awaySets,
        day,
      });
    });
  });
}

// ---------- Clasificación (3-0 y 3-1: 3 y 0 puntos; 3-2: 2 y 1) ----------
const standings = [];
for (const comp of competitions) {
  const rows = new Map(
    teams.filter((t) => t.competition === comp.code).map((t) => [
      t.code,
      { competition: comp.code, team: t.code, points: 0, played: 0, won: 0, lost: 0, sets_won: 0, sets_lost: 0, penalty_points: 0 },
    ]),
  );
  for (const m of matches.filter((x) => x.competition === comp.code && x.status === 'FINISHED')) {
    const h = rows.get(m.home_team);
    const a = rows.get(m.away_team);
    const [hs, as] = [m.home_sets, m.away_sets];
    h.played++; a.played++;
    h.sets_won += hs; h.sets_lost += as; a.sets_won += as; a.sets_lost += hs;
    const [win, lose] = hs > as ? [h, a] : [a, h];
    win.won++; lose.lost++;
    const tight = Math.min(hs, as) === 2;
    win.points += tight ? 2 : 3;
    lose.points += tight ? 1 : 0;
  }
  standings.push(
    ...[...rows.values()].sort(
      (x, y) => y.points - x.points || (y.sets_won - y.sets_lost) - (x.sets_won - x.sets_lost),
    ),
  );
}

// ---------- Personas y plantillas (solo equipos del club) ----------
const FIRST_NAMES = {
  M: ['Acaymo', 'Adrián', 'Airam', 'Alejandro', 'Álvaro', 'Ayoze', 'Bentor', 'Carlos', 'Dácil', 'Daniel',
    'David', 'Echedey', 'Eduardo', 'Gara', 'Guillermo', 'Héctor', 'Hugo', 'Iván', 'Jonay', 'Jorge',
    'José', 'Juan', 'Kevin', 'Leo', 'Lucas', 'Manuel', 'Mario', 'Nauzet', 'Néstor', 'Óscar', 'Pablo',
    'Raúl', 'Rubén', 'Samuel', 'Tanausú', 'Tinerfe', 'Yeray', 'Yoel'],
  F: ['Aday', 'Ainhoa', 'Alba', 'Andrea', 'Atteneri', 'Carla', 'Cathaysa', 'Chaxiraxi', 'Claudia',
    'Elena', 'Gara', 'Guacimara', 'Idaira', 'Irene', 'Jennifer', 'Laura', 'Lucía', 'María', 'Marta',
    'Nayra', 'Noelia', 'Nerea', 'Paula', 'Raquel', 'Sara', 'Tamara', 'Tania', 'Xerach', 'Yaiza',
    'Yanira', 'Yurena', 'Zaida'],
};
// Nombres que en la lista anterior son de un solo sexo
FIRST_NAMES.M = FIRST_NAMES.M.filter((n) => !['Dácil', 'Gara'].includes(n));
FIRST_NAMES.F = FIRST_NAMES.F.filter((n) => !['Aday', 'Xerach'].includes(n));
const SURNAMES = ['Afonso', 'Alonso', 'Barrios', 'Bethencourt', 'Cabrera', 'Castro', 'Delgado', 'Díaz',
  'Expósito', 'Febles', 'García', 'González', 'Hernández', 'Herrera', 'Linares', 'Luis', 'Marrero',
  'Martín', 'Medina', 'Mesa', 'Morales', 'Padilla', 'Perera', 'Pérez', 'Ramos', 'Reyes', 'Rodríguez',
  'Rosales', 'Santana', 'Siverio', 'Suárez', 'Toledo', 'Trujillo', 'Vera'];
const POSITIONS = ['colocador', 'colocador', 'opuesto', 'opuesto', 'central', 'central', 'central',
  'receptor', 'receptor', 'receptor', 'líbero', 'líbero'];

const people = [];
const memberships = [];
const usedNames = new Set();
function newPerson(gender, birthYear, adult) {
  let first;
  let last;
  do {
    first = pick(FIRST_NAMES[gender]);
    last = `${pick(SURNAMES)} ${pick(SURNAMES)}`;
  } while (usedNames.has(`${first} ${last}`) || last.split(' ')[0] === last.split(' ')[1]);
  usedNames.add(`${first} ${last}`);
  const month = String(between(1, 12)).padStart(2, '0');
  const day = String(between(1, 28)).padStart(2, '0');
  const person = {
    code: `P${String(people.length + 1).padStart(3, '0')}`,
    first_name: first,
    last_name: last,
    birth_date: birthYear ? `${birthYear}-${month}-${day}` : '',
    photo_url: '',
    image_consent: random() < (adult ? 0.85 : 0.7) ? 'true' : 'false',
  };
  people.push(person);
  return person;
}

const ownTeams = teams.filter((t) => t.own_club === 'true');
for (const team of ownTeams) {
  const { def, g } = team.cat;
  const shirts = [];
  while (shirts.length < def.players) {
    const n = between(1, 20);
    if (!shirts.includes(n)) shirts.push(n);
  }
  for (let i = 0; i < def.players; i++) {
    const year = between(def.years[0], def.years[1]);
    const p = newPerson(g.key, year, def.key === 'SENIOR');
    memberships.push({
      team: team.code,
      person: p.code,
      type: 'PLAYER',
      shirt_number: shirts[i],
      // En alevín (minivoley) todavía no hay puestos fijos
      position: def.key === 'ALEVIN' ? '' : POSITIONS[i],
    });
  }
}
// Dos juveniles que también juegan en el sénior de su sexo (doble ficha)
for (const g of ['M', 'F']) {
  const juv = memberships.find((m) => m.team === `JUVENIL_${g}-${OWN}` && m.position === 'receptor');
  const seniorShirts = memberships.filter((m) => m.team === `SENIOR_${g}-${OWN}`).map((m) => m.shirt_number);
  let shirt = 21;
  while (seniorShirts.includes(shirt)) shirt++;
  memberships.push({ team: `SENIOR_${g}-${OWN}`, person: juv.person, type: 'PLAYER', shirt_number: shirt, position: 'receptor' });
}
// Entrenadores adultos: uno por equipo; en alevín e infantil el mismo lleva el masculino y el femenino
const coachPlan = [
  ['ALEVIN_M', 'ALEVIN_F'], ['INFANTIL_M', 'INFANTIL_F'], ['CADETE_M'], ['CADETE_F'],
  ['JUVENIL_M'], ['JUVENIL_F'], ['SENIOR_M'], ['SENIOR_F'],
];
for (const group of coachPlan) {
  const coach = newPerson(random() < 0.5 ? 'M' : 'F', null, true);
  for (const cat of group) {
    memberships.push({ team: `${cat}-${OWN}`, person: coach.code, type: 'COACH', shirt_number: '', position: '' });
  }
}

// ---------- Noticias ----------
const teamName = (code) => teams.find((t) => t.code === code).name;
const teamLabel = (code) => {
  const t = teams.find((x) => x.code === code);
  return `${t.cat.name.toLowerCase()} ${t.cat.g.team}`;
};
function slugify(text) {
  return text
    .normalize('NFD')
    .replace(/[̀-ͯ]/g, '')
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-|-$/g, '');
}
const news = [];
function addNews(n) {
  news.push({ slug: slugify(n.title), featured: 'false', image_url: '', ...n });
}

addNews({
  title: 'Horarios de entrenamiento de la temporada 2026-27',
  summary: 'Días, horas y pabellón de entrenamiento de los diez equipos del club.',
  content: [
    'Ya están los horarios de entrenamiento de la temporada 2026-27. Empiezan el lunes 14 de septiembre.',
    '',
    '| Equipos | Días | Hora | Pabellón |',
    '| --- | --- | --- | --- |',
    '| Alevín e infantil | Lunes y miércoles | 17:00 a 18:30 | Polideportivo La Marina |',
    '| Cadete | Martes y jueves | 17:30 a 19:00 | Polideportivo La Marina |',
    '| Juvenil | Lunes, miércoles y viernes | 19:00 a 20:30 | Pabellón del Puerto |',
    '| Sénior | Martes, jueves y viernes | 20:30 a 22:00 | Pabellón del Puerto |',
    '',
    'Cualquier cambio se publicará en esta web.',
  ].join('\n'),
  image_url: '/assets/imagenes/noticia-comunicado.svg',
  type: 'STATEMENT',
  published_at: localToUtc('2026-09-07', '10:00'),
});
addNews({
  title: 'Presentación de los equipos de la temporada 2026-27',
  summary: 'El club presenta a sus diez equipos, de alevín a sénior, en el Pabellón del Puerto.',
  content: [
    'El sábado 26 de septiembre presentamos a los diez equipos de Los Chicharros V.C. en el Pabellón del Puerto.',
    '',
    'Esta temporada el club tiene equipo masculino y femenino en todas las categorías: alevín, infantil, cadete, juvenil y sénior.',
    'Las ligas empiezan el fin de semana del 3 de octubre.',
    '',
    '¡Os esperamos en la grada!',
  ].join('\n'),
  image_url: '/assets/imagenes/portada.svg',
  type: 'EVENT',
  featured: 'true',
  published_at: localToUtc('2026-09-21', '18:00'),
});
// Crónicas de cuatro equipos del club: el último partido jugado del sénior femenino, el penúltimo
// del sénior masculino, y así, para que haya noticias de fechas y rivales distintos
['SENIOR_F', 'SENIOR_M', 'JUVENIL_F', 'CADETE_M'].forEach((cat, back) => {
  const code = `${cat}-${OWN}`;
  const played = matches.filter(
    (m) => m.status === 'FINISHED' && (m.home_team === code || m.away_team === code),
  );
  const m = played[played.length - 1 - back];
  if (!m) return;
  const home = m.home_team === code;
  const ours = home ? m.home_sets : m.away_sets;
  const theirs = home ? m.away_sets : m.home_sets;
  const rival = teamName(home ? m.away_team : m.home_team);
  const won = ours > theirs;
  const label = teamLabel(code);
  const where = home ? 'en casa' : 'a domicilio';
  const title = won
    ? `Victoria del ${label} ante ${rival} (${ours}-${theirs})`
    : `Derrota del ${label} ante ${rival} (${ours}-${theirs})`;
  const next = matches.find(
    (x) => x.matchday > m.matchday && x.competition === m.competition
      && (x.home_team === code || x.away_team === code),
  );
  const nextText = next
    ? `El próximo partido es en la jornada ${next.matchday}, contra ${teamName(next.home_team === code ? next.away_team : next.home_team)}.`
    : 'Era el último partido de la liga regular.';
  addNews({
    title,
    summary: `Crónica de la jornada ${m.matchday} de la ${competitions.find((c) => c.code === m.competition).name}, jugada ${where}.`,
    content: [
      `El ${label} ${won ? 'ganó' : 'perdió'} ${where} por ${ours}-${theirs} en la jornada ${m.matchday}.`,
      '',
      won
        ? (Math.min(ours, theirs) === 2
          ? 'Fue un partido muy igualado que se decidió en el quinto set; dos puntos que valen mucho.'
          : 'El equipo controló el partido desde el saque y suma los tres puntos.')
        : (Math.min(ours, theirs) === 2
          ? 'Se llegó al quinto set y el punto conseguido mantiene al equipo en la pelea.'
          : 'El rival fue superior esta vez; toca seguir trabajando en los entrenamientos.'),
      '',
      nextText,
    ].join('\n'),
    image_url: '/assets/imagenes/noticia-cronica.svg',
    type: 'MATCH_REPORT',
    published_at: localToUtc(addDays(m.day, 1), '12:00'),
  });
});
addNews({
  title: 'Campus de Navidad de voleibol',
  summary: 'Del 28 al 30 de diciembre, campus para nacidos entre 2011 y 2018, socios o no del club.',
  content: [
    'Del 28 al 30 de diciembre organizamos el Campus de Navidad en el Polideportivo La Marina, de 9:30 a 13:30.',
    '',
    '- Para nacidos entre 2011 y 2018, sean o no del club.',
    '- Plazas limitadas: 40.',
    '- Inscripción en la secretaría del club hasta el 18 de diciembre.',
  ].join('\n'),
  image_url: '/assets/imagenes/noticia-evento.svg',
  type: 'EVENT',
  featured: 'true',
  published_at: localToUtc('2026-12-01', '09:00'),
});
addNews({
  title: 'Asamblea de socios de enero',
  summary: 'Borrador: convocatoria de la asamblea ordinaria de socios.',
  content: 'Texto pendiente de aprobar por la junta directiva.',
  image_url: '',
  type: 'STATEMENT',
  published_at: '',
});
news.sort((a, b) => (a.published_at || '9999').localeCompare(b.published_at || '9999'));

// ---------- Escritura de los CSV ----------
function csvValue(v) {
  const s = v === null || v === undefined ? '' : String(v);
  return /[",\n]/.test(s) ? `"${s.replace(/"/g, '""')}"` : s;
}
function writeCsv(name, columns, rows) {
  const lines = [columns.join(','), ...rows.map((r) => columns.map((c) => csvValue(r[c])).join(','))];
  writeFileSync(path.join(outDir, `${name}.csv`), `${lines.join('\n')}\n`, 'utf8');
  return `${name}.csv: ${rows.length} filas`;
}
mkdirSync(outDir, { recursive: true });
const report = [
  writeCsv('seasons', ['name', 'start_date', 'end_date', 'active'], seasons),
  writeCsv('categories', ['code', 'name', 'gender', 'sort_order'], categories),
  writeCsv('venues', ['code', 'name', 'address', 'latitude', 'longitude'], venues),
  writeCsv('competitions', ['code', 'season', 'category', 'name', 'federation', 'forfeit_penalty_points'], competitions),
  writeCsv('teams', ['code', 'season', 'category', 'name', 'own_club', 'crest_url'], teams),
  writeCsv('people', ['code', 'first_name', 'last_name', 'birth_date', 'photo_url', 'image_consent'], people),
  writeCsv('team_memberships', ['team', 'person', 'type', 'shirt_number', 'position'], memberships),
  writeCsv('matchdays', ['competition', 'number', 'scheduled_date'], matchdays),
  writeCsv('matches', ['code', 'competition', 'matchday', 'home_team', 'away_team', 'venue', 'starts_at', 'status', 'home_sets', 'away_sets'], matches),
  writeCsv('standings', ['competition', 'team', 'points', 'played', 'won', 'lost', 'sets_won', 'sets_lost', 'penalty_points'], standings),
  writeCsv('news_articles', ['slug', 'title', 'summary', 'content', 'image_url', 'type', 'featured', 'published_at'], news),
];
console.log(`Datos de ejemplo de la temporada ${SEASON} (corte: ${CUT_DATE}) en ${path.relative(root, outDir)}`);
for (const line of report) console.log(`  ${line}`);
