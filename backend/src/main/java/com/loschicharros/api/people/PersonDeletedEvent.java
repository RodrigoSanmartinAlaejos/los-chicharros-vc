package com.loschicharros.api.people;

/**
 * Una persona se acaba de borrar (borrado lógico). Se publica dentro de la misma transacción, para
 * que otras áreas reaccionen sin que {@code people} dependa de ellas: por ejemplo, las plantillas
 * de las temporadas en curso la quitan.
 */
public record PersonDeletedEvent(Long personId) {}
