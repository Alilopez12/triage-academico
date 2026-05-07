package co.edu.uniquindio.triage.controller;

import co.edu.uniquindio.triage.dto.request.SugerirClasificacionRequest;
import co.edu.uniquindio.triage.dto.response.SugerenciaClasificacionResponse;
import co.edu.uniquindio.triage.service.IAService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ia")
@Tag(name = "ia-controller", description = "Operaciones de asistencia mediante IA para solicitudes académicas")
public class IAController {

    private final IAService iaService;

    public IAController(IAService iaService) {
        this.iaService = iaService;
    }

    @PostMapping("/sugerir-clasificacion")
    public SugerenciaClasificacionResponse sugerirClasificacion(
            @Valid @RequestBody SugerirClasificacionRequest request
    ) {
        return iaService.sugerirClasificacion(request.getDescripcion());
    }

    @GetMapping("/solicitudes/{id}/resumen")
    public String generarResumen(@PathVariable Long id) {
        return iaService.generarResumenSolicitud(id);
    }
}