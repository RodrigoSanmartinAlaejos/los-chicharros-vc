package com.loschicharros.api.support.probe;

import com.loschicharros.api.common.error.ConflictException;
import com.loschicharros.api.common.error.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints que solo existen en los tests, para probar la base del backend (errores y auditoría)
 * antes de que haya endpoints reales. {@code @TestComponent} lo saca del escaneo normal: solo lo
 * registra {@code AbstractIntegrationTest}.
 */
@TestComponent
@RestController
@RequestMapping("/test/probes")
public class ProbeController {

  private final AuditProbeRepository repository;

  public ProbeController(AuditProbeRepository repository) {
    this.repository = repository;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProbeResponse create(@Valid @RequestBody ProbeRequest request) {
    return ProbeResponse.of(
        repository.saveAndFlush(new AuditProbe(request.name(), request.code())));
  }

  @PutMapping("/{id}")
  public ProbeResponse rename(@PathVariable long id, @Valid @RequestBody ProbeRequest request) {
    var probe =
        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("sonda", id));
    probe.rename(request.name());
    return ProbeResponse.of(repository.saveAndFlush(probe));
  }

  @GetMapping("/{id}")
  public ProbeResponse get(@PathVariable long id) {
    return repository
        .findById(id)
        .map(ProbeResponse::of)
        .orElseThrow(() -> new ResourceNotFoundException("sonda", id));
  }

  @GetMapping("/page")
  public String page(@RequestParam @Max(100) int size) {
    return "ok";
  }

  @GetMapping("/client-ip")
  public String clientIp(HttpServletRequest request) {
    return request.getRemoteAddr();
  }

  @GetMapping("/conflict")
  public void conflict() {
    throw new ConflictException("No se puede borrar: tiene equipos");
  }

  @GetMapping("/failure")
  public void failure() {
    throw new IllegalStateException("detalle interno que no debe salir");
  }
}
