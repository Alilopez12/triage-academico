package co.edu.uniquindio.triage.controller;

import co.edu.uniquindio.triage.dto.request.AsignarPrioridadRequest;
import co.edu.uniquindio.triage.dto.request.SolicitudCreateRequest;
import co.edu.uniquindio.triage.dto.response.HistorialSolicitudResponse;
import co.edu.uniquindio.triage.dto.response.SolicitudResponse;
import co.edu.uniquindio.triage.service.impl.SolicitudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
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

    @GetMapping("/{id}/historial")
    public List<HistorialSolicitudResponse> obtenerHistorial(@PathVariable Long id) {
        return solicitudService.obtenerHistorial(id);
    }
}