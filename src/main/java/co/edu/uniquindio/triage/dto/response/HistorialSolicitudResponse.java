package co.edu.uniquindio.triage.dto.response;

import java.time.LocalDateTime;

public class HistorialSolicitudResponse {

    private Long id;
    private LocalDateTime fechaHora;
    private String accion;
    private String observaciones;
    private Long usuarioResponsableId;
    private String nombreUsuarioResponsable;

    public HistorialSolicitudResponse() {
    }

    public HistorialSolicitudResponse(Long id,
                                      LocalDateTime fechaHora,
                                      String accion,
                                      String observaciones,
                                      Long usuarioResponsableId,
                                      String nombreUsuarioResponsable) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.accion = accion;
        this.observaciones = observaciones;
        this.usuarioResponsableId = usuarioResponsableId;
        this.nombreUsuarioResponsable = nombreUsuarioResponsable;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getAccion() {
        return accion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public Long getUsuarioResponsableId() {
        return usuarioResponsableId;
    }

    public String getNombreUsuarioResponsable() {
        return nombreUsuarioResponsable;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public void setUsuarioResponsableId(Long usuarioResponsableId) {
        this.usuarioResponsableId = usuarioResponsableId;
    }

    public void setNombreUsuarioResponsable(String nombreUsuarioResponsable) {
        this.nombreUsuarioResponsable = nombreUsuarioResponsable;
    }
}