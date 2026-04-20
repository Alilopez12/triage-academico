package co.edu.uniquindio.triage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CerrarSolicitudRequest {

    @NotBlank(message = "La observación de cierre es obligatoria.")
    @Size(min = 10, max = 500, message = "La observación de cierre debe tener entre 10 y 500 caracteres.")
    private String observacionCierre;

    @NotNull(message = "La versión es obligatoria.")
    private Long version;

    public CerrarSolicitudRequest() {
    }

    public CerrarSolicitudRequest(String observacionCierre, Long version) {
        this.observacionCierre = observacionCierre;
        this.version = version;
    }

    public String getObservacionCierre() {
        return observacionCierre;
    }

    public void setObservacionCierre(String observacionCierre) {
        this.observacionCierre = observacionCierre;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}