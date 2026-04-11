package co.edu.uniquindio.triage.dto.response;

import co.edu.uniquindio.triage.domain.enums.CanalOrigen;
import co.edu.uniquindio.triage.domain.enums.EstadoSolicitud;
import co.edu.uniquindio.triage.domain.enums.ImpactoAcademico;
import co.edu.uniquindio.triage.domain.enums.Prioridad;
import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;

import java.time.LocalDateTime;
import java.util.List;

public class SolicitudResponse {

    private Long id;
    private TipoSolicitud tipo;
    private String descripcion;
    private CanalOrigen canalOrigen;
    private LocalDateTime fechaRegistro;
    private Prioridad prioridad;
    private String justificacionPrioridad;
    private EstadoSolicitud estado;
    private ImpactoAcademico impactoAcademico;
    private Long solicitanteId;
    private String nombreSolicitante;
    private Long responsableAsignadoId;
    private String nombreResponsableAsignado;
    private String observacionCierre;
    private List<HistorialSolicitudResponse> historial;

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
    }

    public Long getId() {
        return id;
    }

    public TipoSolicitud getTipo() {
        return tipo;
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

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public String getJustificacionPrioridad() {
        return justificacionPrioridad;
    }

    public EstadoSolicitud getEstado() {
        return estado;
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

    public List<HistorialSolicitudResponse> getHistorial() {
        return historial;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTipo(TipoSolicitud tipo) {
        this.tipo = tipo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setCanalOrigen(CanalOrigen canalOrigen) {
        this.canalOrigen = canalOrigen;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public void setJustificacionPrioridad(String justificacionPrioridad) {
        this.justificacionPrioridad = justificacionPrioridad;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public void setImpactoAcademico(ImpactoAcademico impactoAcademico) {
        this.impactoAcademico = impactoAcademico;
    }

    public void setSolicitanteId(Long solicitanteId) {
        this.solicitanteId = solicitanteId;
    }

    public void setNombreSolicitante(String nombreSolicitante) {
        this.nombreSolicitante = nombreSolicitante;
    }

    public void setResponsableAsignadoId(Long responsableAsignadoId) {
        this.responsableAsignadoId = responsableAsignadoId;
    }

    public void setNombreResponsableAsignado(String nombreResponsableAsignado) {
        this.nombreResponsableAsignado = nombreResponsableAsignado;
    }

    public void setObservacionCierre(String observacionCierre) {
        this.observacionCierre = observacionCierre;
    }

    public void setHistorial(List<HistorialSolicitudResponse> historial) {
        this.historial = historial;
    }
}