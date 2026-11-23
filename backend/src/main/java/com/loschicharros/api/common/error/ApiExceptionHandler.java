package com.loschicharros.api.common.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Único sitio donde los errores se convierten en respuestas RFC 7807 ({@code
 * application/problem+json}), como fija la API (docs/05-api/README.md, «Errores»).
 *
 * <p>Los errores de Spring MVC (JSON mal formado, ruta inexistente, método no permitido…) los
 * resuelve la clase padre; aquí se añaden los de validación con {@code errors} por campo y las
 * excepciones propias. Desde el sprint 2, los de autenticación y autorización los resuelve Spring
 * Security antes de llegar aquí.
 */
@RestControllerAdvice
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

  static final String VALIDATION_DETAIL = "La petición tiene campos no válidos";
  private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    var violations =
        ex.getBindingResult().getAllErrors().stream()
            .map(error ->
                new FieldViolation(
                    error instanceof org.springframework.validation.FieldError field
                        ? field.getField()
                        : error.getObjectName(),
                    error.getDefaultMessage()))
            .toList();
    return handleExceptionInternal(ex, validationProblem(violations), headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleHandlerMethodValidationException(
      HandlerMethodValidationException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    var violations =
        ex.getParameterValidationResults().stream()
            .flatMap(result ->
                result.getResolvableErrors().stream()
                    .map(error ->
                        new FieldViolation(
                            result.getMethodParameter().getParameterName(),
                            error.getDefaultMessage())))
            .toList();
    return handleExceptionInternal(
        ex, validationProblem(violations), headers, HttpStatus.BAD_REQUEST, request);
  }

  /** Validación de parámetros en los servicios ({@code @Validated}). */
  @ExceptionHandler(ConstraintViolationException.class)
  ProblemDetail handleConstraintViolation(
      ConstraintViolationException ex, HttpServletRequest request) {
    var violations =
        ex.getConstraintViolations().stream()
            .map(v -> new FieldViolation(lastNode(v.getPropertyPath().toString()), v.getMessage()))
            .toList();
    return withInstance(validationProblem(violations), request);
  }

  /** Reglas entre campos que comprueba el servicio. */
  @ExceptionHandler(InvalidFieldException.class)
  ProblemDetail handleInvalidField(InvalidFieldException ex, HttpServletRequest request) {
    return withInstance(
        validationProblem(List.of(new FieldViolation(ex.getField(), ex.getMessage()))), request);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  ProblemDetail handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
    return withInstance(
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage()), request);
  }

  @ExceptionHandler(ConflictException.class)
  ProblemDetail handleConflict(ConflictException ex, HttpServletRequest request) {
    return withInstance(
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage()), request);
  }

  /**
   * Una restricción de la base (único, clave foránea) que el servicio no comprobó antes. El mensaje
   * de MySQL no se devuelve: puede contener datos y nombres internos.
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  ProblemDetail handleDataIntegrity(
      DataIntegrityViolationException ex, HttpServletRequest request) {
    log.warn("Restricción de la base de datos incumplida en {}", request.getRequestURI(), ex);
    return withInstance(
        ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, "La operación choca con datos que ya existen"),
        request);
  }

  /** Cualquier otro error: 500 sin detalles internos; la traza solo va al log. */
  @ExceptionHandler(Exception.class)
  ProblemDetail handleUnexpected(Exception ex, HttpServletRequest request) {
    log.error("Error no controlado en {} {}", request.getMethod(), request.getRequestURI(), ex);
    return withInstance(
        ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error inesperado"),
        request);
  }

  /** Añade {@code instance} con la ruta pedida a los errores que resuelve la clase padre. */
  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    if (body instanceof ProblemDetail problem
        && problem.getInstance() == null
        && request instanceof ServletWebRequest servlet) {
      withInstance(problem, servlet.getRequest());
    }
    return super.handleExceptionInternal(ex, body, headers, status, request);
  }

  private static ProblemDetail validationProblem(List<FieldViolation> violations) {
    var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, VALIDATION_DETAIL);
    problem.setProperty(
        "errors",
        violations.stream()
            .sorted(
                Comparator.comparing(FieldViolation::field).thenComparing(FieldViolation::message))
            .toList());
    return problem;
  }

  private static ProblemDetail withInstance(ProblemDetail problem, HttpServletRequest request) {
    problem.setInstance(URI.create(request.getRequestURI()));
    return problem;
  }

  private static String lastNode(String propertyPath) {
    return propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
  }
}
