package co.edu.uniquindio.triage.dto.request;

import co.edu.uniquindio.triage.domain.enums.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;

public class CambiarEstadoRequest {

    @NotNull(message = "El nuevo estado es obligatorio.")
    private EstadoSolicitud nuevoEstado;

    private String observacion;

    @NotNull(message = "El usuarioId es obligatorio.")
    private Long usuarioId;

    public CambiarEstadoRequest() {
    }

    public CambiarEstadoRequest(EstadoSolicitud nuevoEstado, String observacion, Long usuarioId) {
        this.nuevoEstado = nuevoEstado;
        this.observacion = observacion;
        this.usuarioId = usuarioId;
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
}