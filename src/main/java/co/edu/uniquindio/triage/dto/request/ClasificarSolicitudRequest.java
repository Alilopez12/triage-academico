package co.edu.uniquindio.triage.dto.request;

import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;
import jakarta.validation.constraints.NotNull;

public class ClasificarSolicitudRequest {

    @NotNull(message = "El tipo de solicitud es obligatorio.")
    private TipoSolicitud tipo;

    public ClasificarSolicitudRequest() {
    }

    public ClasificarSolicitudRequest(TipoSolicitud tipo) {
        this.tipo = tipo;
    }

    public TipoSolicitud getTipo() {
        return tipo;
    }

    public void setTipo(TipoSolicitud tipo) {
        this.tipo = tipo;
    }
}
