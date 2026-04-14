package co.edu.uniquindio.triage.domain.entity;

import co.edu.uniquindio.triage.domain.enums.RolUsuario;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false)
    private boolean activo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RolUsuario rol;

    @OneToMany(mappedBy = "solicitante")
    private List<SolicitudEntity> solicitudesRegistradas = new ArrayList<>();

    @OneToMany(mappedBy = "responsableAsignado")
    private List<SolicitudEntity> solicitudesAsignadas = new ArrayList<>();

    @OneToMany(mappedBy = "usuarioResponsable")
    private List<HistorialSolicitudEntity> historialAcciones = new ArrayList<>();

    public UsuarioEntity() {
    }

    public UsuarioEntity(Long id, String nombre, String email, boolean activo, RolUsuario rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.activo = activo;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActivo() {
        return activo;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public List<SolicitudEntity> getSolicitudesRegistradas() {
        return solicitudesRegistradas;
    }

    public List<SolicitudEntity> getSolicitudesAsignadas() {
        return solicitudesAsignadas;
    }

    public List<HistorialSolicitudEntity> getHistorialAcciones() {
        return historialAcciones;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public void setSolicitudesRegistradas(List<SolicitudEntity> solicitudesRegistradas) {
        this.solicitudesRegistradas = solicitudesRegistradas;
    }

    public void setSolicitudesAsignadas(List<SolicitudEntity> solicitudesAsignadas) {
        this.solicitudesAsignadas = solicitudesAsignadas;
    }

    public void setHistorialAcciones(List<HistorialSolicitudEntity> historialAcciones) {
        this.historialAcciones = historialAcciones;
    }
}