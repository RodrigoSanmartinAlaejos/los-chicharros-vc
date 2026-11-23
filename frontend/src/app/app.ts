import { Component, inject } from '@angular/core';
import { Meta } from '@angular/platform-browser';
import { RouterOutlet } from '@angular/router';
import { APP_ENV } from './core/config/app-env';
import { SiteFooter } from './core/layout/site-footer/site-footer';
import { SiteHeader } from './core/layout/site-header/site-header';

/** Estructura común de la web pública: cabecera, contenido y pie. */
@Component({
  selector: 'cv-root',
  imports: [RouterOutlet, SiteHeader, SiteFooter],
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
