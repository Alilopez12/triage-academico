package co.edu.uniquindio.triage.controller;

import co.edu.uniquindio.triage.domain.enums.EstadoSolicitud;
import co.edu.uniquindio.triage.domain.enums.Prioridad;
import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.triage.dto.request.AsignarPrioridadRequest;
import co.edu.uniquindio.triage.dto.request.AsignarResponsableRequest;
import co.edu.uniquindio.triage.dto.request.CambiarEstadoRequest;
import co.edu.uniquindio.triage.dto.request.CerrarSolicitudRequest;
import co.edu.uniquindio.triage.dto.request.SolicitudCreateRequest;
import co.edu.uniquindio.triage.dto.response.HistorialSolicitudResponse;
import co.edu.uniquindio.triage.dto.response.SolicitudResponse;
import co.edu.uniquindio.triage.service.impl.SolicitudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import co.edu.uniquindio.triage.dto.request.ClasificarSolicitudRequest;
import co.edu.uniquindio.triage.dto.request.SugerirClasificacionRequest;
import co.edu.uniquindio.triage.dto.response.SugerenciaClasificacionResponse;
import co.edu.uniquindio.triage.service.impl.IAService;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final IAService iaService;

    public SolicitudController(SolicitudService solicitudService, IAService iaService) {
        this.solicitudService = solicitudService;
        this.iaService = iaService;
    }
    // RF-01 Registro
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudResponse registrarSolicitud(@Valid @RequestBody SolicitudCreateRequest request) {
        return solicitudService.registrarSolicitud(request);
    }

    @PatchMapping("/{id}/clasificar")
    public SolicitudResponse clasificarSolicitud(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @Valid @RequestBody ClasificarSolicitudRequest request) {

        return solicitudService.clasificarSolicitud(id, usuarioId, request);
    }

    // RF-03 Priorización + RF-13 autorización
    @PutMapping("/{id}/prioridad")
    public SolicitudResponse asignarPrioridad(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @Valid @RequestBody AsignarPrioridadRequest request) {

        return solicitudService.asignarPrioridad(id, request, usuarioId);
    }

    // RF-05 Asignación de responsables + RF-13 autorización
    @PatchMapping("/{id}/asignar")
    public SolicitudResponse asignarResponsable(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @Valid @RequestBody AsignarResponsableRequest request) {

        return solicitudService.asignarResponsable(id, request, usuarioId);
    }

    // RF-06 Historial
    @GetMapping("/{id}/historial")
    public List<HistorialSolicitudResponse> obtenerHistorial(@PathVariable Long id) {
        return solicitudService.obtenerHistorial(id);
    }

    // RF-04 Cambio de estado
    @PatchMapping("/{id}/estado")
    public SolicitudResponse cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request) {
        return solicitudService.cambiarEstado(id, request);
    }

    // RF-08 Cierre + RF-13 autorización
    @PutMapping("/{id}/cerrar")
    public SolicitudResponse cerrarSolicitud(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @Valid @RequestBody CerrarSolicitudRequest request) {

        return solicitudService.cerrarSolicitud(id, request, usuarioId);
    }

    // RF-09 opcional resumen
    @GetMapping("/{id}/resumen")
    public String generarResumen(@PathVariable Long id) {
        return solicitudService.generarResumenSolicitud(id);
    }

    // RF-07 Consulta con filtros
    @GetMapping
    public List<SolicitudResponse> listarSolicitudes(
            @RequestParam(required = false) EstadoSolicitud estado,
            @RequestParam(required = false) TipoSolicitud tipo,
            @RequestParam(required = false) Prioridad prioridad,
            @RequestParam(required = false) Long responsableId) {

        return solicitudService.listarSolicitudes(estado, tipo, prioridad, responsableId);
    }

    @PostMapping("/sugerir-clasificacion")
    public SugerenciaClasificacionResponse sugerirClasificacion(
            @Valid @RequestBody SugerirClasificacionRequest request) {
        return iaService.sugerirClasificacion(request.getDescripcion());
    }

    // Consulta por id
    @GetMapping("/{id}")
    public SolicitudResponse obtenerSolicitudPorId(@PathVariable Long id) {
        return solicitudService.obtenerSolicitudPorId(id);
    }
}