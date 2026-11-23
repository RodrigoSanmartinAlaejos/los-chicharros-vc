package com.loschicharros.api.venues;

import com.loschicharros.api.common.error.ConflictException;
import com.loschicharros.api.common.error.ResourceNotFoundException;
import com.loschicharros.api.venues.dto.VenueRequest;
import com.loschicharros.api.venues.dto.VenueResponse;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Alta, edición y borrado de instalaciones. */
@Service
@Transactional(readOnly = true)
public class VenueService {

  private static final String RESOURCE = "instalación";

  private final VenueRepository venues;

  public VenueService(VenueRepository venues) {
    this.venues = venues;
  }

  /** Todas las instalaciones, por orden alfabético. */
  public List<VenueResponse> findAll() {
    return venues.findAllByOrderByNameAsc().stream().map(VenueResponse::from).toList();
  }

  /**
   * Crea una instalación.
   *
   * @throws ConflictException si ya hay una con ese nombre
   */
  @Transactional
  public VenueResponse create(VenueRequest request) {
    if (venues.existsByName(request.name())) {
      throw duplicated(request.name());
    }
    var venue =
        new Venue(request.name(), request.address(), request.latitude(), request.longitude());
    return VenueResponse.from(venues.save(venue));
  }

  /**
   * Edita una instalación.
   *
   * @throws ResourceNotFoundException si no existe
   * @throws ConflictException si otra instalación ya tiene ese nombre
   */
  @Transactional
  public VenueResponse update(Long id, VenueRequest request) {
    var venue = get(id);
    if (venues.existsByNameAndIdNot(request.name(), id)) {
      throw duplicated(request.name());
    }
    venue.update(request.name(), request.address(), request.latitude(), request.longitude());
    return VenueResponse.from(venue);
  }

  /**
   * Borra una instalación.
   *
   * @throws ResourceNotFoundException si no existe
   * @throws ConflictException si tiene partidos o entrenamientos
   */
  @Transactional
  public void delete(Long id) {
    var venue = get(id);
    try {
      venues.delete(venue);
      venues.flush();
    } catch (DataIntegrityViolationException e) {
      throw new ConflictException(
          "No se puede borrar la instalación "
              + venue.getName()
              + ": tiene partidos o entrenamientos");
    }
  }

  private Venue get(Long id) {
    return venues.findById(id).orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
  }

  private static ConflictException duplicated(String name) {
    return new ConflictException("Ya existe la instalación " + name);
  }
}
