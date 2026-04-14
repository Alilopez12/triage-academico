package co.edu.uniquindio.triage.dto.request;

import jakarta.validation.constraints.NotNull;

public class AsignarResponsableRequest {

    @NotNull(message = "El id del responsable es obligatorio")
    private Long responsableId;

    public AsignarResponsableRequest() {
    }

    public AsignarResponsableRequest(Long responsableId) {
        this.responsableId = responsableId;
    }

    public Long getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(Long responsableId) {
        this.responsableId = responsableId;
    }
}
