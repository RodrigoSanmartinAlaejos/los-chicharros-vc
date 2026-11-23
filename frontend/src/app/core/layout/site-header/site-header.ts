import { Component, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MAIN_NAV } from '../navigation';

/**
 * Cabecera de la web pública: escudo, nombre y menú. En móvil el menú se abre con un botón; en
 * escritorio se muestra en línea.
 */
@Component({
  selector: 'cv-site-header',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './site-header.html',
  styleUrl: './site-header.scss',
})
export class SiteHeader {
  protected readonly navItems = MAIN_NAV;
  protected readonly menuOpen = signal(false);

  protected toggleMenu(): void {
    this.menuOpen.update((open) => !open);
  }

  protected closeMenu(): void {
    this.menuOpen.set(false);
  }
}
