package co.edu.uniquindio.triage.controller;

import co.edu.uniquindio.triage.domain.enums.EstadoSolicitud;
import co.edu.uniquindio.triage.domain.enums.Prioridad;
import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.triage.dto.request.AsignarPrioridadRequest;
import co.edu.uniquindio.triage.dto.request.AsignarResponsableRequest;
import co.edu.uniquindio.triage.dto.request.CambiarEstadoRequest;
import co.edu.uniquindio.triage.dto.request.CerrarSolicitudRequest;
import co.edu.uniquindio.triage.dto.request.ClasificarSolicitudRequest;
import co.edu.uniquindio.triage.dto.request.SolicitudCreateRequest;
import co.edu.uniquindio.triage.dto.request.SugerirClasificacionRequest;
import co.edu.uniquindio.triage.dto.response.HistorialSolicitudResponse;
import co.edu.uniquindio.triage.dto.response.SolicitudResponse;
import co.edu.uniquindio.triage.dto.response.SugerenciaClasificacionResponse;
import co.edu.uniquindio.triage.service.AsignarPrioridadUseCase;
import co.edu.uniquindio.triage.service.AsignarResponsableUseCase;
import co.edu.uniquindio.triage.service.CambiarEstadoSolicitudUseCase;
import co.edu.uniquindio.triage.service.CerrarSolicitudUseCase;
import co.edu.uniquindio.triage.service.ClasificarSolicitudUseCase;
import co.edu.uniquindio.triage.service.ConsultarSolicitudUseCase;
import co.edu.uniquindio.triage.service.RegistrarSolicitudUseCase;
import co.edu.uniquindio.triage.service.impl.IAService;
import co.edu.uniquindio.triage.service.impl.SolicitudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import co.edu.uniquindio.triage.dto.response.PageResponse;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final RegistrarSolicitudUseCase registrarSolicitudUseCase;
    private final ClasificarSolicitudUseCase clasificarSolicitudUseCase;
    private final AsignarPrioridadUseCase asignarPrioridadUseCase;
    private final AsignarResponsableUseCase asignarResponsableUseCase;
    private final CambiarEstadoSolicitudUseCase cambiarEstadoSolicitudUseCase;
    private final CerrarSolicitudUseCase cerrarSolicitudUseCase;
    private final ConsultarSolicitudUseCase consultarSolicitudUseCase;
    private final SolicitudService solicitudService;
    private final IAService iaService;


    public SolicitudController(RegistrarSolicitudUseCase registrarSolicitudUseCase,
                               ClasificarSolicitudUseCase clasificarSolicitudUseCase,
                               AsignarPrioridadUseCase asignarPrioridadUseCase,
                               AsignarResponsableUseCase asignarResponsableUseCase,
                               CambiarEstadoSolicitudUseCase cambiarEstadoSolicitudUseCase,
                               CerrarSolicitudUseCase cerrarSolicitudUseCase,
                               ConsultarSolicitudUseCase consultarSolicitudUseCase,
                               SolicitudService solicitudService,
                               IAService iaService) {
        this.registrarSolicitudUseCase = registrarSolicitudUseCase;
        this.clasificarSolicitudUseCase = clasificarSolicitudUseCase;
        this.asignarPrioridadUseCase = asignarPrioridadUseCase;
        this.asignarResponsableUseCase = asignarResponsableUseCase;
        this.cambiarEstadoSolicitudUseCase = cambiarEstadoSolicitudUseCase;
        this.cerrarSolicitudUseCase = cerrarSolicitudUseCase;
        this.consultarSolicitudUseCase = consultarSolicitudUseCase;
        this.solicitudService = solicitudService;
        this.iaService = iaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudResponse registrarSolicitud(@Valid @RequestBody SolicitudCreateRequest request) {
        return registrarSolicitudUseCase.ejecutar(request);
    }

    @PatchMapping("/{id}/clasificar")
    public SolicitudResponse clasificarSolicitud(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @Valid @RequestBody ClasificarSolicitudRequest request) {

        return clasificarSolicitudUseCase.ejecutar(id, usuarioId, request);
    }

    @PutMapping("/{id}/prioridad")
    public SolicitudResponse asignarPrioridad(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @Valid @RequestBody AsignarPrioridadRequest request) {

        return asignarPrioridadUseCase.ejecutar(id, request, usuarioId);
    }


    @PatchMapping("/{id}/asignar")
    public SolicitudResponse asignarResponsable(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @Valid @RequestBody AsignarResponsableRequest request) {

        return asignarResponsableUseCase.ejecutar(id, request, usuarioId);
    }

    @GetMapping("/{id}/historial")
    public List<HistorialSolicitudResponse> obtenerHistorial(@PathVariable Long id) {
        return consultarSolicitudUseCase.obtenerHistorial(id);
    }

    @PatchMapping("/{id}/estado")
    public SolicitudResponse cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request) {
        return cambiarEstadoSolicitudUseCase.ejecutar(id, request);
    }

    @PutMapping("/{id}/cerrar")
    public SolicitudResponse cerrarSolicitud(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @Valid @RequestBody CerrarSolicitudRequest request) {

        return cerrarSolicitudUseCase.ejecutar(id, request, usuarioId);
    }

    @GetMapping("/{id}/resumen")
    public String generarResumen(@PathVariable Long id) {
        return solicitudService.generarResumenSolicitud(id);
    }

    @Operation(
            summary = "Listar solicitudes con filtros",
            description = "Permite consultar solicitudes por estado, tipo, prioridad y responsable asignado, con paginación y ordenamiento."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitudes consultadas correctamente")
    })
    @GetMapping
    public PageResponse<SolicitudResponse> listarSolicitudes(
            @RequestParam(required = false) EstadoSolicitud estado,
            @RequestParam(required = false) TipoSolicitud tipo,
            @RequestParam(required = false) Prioridad prioridad,
            @RequestParam(required = false) Long responsableId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        return consultarSolicitudUseCase.listar(
                estado,
                tipo,
                prioridad,
                responsableId,
                page,
                size,
                sortBy,
                direction
        );
    }

    @PostMapping("/sugerir-clasificacion")
    public SugerenciaClasificacionResponse sugerirClasificacion(
            @Valid @RequestBody SugerirClasificacionRequest request) {
        return iaService.sugerirClasificacion(request.getDescripcion());
    }

    @GetMapping("/{id}")
    public SolicitudResponse obtenerSolicitudPorId(@PathVariable Long id) {
        return consultarSolicitudUseCase.obtenerPorId(id);
    }
}