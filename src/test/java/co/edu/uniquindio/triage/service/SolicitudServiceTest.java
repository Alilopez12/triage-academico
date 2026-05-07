package co.edu.uniquindio.triage.service;

import co.edu.uniquindio.triage.domain.entity.HistorialSolicitudEntity;
import co.edu.uniquindio.triage.domain.entity.SolicitudEntity;
import co.edu.uniquindio.triage.domain.entity.UsuarioEntity;
import co.edu.uniquindio.triage.domain.enums.*;
import co.edu.uniquindio.triage.dto.request.*;
import co.edu.uniquindio.triage.dto.response.PageResponse;
import co.edu.uniquindio.triage.dto.response.SolicitudResponse;
import co.edu.uniquindio.triage.exception.AutorizacionException;
import co.edu.uniquindio.triage.exception.ConcurrenciaException;
import co.edu.uniquindio.triage.repository.HistorialSolicitudRepository;
import co.edu.uniquindio.triage.repository.SolicitudRepository;
import co.edu.uniquindio.triage.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SolicitudServiceTest {

    private SolicitudRepository solicitudRepository;
    private UsuarioRepository usuarioRepository;
    private HistorialSolicitudRepository historialSolicitudRepository;
    private IAService iaService;
    private SolicitudService solicitudService;

    private UsuarioEntity admin;
    private UsuarioEntity estudiante;
    private UsuarioEntity responsable;
    private SolicitudEntity solicitudEntity;

    @BeforeEach
    void setUp() {
        solicitudRepository = mock(SolicitudRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        historialSolicitudRepository = mock(HistorialSolicitudRepository.class);
        iaService = mock(IAService.class);

        solicitudService = new SolicitudService(
                solicitudRepository,
                usuarioRepository,
                historialSolicitudRepository,
                iaService
        );

        admin = new UsuarioEntity();
        admin.setId(1L);
        admin.setNombre("Administrador");
        admin.setEmail("admin@uq.edu.co");
        admin.setRol(RolUsuario.ADMIN);
        admin.setActivo(true);

        estudiante = new UsuarioEntity();
        estudiante.setId(10L);
        estudiante.setNombre("Estudiante");
        estudiante.setEmail("estudiante@uq.edu.co");
        estudiante.setRol(RolUsuario.ESTUDIANTE);
        estudiante.setActivo(true);

        responsable = new UsuarioEntity();
        responsable.setId(2L);
        responsable.setNombre("Responsable");
        responsable.setEmail("responsable@uq.edu.co");
        responsable.setRol(RolUsuario.RESPONSABLE);
        responsable.setActivo(true);

        solicitudEntity = new SolicitudEntity();
        solicitudEntity.setId(50L);
        solicitudEntity.setVersion(1L);
        solicitudEntity.setTipo(TipoSolicitud.HOMOLOGACION);
        solicitudEntity.setDescripcion("Necesito homologar una materia.");
        solicitudEntity.setCanalOrigen(CanalOrigen.CORREO);
        solicitudEntity.setFechaRegistro(LocalDateTime.now());
        solicitudEntity.setEstado(EstadoSolicitud.REGISTRADA);
        solicitudEntity.setSolicitante(estudiante);
    }

    @Test
    @DisplayName("Debe registrar una solicitud correctamente")
    void deberiaRegistrarSolicitudCorrectamente() {
        SolicitudCreateRequest request = new SolicitudCreateRequest(
                TipoSolicitud.HOMOLOGACION,
                "Necesito homologar una materia.",
                CanalOrigen.CORREO,
                10L
        );

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(solicitudRepository.saveAndFlush(ArgumentMatchers.any(SolicitudEntity.class)))
                .thenAnswer(invocation -> {
                    SolicitudEntity entity = invocation.getArgument(0);
                    entity.setId(50L);
                    entity.setVersion(1L);
                    return entity;
                });
        when(historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(50L))
                .thenReturn(List.of());

        SolicitudResponse response = solicitudService.registrarSolicitud(request);

        assertNotNull(response);
        assertEquals(50L, response.getId());
        assertEquals(TipoSolicitud.HOMOLOGACION, response.getTipo());
        assertEquals(EstadoSolicitud.REGISTRADA, response.getEstado());
        assertEquals(1L, response.getVersion());

        verify(solicitudRepository).saveAndFlush(any(SolicitudEntity.class));
        verify(historialSolicitudRepository).save(any(HistorialSolicitudEntity.class));
    }

    @Test
    @DisplayName("Debe clasificar una solicitud correctamente")
    void deberiaClasificarSolicitudCorrectamente() {
        ClasificarSolicitudRequest request =
                new ClasificarSolicitudRequest(TipoSolicitud.CANCELACION_ASIGNATURAS, 1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(solicitudRepository.findById(50L)).thenReturn(Optional.of(solicitudEntity));
        when(solicitudRepository.saveAndFlush(any(SolicitudEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(50L))
                .thenReturn(List.of());

        SolicitudResponse response = solicitudService.clasificarSolicitud(50L, 1L, request);

        assertNotNull(response);
        assertEquals(TipoSolicitud.CANCELACION_ASIGNATURAS, response.getTipo());
        assertEquals(EstadoSolicitud.CLASIFICADA, response.getEstado());

        verify(solicitudRepository).saveAndFlush(any(SolicitudEntity.class));
        verify(historialSolicitudRepository).save(any(HistorialSolicitudEntity.class));
    }

    @Test
    @DisplayName("Debe lanzar conflicto si la versión no coincide al clasificar")
    void deberiaLanzarConflictoSiLaVersionNoCoincideAlClasificar() {
        ClasificarSolicitudRequest request =
                new ClasificarSolicitudRequest(TipoSolicitud.CANCELACION_ASIGNATURAS, 99L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(solicitudRepository.findById(50L)).thenReturn(Optional.of(solicitudEntity));

        assertThrows(ConcurrenciaException.class,
                () -> solicitudService.clasificarSolicitud(50L, 1L, request));

        verify(solicitudRepository, never()).saveAndFlush(any(SolicitudEntity.class));
    }

    @Test
    @DisplayName("Debe asignar prioridad correctamente")
    void deberiaAsignarPrioridadCorrectamente() {
        solicitudEntity.setEstado(EstadoSolicitud.CLASIFICADA);

        AsignarPrioridadRequest request = new AsignarPrioridadRequest();
        request.setImpactoAcademico(ImpactoAcademico.ALTO);
        request.setFechaLimite(LocalDate.now().plusDays(2));
        request.setVersion(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(solicitudRepository.findById(50L)).thenReturn(Optional.of(solicitudEntity));
        when(solicitudRepository.saveAndFlush(any(SolicitudEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(50L))
                .thenReturn(List.of());

        SolicitudResponse response = solicitudService.asignarPrioridad(50L, request, 1L);

        assertNotNull(response);
        assertEquals(Prioridad.CRITICA, response.getPrioridad());
        assertNotNull(response.getJustificacionPrioridad());

        verify(solicitudRepository).saveAndFlush(any(SolicitudEntity.class));
        verify(historialSolicitudRepository).save(any(HistorialSolicitudEntity.class));
    }

    @Test
    @DisplayName("Debe asignar responsable correctamente")
    void deberiaAsignarResponsableCorrectamente() {
        AsignarResponsableRequest request = new AsignarResponsableRequest();
        request.setResponsableId(2L);
        request.setVersion(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(responsable));
        when(solicitudRepository.findById(50L)).thenReturn(Optional.of(solicitudEntity));
        when(solicitudRepository.saveAndFlush(any(SolicitudEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(50L))
                .thenReturn(List.of());

        SolicitudResponse response = solicitudService.asignarResponsable(50L, request, 1L);

        assertNotNull(response);
        assertEquals(2L, response.getResponsableAsignadoId());

        verify(solicitudRepository).saveAndFlush(any(SolicitudEntity.class));
        verify(historialSolicitudRepository).save(any(HistorialSolicitudEntity.class));
    }

    @Test
    @DisplayName("Debe cambiar estado correctamente")
    void deberiaCambiarEstadoCorrectamente() {
        solicitudEntity.setEstado(EstadoSolicitud.CLASIFICADA);
        solicitudEntity.setResponsableAsignado(responsable);

        CambiarEstadoRequest request = new CambiarEstadoRequest();
        request.setNuevoEstado(EstadoSolicitud.EN_ATENCION);
        request.setObservacion("Se inicia la atención del caso");
        request.setUsuarioId(2L);
        request.setVersion(1L);

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(responsable));
        when(solicitudRepository.findById(50L)).thenReturn(Optional.of(solicitudEntity));
        when(solicitudRepository.saveAndFlush(any(SolicitudEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(50L))
                .thenReturn(List.of());

        SolicitudResponse response = solicitudService.cambiarEstado(50L, request);

        assertNotNull(response);
        assertEquals(EstadoSolicitud.EN_ATENCION, response.getEstado());

        verify(solicitudRepository).saveAndFlush(any(SolicitudEntity.class));
        verify(historialSolicitudRepository).save(any(HistorialSolicitudEntity.class));
    }

    @Test
    @DisplayName("Debe cerrar solicitud correctamente")
    void deberiaCerrarSolicitudCorrectamente() {
        solicitudEntity.setEstado(EstadoSolicitud.ATENDIDA);

        CerrarSolicitudRequest request = new CerrarSolicitudRequest();
        request.setObservacionCierre("La solicitud fue atendida y cerrada correctamente.");
        request.setVersion(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(solicitudRepository.findById(50L)).thenReturn(Optional.of(solicitudEntity));
        when(solicitudRepository.saveAndFlush(any(SolicitudEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(50L))
                .thenReturn(List.of());

        SolicitudResponse response = solicitudService.cerrarSolicitud(50L, request, 1L);

        assertNotNull(response);
        assertEquals(EstadoSolicitud.CERRADA, response.getEstado());
        assertEquals("La solicitud fue atendida y cerrada correctamente.", response.getObservacionCierre());

        verify(solicitudRepository).saveAndFlush(any(SolicitudEntity.class));
        verify(historialSolicitudRepository).save(any(HistorialSolicitudEntity.class));
    }

    @Test
    @DisplayName("Debe listar solicitudes con filtros")
    void deberiaListarSolicitudesConFiltros() {
        when(solicitudRepository.findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                any(PageRequest.class)
        )).thenReturn(new PageImpl<>(List.of(solicitudEntity)));

        when(historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(50L))
                .thenReturn(List.of());

        PageResponse<SolicitudResponse> response = solicitudService.listarSolicitudes(
                EstadoSolicitud.REGISTRADA,
                TipoSolicitud.HOMOLOGACION,
                null,
                null,
                0,
                10,
                "id",
                "desc"
        );

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(50L, response.getContent().get(0).getId());
    }

    @Test
    @DisplayName("Debe lanzar excepción si usuario no es admin al clasificar")
    void deberiaLanzarExcepcionSiUsuarioNoEsAdminAlClasificar() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(estudiante));

        ClasificarSolicitudRequest request =
                new ClasificarSolicitudRequest(TipoSolicitud.CANCELACION_ASIGNATURAS, 1L);

        assertThrows(AutorizacionException.class,
                () -> solicitudService.clasificarSolicitud(50L, 10L, request));
    }

    @Test
    @DisplayName("Debe generar resumen de solicitud")
    void deberiaGenerarResumenSolicitud() {
        when(solicitudRepository.findById(50L)).thenReturn(Optional.of(solicitudEntity));
        when(historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(50L))
                .thenReturn(List.of());
        when(iaService.generarResumen(anyString()))
                .thenReturn("Resumen automático generado");

        String resumen = solicitudService.generarResumenSolicitud(50L);

        assertNotNull(resumen);
        assertEquals("Resumen automático generado", resumen);
    }
}