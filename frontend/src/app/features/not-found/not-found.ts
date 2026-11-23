import { Component, RESPONSE_INIT, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

/** Página para las rutas que no existen. En el servidor responde con 404, no con 200. */
@Component({
  selector: 'cv-not-found',
  imports: [RouterLink],
  templateUrl: './not-found.html',
})
export class NotFound {
  constructor() {
    const response = inject(RESPONSE_INIT, { optional: true });
    if (response) {
      response.status = 404;
    }
  }
}
