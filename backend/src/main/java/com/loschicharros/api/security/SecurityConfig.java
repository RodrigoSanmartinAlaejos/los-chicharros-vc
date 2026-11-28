package com.loschicharros.api.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Seguridad de la API: sin sesión de servidor, access token JWT en la cabecera {@code
 * Authorization} y autorización por rol (matriz de permisos).
 *
 * <p>Bajo {@code /api/v1} todo exige sesión salvo las rutas de autenticación (que usan la cookie de
 * refresco, no el access token) y las lecturas públicas; cada acción se vuelve a comprobar con
 * {@code @PreAuthorize}. Fuera de {@code /api/v1} solo queda Actuator, que ya limita lo que expone.
 * Los errores 401 y 403 los escribe {@code ApiExceptionHandler}, con el mismo formato RFC 7807 que
 * el resto.
 */
@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
class SecurityConfig {

  static final String ROLES_CLAIM = "roles";
  private static final String AUTH_PATH = "/api/v1/auth/";

  /** Lecturas que cualquiera puede hacer sin sesión. */
  private static final String[] PUBLIC_GETS = {
    "/api/v1/seasons",
    "/api/v1/categories",
    "/api/v1/venues",
    "/api/v1/teams",
    "/api/v1/teams/*",
    "/api/v1/competitions",
    "/api/v1/competitions/*",
    "/api/v1/competitions/*/matchdays",
    "/api/v1/competitions/*/standings",
    "/api/v1/matches",
    "/api/v1/matches/*"
  };

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver)
      throws Exception {
    return http
        // Sin CSRF: el access token viaja en una cabecera que el navegador no envía solo, y la
        // cookie
        // de refresco es SameSite=Strict y solo va a /api/v1/auth, así que otra web no puede usarla
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.disable())
        .httpBasic(basic -> basic.disable())
        .formLogin(form -> form.disable())
        .logout(logout -> logout.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth ->
            auth.requestMatchers(
                    HttpMethod.POST,
                    "/api/v1/auth/login",
                    "/api/v1/auth/refresh",
                    "/api/v1/auth/logout")
                .permitAll()
                .requestMatchers(HttpMethod.GET, PUBLIC_GETS)
                .permitAll()
                .requestMatchers("/api/**")
                .authenticated()
                .anyRequest()
                .permitAll())
        .oauth2ResourceServer(oauth ->
            oauth
                .bearerTokenResolver(bearerTokenResolver())
                .jwt(jwt -> jwt.jwtAuthenticationConverter(authenticationConverter()))
                .authenticationEntryPoint((request, response, ex) ->
                    exceptionResolver.resolveException(request, response, null, ex))
                .accessDeniedHandler((request, response, ex) ->
                    exceptionResolver.resolveException(request, response, null, ex)))
        .exceptionHandling(handling ->
            handling
                .authenticationEntryPoint((request, response, ex) ->
                    exceptionResolver.resolveException(request, response, null, ex))
                .accessDeniedHandler((request, response, ex) ->
                    exceptionResolver.resolveException(request, response, null, ex)))
        .build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  JwtEncoder jwtEncoder(JwtProperties properties) {
    return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey(properties)));
  }

  @Bean
  JwtDecoder jwtDecoder(JwtProperties properties) {
    var decoder =
        NimbusJwtDecoder.withSecretKey(secretKey(properties))
            .macAlgorithm(MacAlgorithm.HS256)
            .build();
    decoder.setJwtValidator(
        new DelegatingOAuth2TokenValidator<>(
            JwtValidators.createDefault(), new JwtIssuerValidator(properties.issuer())));
    return decoder;
  }

  /**
   * Las rutas de autenticación no leen el access token: un token caducado que siga en memoria no
   * puede impedir volver a iniciar sesión.
   */
  private static BearerTokenResolver bearerTokenResolver() {
    var resolver = new DefaultBearerTokenResolver();
    return request ->
        request.getRequestURI().startsWith(AUTH_PATH) ? null : resolver.resolve(request);
  }

  /** Los roles del token ({@code roles: ["ADMIN"]}) pasan a autoridades {@code ROLE_ADMIN}. */
  private static JwtAuthenticationConverter authenticationConverter() {
    var authorities = new JwtGrantedAuthoritiesConverter();
    authorities.setAuthoritiesClaimName(ROLES_CLAIM);
    authorities.setAuthorityPrefix("ROLE_");
    var converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(authorities);
    return converter;
  }

  private static SecretKey secretKey(JwtProperties properties) {
    return new SecretKeySpec(properties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
  }
}
