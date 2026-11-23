import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { map } from 'rxjs';

/**
 * Página provisional de las secciones que aún no existen, para que el menú ya funcione. Cada
 * historia sustituye su ruta por la página real.
 */
@Component({
  selector: 'cv-coming-soon',
  templateUrl: './coming-soon.html',
})
export class ComingSoon {
  protected readonly heading = toSignal(
    inject(ActivatedRoute).title.pipe(map((title) => title ?? '')),
    { initialValue: '' },
  );
}
