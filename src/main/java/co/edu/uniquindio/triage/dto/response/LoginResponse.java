package co.edu.uniquindio.triage.dto.response;

import co.edu.uniquindio.triage.domain.enums.RolUsuario;

public class LoginResponse {

    private String token;
    private Long userId;
    private String nombre;
    private String email;
    private RolUsuario rol;

    public LoginResponse() {}

    public LoginResponse(String token, Long userId, String nombre, String email, RolUsuario rol) {
        this.token = token;
        this.userId = userId;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }
}
