package co.edu.uniquindio.triage.controller;

import co.edu.uniquindio.triage.config.security.JwtService;
import co.edu.uniquindio.triage.config.security.SecurityConfig;
import co.edu.uniquindio.triage.domain.enums.Prioridad;
import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.triage.dto.request.SugerirClasificacionRequest;
import co.edu.uniquindio.triage.dto.response.SugerenciaClasificacionResponse;
import co.edu.uniquindio.triage.service.IAService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IAController.class)
@Import(SecurityConfig.class)
@WithMockUser
class IAControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IAService iaService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("POST /api/ia/sugerir-clasificacion debería retornar 200")
    void deberiaSugerirClasificacionYRetornar200() throws Exception {

        SugerirClasificacionRequest request =
                new SugerirClasificacionRequest(
                        "Necesito cancelar una asignatura por cruce de horarios"
                );

        SugerenciaClasificacionResponse response =
                new SugerenciaClasificacionResponse();

        response.setTipoSugerido(TipoSolicitud.CANCELACION_ASIGNATURAS);
        response.setPrioridadSugerida(Prioridad.MEDIA);
        response.setJustificacion("Se detectaron palabras clave relacionadas con cancelación.");

        when(iaService.sugerirClasificacion(anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/api/ia/sugerir-clasificacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoSugerido").value("CANCELACION_ASIGNATURAS"))
                .andExpect(jsonPath("$.prioridadSugerida").value("MEDIA"))
                .andExpect(jsonPath("$.justificacion").value("Se detectaron palabras clave relacionadas con cancelación."));
    }

    @Test
    @DisplayName("POST /api/ia/sugerir-clasificacion debería retornar 400 si la descripción es inválida")
    void deberiaRetornar400SiDescripcionEsInvalida() throws Exception {

        SugerirClasificacionRequest request =
                new SugerirClasificacionRequest("");

        mockMvc.perform(post("/api/ia/sugerir-clasificacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/ia/solicitudes/{id}/resumen debería retornar 200")
    void deberiaGenerarResumenYRetornar200() throws Exception {

        when(iaService.generarResumenSolicitud(50L))
                .thenReturn("Resumen automático de la solicitud");

        mockMvc.perform(get("/api/ia/solicitudes/50/resumen"))
                .andExpect(status().isOk())
                .andExpect(content().string("Resumen automático de la solicitud"));
    }
}
