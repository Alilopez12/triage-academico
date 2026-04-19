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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @Operation(
            summary = "Registrar una nueva solicitud",
            description = "Permite a un estudiante registrar una solicitud académica."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Solicitud creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario solicitante no encontrado")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudResponse registrarSolicitud(@Valid @RequestBody SolicitudCreateRequest request) {
        return registrarSolicitudUseCase.ejecutar(request);
    }

    @Operation(
            summary = "Clasificar una solicitud",
            description = "Permite clasificar una solicitud según su tipo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud clasificada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Solicitud o usuario no encontrado")
    })
    @PatchMapping("/{id}/clasificar")
    public SolicitudResponse clasificarSolicitud(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @Valid @RequestBody ClasificarSolicitudRequest request) {

        return clasificarSolicitudUseCase.ejecutar(id, usuarioId, request);
    }

    @Operation(
            summary = "Asignar prioridad a una solicitud",
            description = "Calcula y asigna prioridad con base en impacto académico y fecha límite."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prioridad asignada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Solicitud o usuario no encontrado")
    })
    @PutMapping("/{id}/prioridad")
    public SolicitudResponse asignarPrioridad(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @Valid @RequestBody AsignarPrioridadRequest request) {

        return asignarPrioridadUseCase.ejecutar(id, request, usuarioId);
    }

    @Operation(
            summary = "Asignar responsable a una solicitud",
            description = "Asigna un responsable autorizado a una solicitud."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Responsable asignado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o responsable no autorizado"),
            @ApiResponse(responseCode = "404", description = "Solicitud o usuario no encontrado")
    })
    @PatchMapping("/{id}/asignar")
    public SolicitudResponse asignarResponsable(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @Valid @RequestBody AsignarResponsableRequest request) {

        return asignarResponsableUseCase.ejecutar(id, request, usuarioId);
    }

    @Operation(
            summary = "Consultar historial de una solicitud",
            description = "Obtiene el historial auditable completo de una solicitud."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial consultado correctamente"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    })
    @GetMapping("/{id}/historial")
    public List<HistorialSolicitudResponse> obtenerHistorial(@PathVariable Long id) {
        return consultarSolicitudUseCase.obtenerHistorial(id);
    }

    @Operation(
            summary = "Cambiar estado de una solicitud",
            description = "Permite avanzar una solicitud en su ciclo de vida validando transiciones coherentes."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado cambiado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Solicitud o usuario no encontrado")
    })
    @PatchMapping("/{id}/estado")
    public SolicitudResponse cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request) {
        return cambiarEstadoSolicitudUseCase.ejecutar(id, request);
    }

    @Operation(
            summary = "Cerrar una solicitud",
            description = "Cierra una solicitud atendida registrando la observación de cierre."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud cerrada correctamente"),
            @ApiResponse(responseCode = "400", description = "No se puede cerrar la solicitud"),
            @ApiResponse(responseCode = "404", description = "Solicitud o usuario no encontrado")
    })
    @PutMapping("/{id}/cerrar")
    public SolicitudResponse cerrarSolicitud(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @Valid @RequestBody CerrarSolicitudRequest request) {

        return cerrarSolicitudUseCase.ejecutar(id, request, usuarioId);
    }

    @Operation(
            summary = "Generar resumen de una solicitud",
            description = "Genera un resumen textual del estado e historial de la solicitud."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resumen generado correctamente"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    })
    @GetMapping("/{id}/resumen")
    public String generarResumen(@PathVariable Long id) {
        return solicitudService.generarResumenSolicitud(id);
    }

    @Operation(
            summary = "Listar solicitudes con filtros",
            description = "Permite consultar solicitudes por estado, tipo, prioridad y responsable asignado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitudes consultadas correctamente")
    })
    @GetMapping
    public List<SolicitudResponse> listarSolicitudes(
            @RequestParam(required = false) EstadoSolicitud estado,
            @RequestParam(required = false) TipoSolicitud tipo,
            @RequestParam(required = false) Prioridad prioridad,
            @RequestParam(required = false) Long responsableId) {

        return consultarSolicitudUseCase.listar(estado, tipo, prioridad, responsableId);
    }

    @Operation(
            summary = "Sugerir clasificación automática",
            description = "Genera una sugerencia de tipo y prioridad a partir de la descripción ingresada."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sugerencia generada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/sugerir-clasificacion")
    public SugerenciaClasificacionResponse sugerirClasificacion(
            @Valid @RequestBody SugerirClasificacionRequest request) {
        return iaService.sugerirClasificacion(request.getDescripcion());
    }

    @Operation(
            summary = "Consultar solicitud por id",
            description = "Obtiene una solicitud específica por su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud encontrada"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    })
    @GetMapping("/{id}")
    public SolicitudResponse obtenerSolicitudPorId(@PathVariable Long id) {
        return consultarSolicitudUseCase.obtenerPorId(id);
    }
}