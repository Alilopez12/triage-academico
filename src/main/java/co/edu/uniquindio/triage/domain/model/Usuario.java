package co.edu.uniquindio.triage.domain.model;

import co.edu.uniquindio.triage.domain.enums.RolUsuario;

public class Usuario {

    private Long id;
    private String nombre;
    private String email;
    private boolean activo;
    private RolUsuario rol;

    public Usuario() {
    }

    public Usuario(Long id, String nombre, String email, boolean activo, RolUsuario rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.activo = activo;
        this.rol = rol;
    }

    public boolean estaActivo() {
        return activo;
    }

    public boolean tieneRol(RolUsuario rol) {
        return this.rol == rol;
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
}