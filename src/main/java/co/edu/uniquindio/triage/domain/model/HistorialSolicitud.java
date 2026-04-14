package co.edu.uniquindio.triage.domain.model;

import co.edu.uniquindio.triage.exception.ReglaNegocioException;

import java.time.LocalDateTime;

public class HistorialSolicitud {

    private Long id;
    private LocalDateTime fechaHora;
    private String accion;
    private String observaciones;
    private Usuario usuarioResponsable;
    private Solicitud solicitud;

    public HistorialSolicitud() {
    }

    public HistorialSolicitud(Long id,
                              LocalDateTime fechaHora,
                              String accion,
                              String observaciones,
                              Usuario usuarioResponsable,
                              Solicitud solicitud) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.accion = accion;
        this.observaciones = observaciones;
        this.usuarioResponsable = usuarioResponsable;
        this.solicitud = solicitud;
    }

    public static HistorialSolicitud crearRegistroInicial(Solicitud solicitud, Usuario usuarioResponsable) {
        if (solicitud == null) {
            throw new ReglaNegocioException("La solicitud es obligatoria para crear el historial inicial.");
        }

        if (usuarioResponsable == null) {
            throw new ReglaNegocioException("El usuario responsable es obligatorio para crear el historial inicial.");
        }

        HistorialSolicitud historial = new HistorialSolicitud();
        historial.setFechaHora(LocalDateTime.now());
        historial.setAccion("REGISTRO_SOLICITUD");
        historial.setObservaciones("La solicitud fue registrada correctamente.");
        historial.setUsuarioResponsable(usuarioResponsable);
        historial.setSolicitud(solicitud);

        return historial;
    }

    public static HistorialSolicitud crear(String accion,
                                           String observaciones,
                                           Usuario usuarioResponsable,
                                           Solicitud solicitud) {
        if (accion == null || accion.isBlank()) {
            throw new ReglaNegocioException("La acción del historial es obligatoria.");
        }

        if (usuarioResponsable == null) {
            throw new ReglaNegocioException("El usuario responsable del historial es obligatorio.");
        }

        if (solicitud == null) {
            throw new ReglaNegocioException("La solicitud asociada al historial es obligatoria.");
        }

        HistorialSolicitud historial = new HistorialSolicitud();
        historial.setFechaHora(LocalDateTime.now());
        historial.setAccion(accion.trim());
        historial.setObservaciones(observaciones != null ? observaciones.trim() : null);
        historial.setUsuarioResponsable(usuarioResponsable);
        historial.setSolicitud(solicitud);

        return historial;
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

    public Usuario getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public Solicitud getSolicitud() {
        return solicitud;
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

    public void setUsuarioResponsable(Usuario usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
    }

    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }
}