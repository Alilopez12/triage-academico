package co.edu.uniquindio.triage.controller;

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
import co.edu.uniquindio.triage.domain.enums.EstadoSolicitud;
import co.edu.uniquindio.triage.domain.enums.Prioridad;
import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.triage.dto.request.AsignarResponsableRequest;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    //Registrar solicitud
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudResponse registrarSolicitud(
            @Valid @RequestBody SolicitudCreateRequest request) {

        return solicitudService.registrarSolicitud(request);
    }

    // Clasificar solicitud (CON AUTORIZACIÓN)
    @PatchMapping("/{id}/clasificar")
    public SolicitudResponse clasificarSolicitud(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {

        return solicitudService.clasificarSolicitud(id, usuarioId);
    }

    //Asignar prioridad
    @PutMapping("/{id}/prioridad")
    public SolicitudResponse asignarPrioridad(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @Valid @RequestBody AsignarPrioridadRequest request) {

        return solicitudService.asignarPrioridad(id, request, usuarioId);
    }

    //Asignar responsable
    @PatchMapping("/{id}/asignar")
    public SolicitudResponse asignarResponsable(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @RequestBody AsignarResponsableRequest request) {

        return solicitudService.asignarResponsable(
                id,
                request.getResponsableId(),
                usuarioId
        );
    }

    //Obtener historial
    @GetMapping("/{id}/historial")
    public List<HistorialSolicitudResponse> obtenerHistorial(
            @PathVariable Long id) {

        return solicitudService.obtenerHistorial(id);
    }

    @GetMapping("/{id}/resumen")
    public String generarResumen(@PathVariable Long id) {
        return solicitudService.generarResumenSolicitud(id);
    }
}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudResponse registrarSolicitud(@Valid @RequestBody SolicitudCreateRequest request) {
        return solicitudService.registrarSolicitud(request);
    }
    @PutMapping("/{id}/prioridad")
    public SolicitudResponse asignarPrioridad(
            @PathVariable Long id,
            @Valid @RequestBody AsignarPrioridadRequest request) {

        return solicitudService.asignarPrioridad(id, request);
    }
    @PutMapping("/{id}/responsable")
    public SolicitudResponse asignarResponsable(
            @PathVariable Long id,
            @Valid @RequestBody AsignarResponsableRequest request) {

        return solicitudService.asignarResponsable(id, request);
    }

    @GetMapping("/{id}/historial")
    public List<HistorialSolicitudResponse> obtenerHistorial(@PathVariable Long id) {
        return solicitudService.obtenerHistorial(id);
    }

    @PutMapping("/{id}/estado")
    public SolicitudResponse cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request) {

        return solicitudService.cambiarEstado(id, request);
    }

    @PutMapping("/{id}/cerrar")
    public SolicitudResponse cerrarSolicitud(
            @PathVariable Long id,
            @Valid @RequestBody CerrarSolicitudRequest request) {

        return solicitudService.cerrarSolicitud(id, request);
    }
    @GetMapping
    public List<SolicitudResponse> listarSolicitudes(
            @RequestParam(required = false) EstadoSolicitud estado,
            @RequestParam(required = false) TipoSolicitud tipo,
            @RequestParam(required = false) Prioridad prioridad,
            @RequestParam(required = false) Long responsableId) {

        return solicitudService.listarSolicitudes(estado, tipo, prioridad, responsableId);
    }

    @GetMapping("/{id}")
    public SolicitudResponse obtenerSolicitudPorId(@PathVariable Long id) {
        return solicitudService.obtenerSolicitudPorId(id);
    }
}
