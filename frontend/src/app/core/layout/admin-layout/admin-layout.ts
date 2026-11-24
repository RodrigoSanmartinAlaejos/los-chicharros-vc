import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { ADMIN_NAV } from '../navigation';

/**
 * Estructura del panel de gestión (wireframes 08 y 10): menú lateral fijo en escritorio y barra con
 * menú desplegable en móvil. Abajo, quién tiene la sesión y el botón para cerrarla.
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
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  protected readonly menuOpen = signal(false);
  protected readonly email = this.auth.email;

  /** Cierra la sesión también en la API y vuelve a la portada (secuencia de sesión, H10). */
  protected logout(): void {
    this.auth.logout().subscribe(() => void this.router.navigateByUrl('/'));
  }

  protected toggleMenu(): void {
    this.menuOpen.update((open) => !open);
  }

  protected closeMenu(): void {
    this.menuOpen.set(false);
  }
}
