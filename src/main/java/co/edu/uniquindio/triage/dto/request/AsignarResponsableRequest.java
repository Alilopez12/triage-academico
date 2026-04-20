package co.edu.uniquindio.triage.dto.request;

import jakarta.validation.constraints.NotNull;

public class AsignarResponsableRequest {

    @NotNull(message = "El id del responsable es obligatorio")
    private Long responsableId;

    @NotNull(message = "La versión es obligatoria.")
    private Long version;

    public AsignarResponsableRequest() {
    }

    public AsignarResponsableRequest(Long responsableId, Long version) {
        this.responsableId = responsableId;
        this.version = version;
    }

    public Long getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(Long responsableId) {
        this.responsableId = responsableId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}