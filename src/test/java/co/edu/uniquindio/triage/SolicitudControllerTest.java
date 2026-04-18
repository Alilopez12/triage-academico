package co.edu.uniquindio.triage;

import co.edu.uniquindio.triage.controller.SolicitudController;
import co.edu.uniquindio.triage.domain.enums.CanalOrigen;
import co.edu.uniquindio.triage.domain.enums.EstadoSolicitud;
import co.edu.uniquindio.triage.domain.enums.ImpactoAcademico;
import co.edu.uniquindio.triage.domain.enums.Prioridad;
import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.triage.dto.request.AsignarPrioridadRequest;
import co.edu.uniquindio.triage.dto.request.AsignarResponsableRequest;
import co.edu.uniquindio.triage.dto.request.CambiarEstadoRequest;
import co.edu.uniquindio.triage.dto.request.ClasificarSolicitudRequest;
import co.edu.uniquindio.triage.dto.request.CerrarSolicitudRequest;
import co.edu.uniquindio.triage.dto.request.SolicitudCreateRequest;
import co.edu.uniquindio.triage.dto.request.SugerirClasificacionRequest;
import co.edu.uniquindio.triage.dto.response.HistorialSolicitudResponse;
import co.edu.uniquindio.triage.dto.response.SolicitudResponse;
import co.edu.uniquindio.triage.dto.response.SugerenciaClasificacionResponse;
import co.edu.uniquindio.triage.service.impl.IAService;
import co.edu.uniquindio.triage.service.impl.SolicitudService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SolicitudController.class)
class SolicitudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SolicitudService solicitudService;

    @MockBean
    private IAService iaService;

    @Test
    @DisplayName("POST /api/solicitudes debería registrar y retornar 201")
    void deberiaRegistrarSolicitudYRetornar201() throws Exception {
        SolicitudCreateRequest request = new SolicitudCreateRequest(
                TipoSolicitud.HOMOLOGACION,
                "Solicito homologación de una materia cursada previamente.",
                CanalOrigen.SAC,
                10L
        );

        SolicitudResponse response = crearSolicitudResponseBase();
        response.setId(100L);
        response.setTipo(TipoSolicitud.HOMOLOGACION);
        response.setEstado(EstadoSolicitud.REGISTRADA);

        when(solicitudService.registrarSolicitud(any(SolicitudCreateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.tipo").value("HOMOLOGACION"))
                .andExpect(jsonPath("$.estado").value("REGISTRADA"));
    }

    @Test
    @DisplayName("POST /api/solicitudes debería retornar 400 si el request es inválido")
    void deberiaRetornar400SiSolicitudCreateRequestEsInvalido() throws Exception {
        SolicitudCreateRequest request = new SolicitudCreateRequest(
                null,
                "",
                null,
                null
        );

        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/solicitudes/{id}/clasificar debería retornar 200")
    void deberiaClasificarSolicitudYRetornar200() throws Exception {
        ClasificarSolicitudRequest request = new ClasificarSolicitudRequest(TipoSolicitud.CANCELACION_ASIGNATURAS);

        SolicitudResponse response = crearSolicitudResponseBase();
        response.setId(50L);
        response.setTipo(TipoSolicitud.CANCELACION_ASIGNATURAS);
        response.setEstado(EstadoSolicitud.CLASIFICADA);

        when(solicitudService.clasificarSolicitud(eq(50L), eq(1L), any(ClasificarSolicitudRequest.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/solicitudes/50/clasificar")
                        .param("usuarioId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("CANCELACION_ASIGNATURAS"))
                .andExpect(jsonPath("$.estado").value("CLASIFICADA"));
    }

    @Test
    @DisplayName("PATCH /api/solicitudes/{id}/clasificar debería retornar 400 si falta tipo")
    void deberiaRetornar400SiClasificarRequestEsInvalido() throws Exception {
        ClasificarSolicitudRequest request = new ClasificarSolicitudRequest();
        request.setTipo(null);

        mockMvc.perform(patch("/api/solicitudes/50/clasificar")
                        .param("usuarioId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/solicitudes/{id}/prioridad debería retornar 200")
    void deberiaAsignarPrioridadYRetornar200() throws Exception {
        AsignarPrioridadRequest request = new AsignarPrioridadRequest();
        request.setImpactoAcademico(ImpactoAcademico.ALTO);
        request.setFechaLimite(LocalDate.now().plusDays(2));

        SolicitudResponse response = crearSolicitudResponseBase();
        response.setPrioridad(Prioridad.CRITICA);
        response.setEstado(EstadoSolicitud.CLASIFICADA);

        when(solicitudService.asignarPrioridad(eq(50L), any(AsignarPrioridadRequest.class), eq(1L)))
                .thenReturn(response);

        mockMvc.perform(put("/api/solicitudes/50/prioridad")
                        .param("usuarioId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prioridad").value("CRITICA"));
    }

    @Test
    @DisplayName("PATCH /api/solicitudes/{id}/asignar debería retornar 200")
    void deberiaAsignarResponsableYRetornar200() throws Exception {
        AsignarResponsableRequest request = new AsignarResponsableRequest();
        request.setResponsableId(2L);

        SolicitudResponse response = crearSolicitudResponseBase();
        response.setResponsableAsignadoId(2L);

        when(solicitudService.asignarResponsable(eq(50L), any(AsignarResponsableRequest.class), eq(1L)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/solicitudes/50/asignar")
                        .param("usuarioId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responsableAsignadoId").value(2L));
    }

    @Test
    @DisplayName("GET /api/solicitudes/{id}/historial debería retornar 200")
    void deberiaObtenerHistorialYRetornar200() throws Exception {
        HistorialSolicitudResponse item = new HistorialSolicitudResponse();
        item.setAccion("REGISTRO");
        item.setObservaciones("Solicitud registrada");
        item.setUsuarioResponsableId(10L);
        item.setFechaHora(LocalDateTime.now());

        when(solicitudService.obtenerHistorial(50L)).thenReturn(List.of(item));

        mockMvc.perform(get("/api/solicitudes/50/historial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accion").value("REGISTRO"));
    }

    @Test
    @DisplayName("PATCH /api/solicitudes/{id}/estado debería retornar 200")
    void deberiaCambiarEstadoYRetornar200() throws Exception {
        CambiarEstadoRequest request = new CambiarEstadoRequest();
        request.setNuevoEstado(EstadoSolicitud.EN_ATENCION);
        request.setObservacion("Se inicia la atención del caso");
        request.setUsuarioId(1L);

        SolicitudResponse response = crearSolicitudResponseBase();
        response.setEstado(EstadoSolicitud.EN_ATENCION);

        when(solicitudService.cambiarEstado(eq(50L), any(CambiarEstadoRequest.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/solicitudes/50/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_ATENCION"));
    }

    @Test
    @DisplayName("PUT /api/solicitudes/{id}/cerrar debería retornar 200")
    void deberiaCerrarSolicitudYRetornar200() throws Exception {
        CerrarSolicitudRequest request = new CerrarSolicitudRequest();
        request.setObservacionCierre("La solicitud fue atendida y cerrada correctamente.");

        SolicitudResponse response = crearSolicitudResponseBase();
        response.setEstado(EstadoSolicitud.CERRADA);
        response.setObservacionCierre("La solicitud fue atendida y cerrada correctamente.");

        when(solicitudService.cerrarSolicitud(eq(50L), any(CerrarSolicitudRequest.class), eq(1L)))
                .thenReturn(response);

        mockMvc.perform(put("/api/solicitudes/50/cerrar")
                        .param("usuarioId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CERRADA"))
                .andExpect(jsonPath("$.observacionCierre")
                        .value("La solicitud fue atendida y cerrada correctamente."));
    }

    @Test
    @DisplayName("GET /api/solicitudes/{id}/resumen debería retornar 200")
    void deberiaGenerarResumenYRetornar200() throws Exception {
        when(solicitudService.generarResumenSolicitud(50L))
                .thenReturn("Resumen automático de la solicitud");

        mockMvc.perform(get("/api/solicitudes/50/resumen"))
                .andExpect(status().isOk())
                .andExpect(content().string("Resumen automático de la solicitud"));
    }

    @Test
    @DisplayName("GET /api/solicitudes debería listar con filtros y retornar 200")
    void deberiaListarSolicitudesYRetornar200() throws Exception {
        SolicitudResponse response = crearSolicitudResponseBase();
        response.setId(50L);
        response.setEstado(EstadoSolicitud.REGISTRADA);

        when(solicitudService.listarSolicitudes(
                EstadoSolicitud.REGISTRADA,
                TipoSolicitud.HOMOLOGACION,
                Prioridad.ALTA,
                2L
        )).thenReturn(List.of(response));

        mockMvc.perform(get("/api/solicitudes")
                        .param("estado", "REGISTRADA")
                        .param("tipo", "HOMOLOGACION")
                        .param("prioridad", "ALTA")
                        .param("responsableId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(50L))
                .andExpect(jsonPath("$[0].estado").value("REGISTRADA"));
    }

    @Test
    @DisplayName("GET /api/solicitudes/{id} debería retornar 200")
    void deberiaObtenerSolicitudPorIdYRetornar200() throws Exception {
        SolicitudResponse response = crearSolicitudResponseBase();
        response.setId(50L);

        when(solicitudService.obtenerSolicitudPorId(50L)).thenReturn(response);

        mockMvc.perform(get("/api/solicitudes/50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(50L));
    }

    @Test
    @DisplayName("POST /api/solicitudes/sugerir-clasificacion debería retornar 200")
    void deberiaSugerirClasificacionYRetornar200() throws Exception {
        SugerirClasificacionRequest request = new SugerirClasificacionRequest(
                "Necesito cancelar una materia urgente porque vence mañana."
        );

        SugerenciaClasificacionResponse response = new SugerenciaClasificacionResponse(
                TipoSolicitud.CANCELACION_ASIGNATURAS,
                Prioridad.CRITICA,
                "Sugerencia generada automáticamente."
        );

        when(iaService.sugerirClasificacion(any(String.class))).thenReturn(response);

        mockMvc.perform(post("/api/solicitudes/sugerir-clasificacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoSugerido").value("CANCELACION_ASIGNATURAS"))
                .andExpect(jsonPath("$.prioridadSugerida").value("CRITICA"));
    }

    @Test
    @DisplayName("POST /api/solicitudes/sugerir-clasificacion debería retornar 400 si la descripción es inválida")
    void deberiaRetornar400SiSugerirClasificacionRequestEsInvalido() throws Exception {
        SugerirClasificacionRequest request = new SugerirClasificacionRequest("");
        mockMvc.perform(post("/api/solicitudes/sugerir-clasificacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private SolicitudResponse crearSolicitudResponseBase() {
        SolicitudResponse response = new SolicitudResponse();
        response.setId(50L);
        response.setTipo(TipoSolicitud.HOMOLOGACION);
        response.setDescripcion("Solicitud de prueba");
        response.setCanalOrigen(CanalOrigen.SAC);
        response.setFechaRegistro(LocalDateTime.now());
        response.setSolicitanteId(10L);
        response.setResponsableAsignadoId(null);
        response.setEstado(EstadoSolicitud.REGISTRADA);
        response.setPrioridad(null);
        response.setJustificacionPrioridad(null);
        response.setImpactoAcademico(null);
        response.setFechaLimite(null);
        response.setObservacionCierre(null);
        response.setHistorial(List.of());
        return response;
    }
}