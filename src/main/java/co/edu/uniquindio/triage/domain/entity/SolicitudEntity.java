package co.edu.uniquindio.triage.domain.entity;

import co.edu.uniquindio.triage.domain.enums.CanalOrigen;
import co.edu.uniquindio.triage.domain.enums.EstadoSolicitud;
import co.edu.uniquindio.triage.domain.enums.ImpactoAcademico;
import co.edu.uniquindio.triage.domain.enums.Prioridad;
import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "solicitudes")
public class SolicitudEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoSolicitud tipo;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CanalOrigen canalOrigen;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Prioridad prioridad;

    @Column(length = 500)
    private String justificacionPrioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoSolicitud estado;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ImpactoAcademico impactoAcademico;

    @Column(length = 500)
    private String observacionCierre;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private UsuarioEntity solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_asignado_id")
    private UsuarioEntity responsableAsignado;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialSolicitudEntity> historial = new ArrayList<>();

    public SolicitudEntity() {
    }

    public SolicitudEntity(Long id,
                           TipoSolicitud tipo,
                           String descripcion,
                           CanalOrigen canalOrigen,
                           LocalDateTime fechaRegistro,
                           Prioridad prioridad,
                           String justificacionPrioridad,
                           EstadoSolicitud estado,
                           ImpactoAcademico impactoAcademico,
                           String observacionCierre,
                           UsuarioEntity solicitante,
                           UsuarioEntity responsableAsignado) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.canalOrigen = canalOrigen;
        this.fechaRegistro = fechaRegistro;
        this.prioridad = prioridad;
        this.justificacionPrioridad = justificacionPrioridad;
        this.estado = estado;
        this.impactoAcademico = impactoAcademico;
        this.observacionCierre = observacionCierre;
        this.solicitante = solicitante;
        this.responsableAsignado = responsableAsignado;
    }

    public void agregarHistorial(HistorialSolicitudEntity historialItem) {
        historial.add(historialItem);
        historialItem.setSolicitud(this);
    }

    public void removerHistorial(HistorialSolicitudEntity historialItem) {
        historial.remove(historialItem);
        historialItem.setSolicitud(null);
    }

    public Long getId() {
        return id;
    }

    public TipoSolicitud getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public CanalOrigen getCanalOrigen() {
        return canalOrigen;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public String getJustificacionPrioridad() {
        return justificacionPrioridad;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public ImpactoAcademico getImpactoAcademico() {
        return impactoAcademico;
    }

    public String getObservacionCierre() {
        return observacionCierre;
    }

    public UsuarioEntity getSolicitante() {
        return solicitante;
    }

    public UsuarioEntity getResponsableAsignado() {
        return responsableAsignado;
    }

    public List<HistorialSolicitudEntity> getHistorial() {
        return historial;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTipo(TipoSolicitud tipo) {
        this.tipo = tipo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setCanalOrigen(CanalOrigen canalOrigen) {
        this.canalOrigen = canalOrigen;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public void setJustificacionPrioridad(String justificacionPrioridad) {
        this.justificacionPrioridad = justificacionPrioridad;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public void setImpactoAcademico(ImpactoAcademico impactoAcademico) {
        this.impactoAcademico = impactoAcademico;
    }

    public void setObservacionCierre(String observacionCierre) {
        this.observacionCierre = observacionCierre;
    }

    public void setSolicitante(UsuarioEntity solicitante) {
        this.solicitante = solicitante;
    }

    public void setResponsableAsignado(UsuarioEntity responsableAsignado) {
        this.responsableAsignado = responsableAsignado;
    }

    public void setHistorial(List<HistorialSolicitudEntity> historial) {
        this.historial = historial;
    }
}