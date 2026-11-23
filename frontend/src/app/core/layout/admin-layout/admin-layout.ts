import { Component, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ADMIN_NAV } from '../navigation';

/**
 * Estructura del panel de gestión (wireframes 08 y 10): menú lateral fijo en escritorio y barra con
 * menú desplegable en móvil. El bloque de sesión llega con el login (sprint 2).
 */
@Component({
  selector: 'cv-admin-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './admin-layout.html',
  styleUrl: './admin-layout.scss',
  host: { '(document:keydown.escape)': 'closeMenu()' },
})
export class AdminLayout {
  protected readonly navItems = ADMIN_NAV;
  protected readonly menuOpen = signal(false);

  protected toggleMenu(): void {
    this.menuOpen.update((open) => !open);
  }

  protected closeMenu(): void {
    this.menuOpen.set(false);
  }
}
