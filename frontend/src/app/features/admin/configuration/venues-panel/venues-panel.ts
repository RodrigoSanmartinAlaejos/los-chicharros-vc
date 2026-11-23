import { Component, inject } from '@angular/core';
import {
  FormControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ConfigurationApi } from '../configuration.api';
import { Venue, VenueRequest } from '../configuration.models';
import { CrudPanel } from '../crud-panel';

/** Instalaciones: nombre, dirección y, si se conocen, coordenadas para el mapa. */
@Component({
  selector: 'cv-venues-panel',
  imports: [ReactiveFormsModule],
  templateUrl: './venues-panel.html',
  styleUrl: '../crud-panel.scss',
})
export class VenuesPanel extends CrudPanel<Venue, VenueRequest> {
  protected readonly resource = inject(ConfigurationApi).venues;
  protected readonly noun = 'la instalación';
  protected readonly form = inject(NonNullableFormBuilder).group({
    name: ['', [Validators.required, Validators.maxLength(100)]],
    address: ['', [Validators.required, Validators.maxLength(200)]],
    latitude: new FormControl<number | null>(null, [Validators.min(-90), Validators.max(90)]),
    longitude: new FormControl<number | null>(null, [Validators.min(-180), Validators.max(180)]),
  });

  protected override emptyValue() {
    return { name: '', address: '', latitude: null, longitude: null };
  }

  protected override toFormValue(venue: Venue) {
    const { name, address, latitude, longitude } = venue;
    return { name, address, latitude, longitude };
  }

  protected override toRequest(): VenueRequest {
    const { name, address, latitude, longitude } = this.form.getRawValue();
    return { name, address, latitude: latitude ?? null, longitude: longitude ?? null };
  }

  protected override label(venue: Venue): string {
    return venue.name;
  }
}
