import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Meta } from '@angular/platform-browser';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

const GENERIC_ERROR = 'Email o contraseña incorrectos.';

/**
 * Login del panel (H09, wireframe 07). Un solo mensaje para cualquier fallo de credenciales: la
 * API no dice si el email existe y la pantalla tampoco.
 */
@Component({
  selector: 'cv-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly submitting = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly form = inject(NonNullableFormBuilder).group({
    email: ['', [Validators.required, Validators.email, Validators.maxLength(254)]],
    password: ['', [Validators.required, Validators.maxLength(128)]],
  });

  constructor() {
    // El login nunca debe salir en los buscadores, tampoco en producción
    inject(Meta).updateTag({ name: 'robots', content: 'noindex, nofollow' });
    if (this.auth.isAuthenticated()) {
      void this.router.navigateByUrl(this.redirectUrl());
    }
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { email, password } = this.form.getRawValue();
    this.submitting.set(true);
    this.error.set(null);
    this.auth.login(email.trim(), password).subscribe({
      next: () => void this.router.navigateByUrl(this.redirectUrl()),
      error: (error: unknown) => {
        this.submitting.set(false);
        this.form.controls.password.reset();
        this.error.set(this.messageFor(error));
      },
    });
  }

  protected fieldError(name: 'email' | 'password'): string | null {
    const control = this.form.controls[name];
    if (!control.touched || !control.errors) {
      return null;
    }
    if (control.errors['required']) {
      return name === 'email' ? 'Escribe tu email' : 'Escribe tu contraseña';
    }
    return name === 'email' ? 'Escribe un email válido' : 'La contraseña es demasiado larga';
  }

  private messageFor(error: unknown): string {
    if (error instanceof HttpErrorResponse && error.status === 429) {
      return 'Demasiados intentos. Espera unos minutos y vuelve a probar.';
    }
    if (error instanceof HttpErrorResponse && (error.status === 401 || error.status === 400)) {
      return GENERIC_ERROR;
    }
    return 'No se ha podido conectar con el servidor. Inténtalo de nuevo.';
  }

  /** Solo rutas del panel: un enlace de login no puede mandar a otra web tras entrar. */
  private redirectUrl(): string {
    const redirect = this.route.snapshot.queryParamMap.get('redirect');
    return redirect?.startsWith('/gestion') ? redirect : '/gestion';
  }
}
