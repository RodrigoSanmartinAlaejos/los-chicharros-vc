package com.loschicharros.api.auth;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.loschicharros.api.common.error.TooManyRequestsException;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Límite de peticiones por IP en {@code /api/v1/auth/**} (Bucket4j, un cubo por IP en memoria): la
 * siguiente a la que pasa del límite recibe 429. Va antes que Spring Security, para que ni siquiera
 * se compruebe una contraseña.
 *
 * <p>La IP es la de {@code getRemoteAddr()}, que con {@code server.forward-headers-strategy:
 * native} ya es la del cliente cuando la petición llega por el proxy del SSR (ADR-004).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class AuthRateLimitFilter extends OncePerRequestFilter {

  private static final String AUTH_PATH = "/api/v1/auth/";
  private static final long NANOS_PER_SECOND = 1_000_000_000L;

  private final LoginProtectionProperties properties;
  private final HandlerExceptionResolver exceptionResolver;
  private final Cache<String, Bucket> buckets =
      Caffeine.newBuilder().expireAfterAccess(Duration.ofMinutes(5)).maximumSize(100_000).build();

  public AuthRateLimitFilter(
      LoginProtectionProperties properties,
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
    this.properties = properties;
    this.exceptionResolver = exceptionResolver;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !request.getRequestURI().startsWith(AUTH_PATH);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    var bucket = buckets.get(request.getRemoteAddr(), ip -> newBucket());
    var probe = bucket.tryConsumeAndReturnRemaining(1);
    if (probe.isConsumed()) {
      chain.doFilter(request, response);
      return;
    }
    // Segundos hasta que vuelva a haber sitio, redondeados hacia arriba
    var seconds =
        Math.max(1, (probe.getNanosToWaitForRefill() + NANOS_PER_SECOND - 1) / NANOS_PER_SECOND);
    exceptionResolver.resolveException(
        request, response, null, new TooManyRequestsException(seconds));
  }

  /** Olvida todos los cubos (lo usan los tests para empezar de cero). */
  public void clear() {
    buckets.invalidateAll();
  }

  private Bucket newBucket() {
    var perMinute = properties.requestsPerMinute();
    return Bucket.builder()
        .addLimit(limit -> limit.capacity(perMinute).refillGreedy(perMinute, Duration.ofMinutes(1)))
        .build();
  }
}
