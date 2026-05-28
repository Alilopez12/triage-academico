package co.edu.uniquindio.triage.controller;

import co.edu.uniquindio.triage.config.security.JwtService;
import co.edu.uniquindio.triage.config.security.SecurityConfig;
import co.edu.uniquindio.triage.domain.entity.UsuarioEntity;
import co.edu.uniquindio.triage.domain.enums.RolUsuario;
import co.edu.uniquindio.triage.dto.request.UsuarioCreateRequest;
import co.edu.uniquindio.triage.dto.response.UsuarioResponse;
import co.edu.uniquindio.triage.repository.UsuarioRepository;
import co.edu.uniquindio.triage.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@Import(SecurityConfig.class)
@WithMockUser
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        UsuarioEntity mockAuthUser = new UsuarioEntity();
        mockAuthUser.setId(1L);
        mockAuthUser.setEmail("user");
        when(usuarioRepository.findByEmail("user")).thenReturn(Optional.of(mockAuthUser));
    }

    @Test
    @DisplayName("GET /api/usuarios debería retornar 200 con lista de usuarios")
    void deberiaListarUsuariosYRetornar200() throws Exception {
        UsuarioResponse u1 = new UsuarioResponse(1L, "Admin", "admin@uq.edu.co", RolUsuario.ADMIN, true);
        UsuarioResponse u2 = new UsuarioResponse(2L, "Estudiante", "est@uq.edu.co", RolUsuario.ESTUDIANTE, true);

        when(usuarioService.listarUsuarios(null, null)).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].rol").value("ADMIN"))
                .andExpect(jsonPath("$[1].rol").value("ESTUDIANTE"));
    }

    @Test
    @DisplayName("GET /api/usuarios?rol=RESPONSABLE debería filtrar por rol")
    void deberiaFiltrarUsuariosPorRol() throws Exception {
        UsuarioResponse responsable = new UsuarioResponse(3L, "Responsable", "resp@uq.edu.co", RolUsuario.RESPONSABLE, true);

        when(usuarioService.listarUsuarios(eq(RolUsuario.RESPONSABLE), eq(null))).thenReturn(List.of(responsable));

        mockMvc.perform(get("/api/usuarios").param("rol", "RESPONSABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].rol").value("RESPONSABLE"));
    }

    @Test
    @DisplayName("POST /api/usuarios debería retornar 201 al crear usuario")
    void deberiaCrearUsuarioYRetornar201() throws Exception {
        UsuarioCreateRequest request = new UsuarioCreateRequest(
                "Nuevo Responsable", "nuevo@uq.edu.co", "password123", RolUsuario.RESPONSABLE, true
        );

        UsuarioResponse response = new UsuarioResponse(10L, "Nuevo Responsable", "nuevo@uq.edu.co", RolUsuario.RESPONSABLE, true);

        when(usuarioService.crearUsuario(any(UsuarioCreateRequest.class), anyLong())).thenReturn(response);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.nombre").value("Nuevo Responsable"))
                .andExpect(jsonPath("$.rol").value("RESPONSABLE"));
    }

    @Test
    @DisplayName("POST /api/usuarios debería retornar 400 si el request es inválido")
    void deberiaRetornar400SiRequestEsInvalido() throws Exception {
        UsuarioCreateRequest request = new UsuarioCreateRequest(
                "", "no-es-email", "123", null, true
        );

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
