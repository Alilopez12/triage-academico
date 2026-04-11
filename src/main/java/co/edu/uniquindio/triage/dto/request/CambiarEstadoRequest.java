package co.edu.uniquindio.triage.dto.request;

import co.edu.uniquindio.triage.domain.enums.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;

public class CambiarEstadoRequest {

    @NotNull(message = "El nuevo estado es obligatorio.")
    private EstadoSolicitud nuevoEstado;

    private String observacion;

    public CambiarEstadoRequest() {
    }

    public CambiarEstadoRequest(EstadoSolicitud nuevoEstado, String observacion) {
        this.nuevoEstado = nuevoEstado;
        this.observacion = observacion;
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
}