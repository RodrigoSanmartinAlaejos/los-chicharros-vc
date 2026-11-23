# Casos de uso por rol

Última revisión: 2026-11-22.

Qué puede hacer cada rol en la web, en todo el proyecto. Cada caso de uso indica la fase en la que se implementa (F1 a F4). Los permisos exactos, incluida la diferencia entre «sobre cualquier registro» y «solo sobre lo suyo», están en la [matriz de permisos](../02-requisitos/matriz-permisos.md); si este documento y la matriz no coinciden, manda la matriz.

Reglas generales:

- Los permisos se suman: quien tiene varios roles hace lo que permita cualquiera de ellos.
- Lo que puede el visitante lo puede cualquier usuario con sesión.
- Todo usuario con sesión puede ver y editar su perfil, ver sus equipos y partidos, leer sus avisos y pedir la exportación o el borrado de sus datos (F2). Para no repetirlo, no aparece en cada diagrama.
- En la fase 1 solo existe el administrador; el resto de roles llega en la fase 2.

## Visitante

```mermaid
flowchart LR
    V(["Visitante"])
    subgraph PUB [Web pública]
        V1["Ver noticias · F1"]
        V2["Ver equipos y plantillas públicas · F1"]
        V3["Ver calendario de partidos · F1"]
        V4["Ver clasificación y resultados · F1"]
        V5["Ver páginas del club · F1"]
        V6["Suscribirse al calendario iCal de un equipo · F4"]
    end
    V --> PUB
```

## Socio y tutor

El tutor hace por sus menores lo mismo que el socio hace por sí mismo.

```mermaid
flowchart LR
    S(["Socio / jugador"])
    T(["Tutor"])
    subgraph PRIV [Lo suyo o lo de sus menores]
        P1["Responder a una convocatoria · F3"]
        P2["Ver su asistencia y sus convocatorias · F3"]
        P3["Dar o retirar el consentimiento de imagen · F2"]
        P4["Ver sus cuotas y su estado · F4"]
        P5["Pagar una cuota online · F4"]
    end
    subgraph TUT [Solo tutor]
        P6["Ver y editar los datos de sus menores · F2"]
    end
    S --> PRIV
    T --> PRIV
    T --> TUT
```

## Entrenador y coordinador deportivo

El entrenador actúa solo sobre sus equipos; el coordinador, sobre todos.

```mermaid
flowchart LR
    E(["Entrenador"])
    C(["Coordinador deportivo"])
    subgraph DEP [Gestión deportiva: entrenador en sus equipos, coordinador en todos]
        D1["Ver la plantilla con datos privados · F2"]
        D2["Editar dorsal y posición · F2"]
        D3["Subir foto de jugador o escudo · F1"]
        D4["Convocar a un partido y ver respuestas · F3"]
        D5["Gestionar horario de entrenamientos · F3"]
        D6["Pasar lista y ver estadísticas de asistencia · F3"]
        D7["Introducir resultado set a set · F3"]
        D8["Publicar un aviso a un equipo o persona · F2"]
        D9["Exportar listados de asistencia · F4"]
    end
    subgraph COO [Solo coordinador]
        K1["Crear y editar equipos · F1"]
        K2["Dar de alta personas y moverlas de equipo · F1"]
        K3["Asignar entrenadores · F2"]
        K4["Gestionar competiciones, jornadas y partidos · F1"]
        K5["Aplazar o cancelar partidos · F1"]
        K6["Corregir resultados y registrar incomparecencias · F3"]
        K7["Registrar tutelas · F2"]
        K8["Publicar un aviso general · F2"]
    end
    E --> DEP
    C --> DEP
    C --> COO
```

## Comunicación, tesorero y administrador

```mermaid
flowchart LR
    M(["Comunicación"])
    TS(["Tesorero"])
    A(["Administrador"])
    subgraph COM [Comunicación]
        M1["Crear, editar, publicar y retirar noticias · F1"]
        M2["Subir imágenes de noticias, fotos y escudos · F1"]
        M3["Publicar un aviso general · F2"]
    end
    subgraph TES [Tesorería]
        T1["Crear, editar y anular cuotas · F4"]
        T2["Registrar un pago manual · F4"]
        T3["Ver el estado de cuotas de todos · F4"]
        T4["Enviar recordatorios de pago · F4"]
        T5["Exportar socios e informes económicos · F4"]
        T6["Publicar un aviso a una persona · F2"]
    end
    subgraph CFG [Configuración]
        A1["Crear, desactivar e invitar usuarios · F2"]
        A2["Asignar y quitar roles · F2"]
        A3["Gestionar temporadas, categorías e instalaciones · F1"]
        A4["Consultar el historial de cambios · F3"]
        A5["Atender peticiones de exportación o borrado · F2"]
        A6["Editar la clasificación a mano (hasta F3) · F1"]
    end
    M --> COM
    TS --> TES
    A --> CFG
```

El administrador puede hacer además todo lo de comunicación, tesorería, coordinador y entrenador; no se dibuja para que el diagrama se pueda leer.

## Casos de uso de la fase 1

En la fase 1 solo hay visitante y administrador. El administrador hace todos los casos marcados como F1 de cualquier rol:

| Rol | Casos de uso F1 |
| --- | --- |
| Visitante | Ver noticias, equipos, calendario, clasificación y páginas del club |
| Administrador | Iniciar y cerrar sesión; noticias e imágenes; equipos, personas y plantillas; competiciones, jornadas y partidos (incluido aplazar o cancelar); marcador de sets; clasificación a mano; temporadas, categorías e instalaciones |
