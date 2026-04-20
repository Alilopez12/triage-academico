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
    @Setter
    private String descripcion;
    @Setter
    private CanalOrigen canalOrigen;
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
    @Setter
    private ImpactoAcademico impactoAcademico;
    @Setter
    private Long solicitanteId;
    @Setter
    private String nombreSolicitante;
    @Setter
    private Long responsableAsignadoId;
    @Setter
    private String nombreResponsableAsignado;
    @Setter
    private String observacionCierre;
    @Getter
    @Setter
    private List<HistorialSolicitudResponse> historial;
    @Setter
    private LocalDate fechaLimite;
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
                             List<HistorialSolicitudResponse> historial) {
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
    }

    public String getDescripcion() {
        return descripcion;
    }

    public CanalOrigen getCanalOrigen() {
        return canalOrigen;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public ImpactoAcademico getImpactoAcademico() {
        return impactoAcademico;
    }

    public Long getSolicitanteId() {
        return solicitanteId;
    }

    public String getNombreSolicitante() {
        return nombreSolicitante;
    }

    public Long getResponsableAsignadoId() {
        return responsableAsignadoId;
    }

    public String getNombreResponsableAsignado() {
        return nombreResponsableAsignado;
    }

    public String getObservacionCierre() {
        return observacionCierre;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setVersion(Long version) {
    }
}