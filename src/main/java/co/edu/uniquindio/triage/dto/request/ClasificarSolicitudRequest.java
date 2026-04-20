package co.edu.uniquindio.triage.dto.request;

import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;
import jakarta.validation.constraints.NotNull;

public class ClasificarSolicitudRequest {

    @NotNull(message = "El tipo de solicitud es obligatorio.")
    private TipoSolicitud tipo;

    @NotNull(message = "La versión es obligatoria.")
    private Long version;

    public ClasificarSolicitudRequest() {
    }

    public ClasificarSolicitudRequest(TipoSolicitud tipo, Long version) {
        this.tipo = tipo;
        this.version = version;
    }

    public TipoSolicitud getTipo() {
        return tipo;
    }

    public void setTipo(TipoSolicitud tipo) {
        this.tipo = tipo;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}