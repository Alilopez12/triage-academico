package co.edu.uniquindio.triage.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "historial_solicitudes",
        indexes = {
                @Index(name = "idx_historial_solicitud", columnList = "solicitud_id"),
                @Index(name = "idx_historial_usuario", columnList = "usuario_responsable_id"),
                @Index(name = "idx_historial_fecha", columnList = "fecha_hora")
        }
)
public class HistorialSolicitudEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "fecha_hora")
    private LocalDateTime fechaHora;

    @Column(nullable = false, length = 100)
    private String accion;

    @Column(length = 500)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_responsable_id", nullable = false)
    private UsuarioEntity usuarioResponsable;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitud_id", nullable = false)
    private SolicitudEntity solicitud;

    public HistorialSolicitudEntity() {
    }

    // Getters
    public Long getId() { return id; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getAccion() { return accion; }
    public String getObservaciones() { return observaciones; }
    public UsuarioEntity getUsuarioResponsable() { return usuarioResponsable; }
    public SolicitudEntity getSolicitud() { return solicitud; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public void setAccion(String accion) { this.accion = accion; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public void setUsuarioResponsable(UsuarioEntity usuarioResponsable) { this.usuarioResponsable = usuarioResponsable; }
    public void setSolicitud(SolicitudEntity solicitud) { this.solicitud = solicitud; }
}