package co.edu.uniquindio.triage.dto.request;

import co.edu.uniquindio.triage.domain.enums.CanalOrigen;
import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SolicitudCreateRequest {

    @NotNull(message = "El tipo de solicitud es obligatorio.")
    private TipoSolicitud tipo;

    @NotBlank(message = "La descripción es obligatoria.")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres.")
    private String descripcion;

    @NotNull(message = "El canal de origen es obligatorio.")
    private CanalOrigen canalOrigen;

    @NotNull(message = "El id del solicitante es obligatorio.")
    private Long solicitanteId;

    public SolicitudCreateRequest() {
    }

    public SolicitudCreateRequest(TipoSolicitud tipo, String descripcion, CanalOrigen canalOrigen, Long solicitanteId) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.canalOrigen = canalOrigen;
        this.solicitanteId = solicitanteId;
    }

    public TipoSolicitud getTipo() {
        return tipo;
    }

    public void setTipo(TipoSolicitud tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public CanalOrigen getCanalOrigen() {
        return canalOrigen;
    }

    public void setCanalOrigen(CanalOrigen canalOrigen) {
        this.canalOrigen = canalOrigen;
    }

    public Long getSolicitanteId() {
        return solicitanteId;
    }

    public void setSolicitanteId(Long solicitanteId) {
        this.solicitanteId = solicitanteId;
    }
}