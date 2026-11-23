import { Component, inject } from '@angular/core';
import { Meta } from '@angular/platform-browser';
import { RouterOutlet } from '@angular/router';
import { APP_ENV } from './core/config/app-env';

/**
 * Raíz de la aplicación: el enlace para saltar al contenido y el layout de cada zona (web pública
 * o panel de gestión). Cada layout pone su `main#contenido`.
 */
@Component({
  selector: 'cv-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  constructor() {
    // Solo producción aparece en los buscadores; pruebas y local nunca
    if (inject(APP_ENV) !== 'prod') {
      inject(Meta).updateTag({ name: 'robots', content: 'noindex, nofollow' });
    }
  }
}
