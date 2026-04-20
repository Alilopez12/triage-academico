package co.edu.uniquindio.triage.dto.request;

import co.edu.uniquindio.triage.domain.enums.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;

public class CambiarEstadoRequest {

    @NotNull(message = "El nuevo estado es obligatorio.")
    private EstadoSolicitud nuevoEstado;

    private String observacion;

    @NotNull(message = "El usuarioId es obligatorio.")
    private Long usuarioId;

    // 🔥 NUEVO CAMPO PARA CONCURRENCIA
    @NotNull(message = "La versión es obligatoria.")
    private Long version;

    public CambiarEstadoRequest() {
    }

    public CambiarEstadoRequest(EstadoSolicitud nuevoEstado,
                                String observacion,
                                Long usuarioId,
                                Long version) {
        this.nuevoEstado = nuevoEstado;
        this.observacion = observacion;
        this.usuarioId = usuarioId;
        this.version = version;
    }

    public EstadoSolicitud getNuevoEstado() {
        return nuevoEstado;
    }

    public void setNuevoEstado(EstadoSolicitud nuevoEstado) {
        this.nuevoEstado = nuevoEstado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }


    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}