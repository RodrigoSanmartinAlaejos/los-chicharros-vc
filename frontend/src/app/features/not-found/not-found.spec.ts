import { RESPONSE_INIT } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { NotFound } from './not-found';

describe('NotFound', () => {
  it('responde con 404 cuando se renderiza en el servidor', async () => {
    const response: ResponseInit = { status: 200 };
    await TestBed.configureTestingModule({
      imports: [NotFound],
      providers: [provideRouter([]), { provide: RESPONSE_INIT, useValue: response }],
    }).compileComponents();

    TestBed.createComponent(NotFound);

    expect(response.status).toBe(404);
  });
});
