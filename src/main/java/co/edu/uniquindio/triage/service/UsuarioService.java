package co.edu.uniquindio.triage.service;

import co.edu.uniquindio.triage.domain.entity.UsuarioEntity;
import co.edu.uniquindio.triage.domain.enums.RolUsuario;
import co.edu.uniquindio.triage.dto.request.UsuarioCreateRequest;
import co.edu.uniquindio.triage.dto.response.UsuarioResponse;
import co.edu.uniquindio.triage.exception.AutorizacionException;
import co.edu.uniquindio.triage.exception.ReglaNegocioException;
import co.edu.uniquindio.triage.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarUsuarios(RolUsuario rol, Boolean activo) {
        List<UsuarioEntity> usuarios;

        if (rol != null && activo != null) {
            usuarios = activo
                    ? usuarioRepository.findByRolAndActivoTrue(rol)
                    : usuarioRepository.findByRol(rol).stream()
                        .filter(u -> !u.isActivo()).toList();
        } else if (rol != null) {
            usuarios = usuarioRepository.findByRol(rol);
        } else if (activo != null && activo) {
            usuarios = usuarioRepository.findByActivoTrue();
        } else {
            usuarios = usuarioRepository.findAll();
        }

        return usuarios.stream().map(this::toResponse).toList();
    }

    public UsuarioResponse crearUsuario(UsuarioCreateRequest request, Long adminId) {
        UsuarioEntity admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new AutorizacionException("Usuario no encontrado"));

        if (admin.getRol() != RolUsuario.ADMIN) {
            throw new AutorizacionException("Solo los administradores pueden crear usuarios");
        }

        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ReglaNegocioException("Ya existe un usuario con el email " + request.getEmail());
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());
        usuario.setActivo(request.isActivo());

        return toResponse(usuarioRepository.save(usuario));
    }

    private UsuarioResponse toResponse(UsuarioEntity entity) {
        return new UsuarioResponse(
                entity.getId(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getRol(),
                entity.isActivo()
        );
    }
}
