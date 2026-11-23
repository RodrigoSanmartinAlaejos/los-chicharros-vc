// Lanza la herramienta de formato o de lint en su carpeta sobre los ficheros que le pasa lint-staged.
// Uso: node scripts/pre-commit.mjs <eslint|prettier|spotless> <ficheros...>
import { spawnSync } from 'node:child_process';
import path from 'node:path';

const root = path.resolve(import.meta.dirname, '..');
const isWindows = process.platform === 'win32';
const [tool, ...files] = process.argv.slice(2);

// En Windows hace falta la shell para lanzar pnpm.cmd y mvnw.cmd; cada argumento va entre comillas.
function run(dir, command, args) {
  const cwd = path.join(root, dir);
  const result = isWindows
    ? spawnSync([command, ...args.map((a) => `"${a}"`)].join(' '), {
        cwd,
        stdio: 'inherit',
        shell: true,
      })
    : spawnSync(command, args, { cwd, stdio: 'inherit' });
  if (result.error) throw result.error;
  process.exit(result.status ?? 1);
}

// Spotless recibe expresiones regulares sobre la ruta absoluta; el separador puede ser \ o /.
function spotlessPattern(file) {
  const relative = path.relative(path.join(root, 'backend'), file);
  const parts = relative.split(/[\\/]/).map((p) => p.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'));
  return `.*[\\\\/]${parts.join('[\\\\/]')}`;
}

if (files.length === 0) process.exit(0);

switch (tool) {
  case 'eslint':
    run('frontend', 'pnpm', ['exec', 'eslint', '--fix', '--no-warn-ignored', ...files]);
    break;
  case 'prettier':
    run('frontend', 'pnpm', ['exec', 'prettier', '--write', '--ignore-unknown', ...files]);
    break;
  case 'spotless':
    run('backend', isWindows ? 'mvnw.cmd' : './mvnw', [
      '-q',
      '-B',
      'spotless:apply',
      `-DspotlessFiles=${files.map(spotlessPattern).join(',')}`,
    ]);
    break;
  default:
    console.error(`Herramienta desconocida: ${tool}`);
    process.exit(1);
}
