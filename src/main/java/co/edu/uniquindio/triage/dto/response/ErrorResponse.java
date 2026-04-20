package co.edu.uniquindio.triage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Estructura estándar de errores del sistema")
public class ErrorResponse {

    @Schema(description = "Fecha y hora del error", example = "2026-04-11T15:10:18")
    private LocalDateTime timestamp;

    @Schema(description = "Código HTTP del error", example = "404")
    private int status;

    @Schema(description = "Tipo de error HTTP", example = "Not Found")
    private String error;

    @Schema(description = "Mensaje descriptivo del error", example = "No existe una solicitud con id 999")
    private String message;

    @Schema(description = "Ruta del endpoint donde ocurrió el error", example = "/api/solicitudes/999")
    private String path;

    @Schema(description = "Detalles adicionales del error (validaciones, etc.)")
    private Map<String, String> details;

    public ErrorResponse() {
    }

    public ErrorResponse(LocalDateTime timestamp,
                         int status,
                         String error,
                         String message,
                         String path,
                         Map<String, String> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }
}