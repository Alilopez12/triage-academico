package co.edu.uniquindio.triage.controller;

import co.edu.uniquindio.triage.config.security.JwtService;
import co.edu.uniquindio.triage.domain.entity.UsuarioEntity;
import co.edu.uniquindio.triage.domain.enums.RolUsuario;
import co.edu.uniquindio.triage.dto.request.LoginRequest;
import co.edu.uniquindio.triage.dto.request.RegisterRequest;
import co.edu.uniquindio.triage.dto.response.LoginResponse;
import co.edu.uniquindio.triage.dto.response.UsuarioResponse;
import co.edu.uniquindio.triage.exception.RecursoNoEncontradoException;
import co.edu.uniquindio.triage.exception.ReglaNegocioException;
import co.edu.uniquindio.triage.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "auth-controller", description = "Autenticación y gestión de sesión")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna un JWT.")
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UsuarioEntity usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        String token = jwtService.generateToken(
                usuario.getEmail(),
                usuario.getId(),
                usuario.getRol().name()
        );

        return new LoginResponse(token, usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getRol());
    }

    @Operation(summary = "Registrarse", description = "Crea una cuenta nueva como Estudiante o Responsable.")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse register(@Valid @RequestBody RegisterRequest request) {
        if (request.getRol() == RolUsuario.ADMIN) {
            throw new ReglaNegocioException("No es posible registrarse como administrador.");
        }

        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ReglaNegocioException("Ya existe una cuenta con ese correo electrónico.");
        }

        UsuarioEntity nuevo = new UsuarioEntity();
        nuevo.setNombre(request.getNombre());
        nuevo.setEmail(request.getEmail());
        nuevo.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevo.setRol(request.getRol());
        nuevo.setActivo(true);

        UsuarioEntity guardado = usuarioRepository.saveAndFlush(nuevo);

        String token = jwtService.generateToken(guardado.getEmail(), guardado.getId(), guardado.getRol().name());
        return new LoginResponse(token, guardado.getId(), guardado.getNombre(), guardado.getEmail(), guardado.getRol());
    }

    @Operation(summary = "Usuario actual", description = "Retorna los datos del usuario autenticado.")
    @GetMapping("/me")
    public UsuarioResponse me(Authentication authentication) {
        String email = authentication.getName();

        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.isActivo()
        );
    }
}
