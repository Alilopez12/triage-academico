package co.edu.uniquindio.triage.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_solicitudes")
public class HistorialSolicitudEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, length = 100)
    private String accion;

    @Column(length = 500)
    private String observaciones;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_responsable_id", nullable = false)
    private UsuarioEntity usuarioResponsable;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id", nullable = false)
    private SolicitudEntity solicitud;

    public HistorialSolicitudEntity() {
    }

    public HistorialSolicitudEntity(Long id,
                                    LocalDateTime fechaHora,
                                    String accion,
                                    String observaciones,
                                    UsuarioEntity usuarioResponsable,
                                    SolicitudEntity solicitud) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.accion = accion;
        this.observaciones = observaciones;
        this.usuarioResponsable = usuarioResponsable;
        this.solicitud = solicitud;
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

    public UsuarioEntity getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public SolicitudEntity getSolicitud() {
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

    public void setUsuarioResponsable(UsuarioEntity usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
    }

    public void setSolicitud(SolicitudEntity solicitud) {
        this.solicitud = solicitud;
    }
}