import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SiteFooter } from '../site-footer/site-footer';
import { SiteHeader } from '../site-header/site-header';

/** Estructura común de la web pública: cabecera, contenido y pie. */
@Component({
  selector: 'cv-public-layout',
  imports: [RouterOutlet, SiteHeader, SiteFooter],
  templateUrl: './public-layout.html',
  styleUrl: './public-layout.scss',
})
export class PublicLayout {}
