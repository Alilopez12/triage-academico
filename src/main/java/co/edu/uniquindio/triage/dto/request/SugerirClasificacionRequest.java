package co.edu.uniquindio.triage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SugerirClasificacionRequest {

    @NotBlank(message = "La descripción es obligatoria.")
    @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres.")
    private String descripcion;

    public SugerirClasificacionRequest() {
    }

    public SugerirClasificacionRequest(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
