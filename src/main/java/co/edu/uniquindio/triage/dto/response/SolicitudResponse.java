package co.edu.uniquindio.triage.dto.response;

import co.edu.uniquindio.triage.domain.enums.CanalOrigen;
import co.edu.uniquindio.triage.domain.enums.EstadoSolicitud;
import co.edu.uniquindio.triage.domain.enums.ImpactoAcademico;
import co.edu.uniquindio.triage.domain.enums.Prioridad;
import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class SolicitudResponse {

    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private TipoSolicitud tipo;

    @Getter
    @Setter
    private String descripcion;

    @Getter
    @Setter
    private CanalOrigen canalOrigen;

    @Getter
    @Setter
    private LocalDateTime fechaRegistro;

    @Getter
    @Setter
    private Prioridad prioridad;

    @Getter
    @Setter
    private String justificacionPrioridad;

    @Getter
    @Setter
    private EstadoSolicitud estado;

    @Getter
    @Setter
    private ImpactoAcademico impactoAcademico;

    @Getter
    @Setter
    private Long solicitanteId;

    @Getter
    @Setter
    private String nombreSolicitante;

    @Getter
    @Setter
    private Long responsableAsignadoId;

    @Getter
    @Setter
    private String nombreResponsableAsignado;

    @Getter
    @Setter
    private String observacionCierre;

    @Getter
    @Setter
    private List<HistorialSolicitudResponse> historial;

    @Getter
    @Setter
    private LocalDate fechaLimite;

    @Getter
    @Setter
    private Long version;

    public SolicitudResponse() {
    }

    public SolicitudResponse(Long id,
                             TipoSolicitud tipo,
                             String descripcion,
                             CanalOrigen canalOrigen,
                             LocalDateTime fechaRegistro,
                             Prioridad prioridad,
                             String justificacionPrioridad,
                             EstadoSolicitud estado,
                             ImpactoAcademico impactoAcademico,
                             Long solicitanteId,
                             String nombreSolicitante,
                             Long responsableAsignadoId,
                             String nombreResponsableAsignado,
                             String observacionCierre,
                             LocalDate fechaLimite,
                             List<HistorialSolicitudResponse> historial,
                             Long version) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.canalOrigen = canalOrigen;
        this.fechaRegistro = fechaRegistro;
        this.prioridad = prioridad;
        this.justificacionPrioridad = justificacionPrioridad;
        this.estado = estado;
        this.impactoAcademico = impactoAcademico;
        this.solicitanteId = solicitanteId;
        this.nombreSolicitante = nombreSolicitante;
        this.responsableAsignadoId = responsableAsignadoId;
        this.nombreResponsableAsignado = nombreResponsableAsignado;
        this.observacionCierre = observacionCierre;
        this.historial = historial;
        this.fechaLimite = fechaLimite;
        this.version = version;
    }
}