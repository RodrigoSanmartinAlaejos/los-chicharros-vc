import { HttpErrorResponse } from '@angular/common/http';
import { FormControl, FormGroup } from '@angular/forms';
import { applyProblem } from './problem';

describe('applyProblem', () => {
  const form = () => new FormGroup({ name: new FormControl('') });

  it('avisa de que no hay conexión cuando el servidor no responde', () => {
    const error = new HttpErrorResponse({ status: 0 });

    expect(applyProblem(form(), error)).toContain('No se ha podido conectar');
  });

  it('devuelve el detalle si algún error no corresponde a un campo del formulario', () => {
    const group = form();
    const error = new HttpErrorResponse({
      status: 400,
      error: {
        detail: 'La petición tiene campos no válidos',
        errors: [
          { field: 'name', message: 'no debe estar vacío' },
          { field: 'otro', message: 'no vale' },
        ],
      },
    });

    expect(applyProblem(group, error)).toBe('La petición tiene campos no válidos');
    expect(group.controls.name.errors).toEqual({ server: 'no debe estar vacío' });
  });
});
