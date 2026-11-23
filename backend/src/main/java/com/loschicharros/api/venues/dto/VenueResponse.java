package com.loschicharros.api.venues.dto;

import com.loschicharros.api.venues.Venue;
import java.math.BigDecimal;

/** Instalación tal como la devuelve la API (contrato OpenAPI, {@code VenueResponse}). */
public record VenueResponse(
    Long id, String name, String address, BigDecimal latitude, BigDecimal longitude) {

  public static VenueResponse from(Venue venue) {
    return new VenueResponse(
        venue.getId(),
        venue.getName(),
        venue.getAddress(),
        venue.getLatitude(),
        venue.getLongitude());
  }
}
