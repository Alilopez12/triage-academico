package co.edu.uniquindio.triage.domain.entity;

import co.edu.uniquindio.triage.domain.enums.*;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "solicitudes",
        indexes = {
                @Index(name = "idx_solicitud_estado", columnList = "estado"),
                @Index(name = "idx_solicitud_tipo", columnList = "tipo"),
                @Index(name = "idx_solicitud_prioridad", columnList = "prioridad"),
                @Index(name = "idx_solicitud_responsable", columnList = "responsable_asignado_id"),
                @Index(name = "idx_solicitud_solicitante", columnList = "solicitante_id"),
                @Index(name = "idx_solicitud_estado_tipo", columnList = "estado,tipo"),
                @Index(name = "idx_solicitud_fecha_registro", columnList = "fecha_registro")
        }
)
public class SolicitudEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoSolicitud tipo;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CanalOrigen canalOrigen;

    @Column(nullable = false, name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Prioridad prioridad;

    @Column(name = "justificacion_prioridad", length = 500)
    private String justificacionPrioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoSolicitud estado;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ImpactoAcademico impactoAcademico;

    @Column(name = "fecha_limite")
    private LocalDate fechaLimite;

    @Column(name = "observacion_cierre", length = 500)
    private String observacionCierre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private UsuarioEntity solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_asignado_id")
    private UsuarioEntity responsableAsignado;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialSolicitudEntity> historial = new ArrayList<>();

    public SolicitudEntity() {
    }

    // Métodos helper
    public void agregarHistorial(HistorialSolicitudEntity item) {
        historial.add(item);
        item.setSolicitud(this);
    }

    public void removerHistorial(HistorialSolicitudEntity item) {
        historial.remove(item);
        item.setSolicitud(null);
    }

    // Getters y Setters

    public Long getId() { return id; }
    public Long getVersion() { return version; }
    public TipoSolicitud getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public CanalOrigen getCanalOrigen() { return canalOrigen; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public Prioridad getPrioridad() { return prioridad; }
    public String getJustificacionPrioridad() { return justificacionPrioridad; }
    public EstadoSolicitud getEstado() { return estado; }
    public ImpactoAcademico getImpactoAcademico() { return impactoAcademico; }
    public LocalDate getFechaLimite() { return fechaLimite; }
    public String getObservacionCierre() { return observacionCierre; }
    public UsuarioEntity getSolicitante() { return solicitante; }
    public UsuarioEntity getResponsableAsignado() { return responsableAsignado; }
    public List<HistorialSolicitudEntity> getHistorial() { return historial; }

    public void setId(Long id) { this.id = id; }
    public void setVersion(Long version) { this.version = version; }
    public void setTipo(TipoSolicitud tipo) { this.tipo = tipo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setCanalOrigen(CanalOrigen canalOrigen) { this.canalOrigen = canalOrigen; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }
    public void setJustificacionPrioridad(String justificacionPrioridad) { this.justificacionPrioridad = justificacionPrioridad; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
    public void setImpactoAcademico(ImpactoAcademico impactoAcademico) { this.impactoAcademico = impactoAcademico; }
    public void setFechaLimite(LocalDate fechaLimite) { this.fechaLimite = fechaLimite; }
    public void setObservacionCierre(String observacionCierre) { this.observacionCierre = observacionCierre; }
    public void setSolicitante(UsuarioEntity solicitante) { this.solicitante = solicitante; }
    public void setResponsableAsignado(UsuarioEntity responsableAsignado) { this.responsableAsignado = responsableAsignado; }
    public void setHistorial(List<HistorialSolicitudEntity> historial) { this.historial = historial; }
}