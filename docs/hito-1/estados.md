# DEFINICIÓN DE ESTADOS
Sistema de Triage Académico

Los estados representan las diferentes fases del ciclo de vida de una Solicitud dentro del sistema de Triage Académico.
Cada estado indica el nivel de avance del proceso y determina qué acciones o transiciones están permitidas en la API.

## 1. Estados de la entidad Solicitud


Una solicitud puede encontrarse en uno de los siguientes estados:

- REGISTRADA
- CLASIFICADA
- EN_ATENCION
- ATENDIDA
- CERRADA

## 2. Descripción de cada estado

### REGISTRADA
Estado inicial cuando la solicitud es creada en el sistema.

### CLASIFICADA
La solicitud ha sido categorizada según su tipo.

### EN_ATENCION
La solicitud ha sido asignada a un responsable y está siendo gestionada.

### ATENDIDA
La solicitud ha sido resuelta y está lista para cierre.

### CERRADA
Estado final del proceso. No permite modificaciones adicionales.

## 3. Reglas globales del ciclo de vida

- No se permite saltar estados.
- CERRADA es un estado final.
- Toda transición genera un registro en HistorialMovimiento.
- Si una transición no es válida, se debe retornar código HTTP 409 Conflict.


