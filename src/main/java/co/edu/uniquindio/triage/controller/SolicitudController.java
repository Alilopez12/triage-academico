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
import co.edu.uniquindio.triage.dto.response.HistorialSolicitudResponse;
import co.edu.uniquindio.triage.dto.response.PageResponse;
import co.edu.uniquindio.triage.dto.response.SolicitudResponse;
import co.edu.uniquindio.triage.exception.RecursoNoEncontradoException;
import co.edu.uniquindio.triage.repository.UsuarioRepository;
import co.edu.uniquindio.triage.service.SolicitudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@Tag(
        name = "solicitud-controller",
        description = "Operaciones del ciclo de vida de solicitudes académicas"
)
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final UsuarioRepository usuarioRepository;

    public SolicitudController(SolicitudService solicitudService, UsuarioRepository usuarioRepository) {
        this.solicitudService = solicitudService;
        this.usuarioRepository = usuarioRepository;
    }

    private Long resolverUsuarioId(Authentication authentication) {
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario autenticado no encontrado"))
                .getId();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudResponse registrarSolicitud(@Valid @RequestBody SolicitudCreateRequest request,
                                                Authentication authentication) {
        return solicitudService.registrarSolicitud(request, resolverUsuarioId(authentication));
    }

    @PatchMapping("/{id}/clasificar")
    public SolicitudResponse clasificarSolicitud(
            @PathVariable Long id,
            @Valid @RequestBody ClasificarSolicitudRequest request,
            Authentication authentication) {

        return solicitudService.clasificarSolicitud(id, resolverUsuarioId(authentication), request);
    }

    @PutMapping("/{id}/prioridad")
    public SolicitudResponse asignarPrioridad(
            @PathVariable Long id,
            @Valid @RequestBody AsignarPrioridadRequest request,
            Authentication authentication) {

        return solicitudService.asignarPrioridad(id, request, resolverUsuarioId(authentication));
    }

    @PatchMapping("/{id}/asignar")
    public SolicitudResponse asignarResponsable(
            @PathVariable Long id,
            @Valid @RequestBody AsignarResponsableRequest request,
            Authentication authentication) {

        return solicitudService.asignarResponsable(id, request, resolverUsuarioId(authentication));
    }

    @GetMapping("/{id}/historial")
    public List<HistorialSolicitudResponse> obtenerHistorial(@PathVariable Long id) {
        return solicitudService.obtenerHistorial(id);
    }

    @PatchMapping("/{id}/estado")
    public SolicitudResponse cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request,
            Authentication authentication) {
        return solicitudService.cambiarEstado(id, request, resolverUsuarioId(authentication));
    }

    @PutMapping("/{id}/cerrar")
    public SolicitudResponse cerrarSolicitud(
            @PathVariable Long id,
            @Valid @RequestBody CerrarSolicitudRequest request,
            Authentication authentication) {

        return solicitudService.cerrarSolicitud(id, request, resolverUsuarioId(authentication));
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

        return solicitudService.listarSolicitudes(
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

    @GetMapping("/{id}")
    public SolicitudResponse obtenerSolicitudPorId(@PathVariable Long id) {
        return solicitudService.obtenerSolicitudPorId(id);
    }
}
