import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { LEGAL_NAV } from '../navigation';

/** Pie de la web pública con los enlaces legales (H08). */
@Component({
  selector: 'cv-site-footer',
  imports: [RouterLink],
  templateUrl: './site-footer.html',
  styleUrl: './site-footer.scss',
})
export class SiteFooter {
  protected readonly legalItems = LEGAL_NAV;
  protected readonly year = new Date().getFullYear();
}
