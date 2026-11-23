package com.loschicharros.api.auth;

import com.loschicharros.api.auth.dto.AccessTokenResponse;
import com.loschicharros.api.auth.dto.LoginRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Autenticación (contrato OpenAPI, etiqueta «Autenticación»). El refresco y el cierre de sesión,
 * con la cookie del refresh token, llegan con H10 (#54).
 */
@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

  private final AuthService service;

  AuthController(AuthService service) {
    this.service = service;
  }

  @PostMapping("/login")
  AccessTokenResponse login(@Valid @RequestBody LoginRequest request) {
    return service.login(request);
  }
}
