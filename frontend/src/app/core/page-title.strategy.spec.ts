import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Title } from '@angular/platform-browser';
import { Router, TitleStrategy, provideRouter } from '@angular/router';
import { PageTitleStrategy } from './page-title.strategy';

@Component({ template: '' })
class Empty {}

describe('PageTitleStrategy', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([
          { path: '', component: Empty },
          { path: 'noticias', title: 'Noticias', component: Empty },
        ]),
        { provide: TitleStrategy, useClass: PageTitleStrategy },
      ],
    });
  });

  it('añade el nombre del club al título de la página', async () => {
    await TestBed.inject(Router).navigateByUrl('/noticias');

    expect(TestBed.inject(Title).getTitle()).toBe('Noticias · Los Chicharros V.C.');
  });

  it('usa solo el nombre del club en la portada', async () => {
    await TestBed.inject(Router).navigateByUrl('/');

    expect(TestBed.inject(Title).getTitle()).toBe('Los Chicharros V.C.');
  });
});
