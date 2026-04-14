package co.edu.uniquindio.triage.domain.model;

import co.edu.uniquindio.triage.domain.enums.CanalOrigen;
import co.edu.uniquindio.triage.domain.enums.EstadoSolicitud;
import co.edu.uniquindio.triage.domain.enums.ImpactoAcademico;
import co.edu.uniquindio.triage.domain.enums.Prioridad;
import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.triage.exception.ReglaNegocioException;
import co.edu.uniquindio.triage.exception.TransicionInvalidaException;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Solicitud {

    private Long id;
    private TipoSolicitud tipo;
    private String descripcion;
    private CanalOrigen canalOrigen;
    private LocalDateTime fechaRegistro;
    private Prioridad prioridad;
    private String justificacionPrioridad;
    private EstadoSolicitud estado;
    private ImpactoAcademico impactoAcademico;
    private LocalDate fechaLimite;
    private Usuario solicitante;
    private Usuario responsableAsignado;
    private String observacionCierre;

    public Solicitud() {
    }

    public Solicitud(Long id,
                     TipoSolicitud tipo,
                     String descripcion,
                     CanalOrigen canalOrigen,
                     LocalDateTime fechaRegistro,
                     Prioridad prioridad,
                     String justificacionPrioridad,
                     EstadoSolicitud estado,
                     ImpactoAcademico impactoAcademico,
                     LocalDate fechaLimite,
                     Usuario solicitante,
                     Usuario responsableAsignado,
                     String observacionCierre) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.canalOrigen = canalOrigen;
        this.fechaRegistro = fechaRegistro;
        this.prioridad = prioridad;
        this.justificacionPrioridad = justificacionPrioridad;
        this.estado = estado;
        this.impactoAcademico = impactoAcademico;
        this.fechaLimite = fechaLimite;
        this.solicitante = solicitante;
        this.responsableAsignado = responsableAsignado;
        this.observacionCierre = observacionCierre;
    }

    public static Solicitud crear(TipoSolicitud tipo,
                                  String descripcion,
                                  CanalOrigen canalOrigen,
                                  Usuario solicitante) {

        if (tipo == null) {
            throw new ReglaNegocioException("El tipo de solicitud es obligatorio.");
        }

        if (descripcion == null || descripcion.isBlank()) {
            throw new ReglaNegocioException("La descripción de la solicitud es obligatoria.");
        }

        if (canalOrigen == null) {
            throw new ReglaNegocioException("El canal de origen es obligatorio.");
        }

        if (solicitante == null) {
            throw new ReglaNegocioException("El solicitante es obligatorio.");
        }

        Solicitud solicitud = new Solicitud();
        solicitud.setTipo(tipo);
        solicitud.setDescripcion(descripcion.trim());
        solicitud.setCanalOrigen(canalOrigen);
        solicitud.setFechaRegistro(LocalDateTime.now());
        solicitud.setEstado(EstadoSolicitud.REGISTRADA);
        solicitud.setSolicitante(solicitante);
        solicitud.setPrioridad(null);
        solicitud.setJustificacionPrioridad(null);
        solicitud.setImpactoAcademico(null);
        solicitud.setFechaLimite(null);
        solicitud.setResponsableAsignado(null);
        solicitud.setObservacionCierre(null);

        return solicitud;
    }

    public void asignarImpactoAcademico(ImpactoAcademico impactoAcademico) {
        if (this.estado == EstadoSolicitud.CERRADA) {
            throw new ReglaNegocioException("No se puede modificar una solicitud cerrada.");
        }

        if (impactoAcademico == null) {
            throw new ReglaNegocioException("El impacto académico es obligatorio.");
        }

        this.impactoAcademico = impactoAcademico;
    }

    public void asignarFechaLimite(LocalDate fechaLimite) {
        if (this.estado == EstadoSolicitud.CERRADA) {
            throw new ReglaNegocioException("No se puede modificar una solicitud cerrada.");
        }

        if (fechaLimite == null) {
            throw new ReglaNegocioException("La fecha límite es obligatoria.");
        }

        this.fechaLimite = fechaLimite;
    }

    public void asignarPrioridad(Prioridad prioridad, String justificacion) {
        if (this.estado == EstadoSolicitud.CERRADA) {
            throw new ReglaNegocioException("No se puede modificar una solicitud cerrada.");
        }

        if (prioridad == null) {
            throw new ReglaNegocioException("La prioridad es obligatoria.");
        }

        if (justificacion == null || justificacion.isBlank()) {
            throw new ReglaNegocioException("La justificación de la prioridad es obligatoria.");
        }

        this.prioridad = prioridad;
        this.justificacionPrioridad = justificacion.trim();
    }

    public void asignarResponsable(Usuario responsable) {
        if (this.estado == EstadoSolicitud.CERRADA) {
            throw new ReglaNegocioException("No se puede modificar una solicitud cerrada.");
        }

        if (responsable == null) {
            throw new ReglaNegocioException("El responsable es obligatorio.");
        }

        if (!responsable.estaActivo()) {
            throw new ReglaNegocioException("El responsable asignado debe estar activo.");
        }

        this.responsableAsignado = responsable;
    }

    public void clasificar(TipoSolicitud tipo) {
        if (this.estado != EstadoSolicitud.REGISTRADA) {
            throw new TransicionInvalidaException("Solo se puede clasificar una solicitud en estado REGISTRADA.");
        }

        if (tipo == null) {
            throw new ReglaNegocioException("El tipo de solicitud es obligatorio.");
        }

        this.tipo = tipo;
        this.estado = EstadoSolicitud.CLASIFICADA;
    }

    public void cambiarEstado(EstadoSolicitud nuevoEstado) {
        if (this.estado == EstadoSolicitud.CERRADA) {
            throw new ReglaNegocioException("No se puede modificar una solicitud cerrada.");
        }

        if (!validarTransicion(nuevoEstado)) {
            throw new TransicionInvalidaException("La transición de estado no es válida.");
        }

        this.estado = nuevoEstado;
    }

    public void cerrar(String observacionCierre) {
        if (this.estado != EstadoSolicitud.ATENDIDA) {
            throw new TransicionInvalidaException("Solo se puede cerrar una solicitud que esté ATENDIDA.");
        }

        if (observacionCierre == null || observacionCierre.isBlank()) {
            throw new ReglaNegocioException("La observación de cierre es obligatoria.");
        }

        this.estado = EstadoSolicitud.CERRADA;
        this.observacionCierre = observacionCierre.trim();
    }

    public boolean validarTransicion(EstadoSolicitud nuevoEstado) {
        if (this.estado == null || nuevoEstado == null) {
            return false;
        }

        return switch (this.estado) {
            case REGISTRADA -> nuevoEstado == EstadoSolicitud.CLASIFICADA;
            case CLASIFICADA -> nuevoEstado == EstadoSolicitud.EN_ATENCION;
            case EN_ATENCION -> nuevoEstado == EstadoSolicitud.ATENDIDA;
            case ATENDIDA -> nuevoEstado == EstadoSolicitud.CERRADA;
            case CERRADA -> false;
        };
    }

    public boolean estaCerrada() {
        return this.estado == EstadoSolicitud.CERRADA;
    }

    public Prioridad calcularPrioridad() {

        if (impactoAcademico == null || fechaLimite == null) {
            throw new ReglaNegocioException("No se puede calcular la prioridad sin impacto académico y fecha límite.");
        }

        long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaLimite);

        if (impactoAcademico == ImpactoAcademico.ALTO && diasRestantes <= 3) {
            return Prioridad.CRITICA;
        }

        if (impactoAcademico == ImpactoAcademico.ALTO) {
            return Prioridad.ALTA;
        }

        if (impactoAcademico == ImpactoAcademico.MEDIO) {
            return Prioridad.MEDIA;
        }

        return Prioridad.BAJA;
    }

    public void calcularYAsignarPrioridad() {

        Prioridad prioridadCalculada = calcularPrioridad();

        this.prioridad = prioridadCalculada;
        this.justificacionPrioridad = "Prioridad calculada automáticamente según impacto académico y fecha límite.";
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

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public Usuario getResponsableAsignado() {
        return responsableAsignado;
    }

    public String getObservacionCierre() {
        return observacionCierre;
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

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public void setSolicitante(Usuario solicitante) {
        this.solicitante = solicitante;
    }

    public void setResponsableAsignado(Usuario responsableAsignado) {
        this.responsableAsignado = responsableAsignado;
    }

    public void setObservacionCierre(String observacionCierre) {
        this.observacionCierre = observacionCierre;
    }
}