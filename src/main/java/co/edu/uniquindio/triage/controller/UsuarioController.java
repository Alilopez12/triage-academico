package co.edu.uniquindio.triage.controller;

import co.edu.uniquindio.triage.domain.enums.RolUsuario;
import co.edu.uniquindio.triage.dto.request.UsuarioCreateRequest;
import co.edu.uniquindio.triage.dto.response.UsuarioResponse;
import co.edu.uniquindio.triage.exception.RecursoNoEncontradoException;
import co.edu.uniquindio.triage.repository.UsuarioRepository;
import co.edu.uniquindio.triage.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "usuario-controller", description = "Gestión de usuarios del sistema")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    private Long resolverUsuarioId(Authentication authentication) {
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario autenticado no encontrado"))
                .getId();
    }

    @Operation(summary = "Listar usuarios", description = "Retorna todos los usuarios, con filtros opcionales por rol y estado.")
    @GetMapping
    public List<UsuarioResponse> listarUsuarios(
            @RequestParam(required = false) RolUsuario rol,
            @RequestParam(required = false) Boolean activo) {
        return usuarioService.listarUsuarios(rol, activo);
    }

    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario en el sistema. Solo accesible por administradores.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crearUsuario(@Valid @RequestBody UsuarioCreateRequest request,
                                        Authentication authentication) {
        return usuarioService.crearUsuario(request, resolverUsuarioId(authentication));
    }
}
