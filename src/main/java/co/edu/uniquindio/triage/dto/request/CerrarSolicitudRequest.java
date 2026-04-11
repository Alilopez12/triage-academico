package co.edu.uniquindio.triage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CerrarSolicitudRequest {

    @NotBlank(message = "La observación de cierre es obligatoria.")
    @Size(min = 10, max = 500, message = "La observación de cierre debe tener entre 10 y 500 caracteres.")
    private String observacionCierre;

    public CerrarSolicitudRequest() {
    }

    public CerrarSolicitudRequest(String observacionCierre) {
        this.observacionCierre = observacionCierre;
    }

    public String getObservacionCierre() {
        return observacionCierre;
    }

    public void setObservacionCierre(String observacionCierre) {
        this.observacionCierre = observacionCierre;
    }
}