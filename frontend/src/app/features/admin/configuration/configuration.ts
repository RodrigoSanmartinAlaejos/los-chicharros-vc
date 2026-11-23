import { Component } from '@angular/core';
import { CategoriesPanel } from './categories-panel/categories-panel';
import { SeasonsPanel } from './seasons-panel/seasons-panel';
import { VenuesPanel } from './venues-panel/venues-panel';

/**
 * Configuración del club (H15): temporadas, categorías e instalaciones, la estructura que hace
 * falta antes de crear equipos y partidos.
 */
@Component({
  selector: 'cv-configuration',
  imports: [SeasonsPanel, CategoriesPanel, VenuesPanel],
  template: `
    <h1>Configuración</h1>
    <p class="intro">
      Prepara las temporadas, las categorías y las instalaciones antes de crear equipos y partidos.
    </p>
    <cv-seasons-panel />
    <cv-categories-panel />
    <cv-venues-panel />
  `,
  styles: `
    .intro {
      margin-bottom: var(--space-6);
      color: var(--color-text-secondary);
    }
  `,
})
export class Configuration {}
