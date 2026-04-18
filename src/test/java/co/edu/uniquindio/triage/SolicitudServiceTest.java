package co.edu.uniquindio.triage;

import co.edu.uniquindio.triage.domain.enums.CanalOrigen;
import co.edu.uniquindio.triage.domain.enums.EstadoSolicitud;
import co.edu.uniquindio.triage.domain.enums.ImpactoAcademico;
import co.edu.uniquindio.triage.domain.enums.Prioridad;
import co.edu.uniquindio.triage.domain.enums.RolUsuario;
import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.triage.dto.request.AsignarPrioridadRequest;
import co.edu.uniquindio.triage.dto.request.AsignarResponsableRequest;
import co.edu.uniquindio.triage.dto.request.CambiarEstadoRequest;
import co.edu.uniquindio.triage.dto.request.ClasificarSolicitudRequest;
import co.edu.uniquindio.triage.dto.request.CerrarSolicitudRequest;
import co.edu.uniquindio.triage.dto.request.SolicitudCreateRequest;
import co.edu.uniquindio.triage.dto.response.HistorialSolicitudResponse;
import co.edu.uniquindio.triage.dto.response.SolicitudResponse;
import co.edu.uniquindio.triage.domain.entity.HistorialSolicitudEntity;
import co.edu.uniquindio.triage.domain.entity.SolicitudEntity;
import co.edu.uniquindio.triage.domain.entity.UsuarioEntity;
import co.edu.uniquindio.triage.repository.HistorialSolicitudRepository;
import co.edu.uniquindio.triage.repository.SolicitudRepository;
import co.edu.uniquindio.triage.repository.UsuarioRepository;
import co.edu.uniquindio.triage.service.impl.IAService;
import co.edu.uniquindio.triage.service.impl.SolicitudService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HistorialSolicitudRepository historialSolicitudRepository;

    @Mock
    private IAService iaService;

    @InjectMocks
    private SolicitudService solicitudService;

    private UsuarioEntity estudiante;
    private UsuarioEntity admin;
    private UsuarioEntity responsable;
    private SolicitudEntity solicitudEntity;

    @BeforeEach
    void setUp() {
        estudiante = crearUsuarioEntity(10L, "Estudiante Uno", "est1@uq.edu.co", true, RolUsuario.ESTUDIANTE);
        admin = crearUsuarioEntity(1L, "Admin Uno", "admin@uq.edu.co", true, RolUsuario.ADMIN);
        responsable = crearUsuarioEntity(2L, "Responsable Uno", "resp@uq.edu.co", true, RolUsuario.RESPONSABLE);

        solicitudEntity = crearSolicitudEntityBase();
    }

    @Test
    @DisplayName("Debería registrar una solicitud correctamente")
    void deberiaRegistrarSolicitudCorrectamente() {
        SolicitudCreateRequest request = new SolicitudCreateRequest(
                TipoSolicitud.HOMOLOGACION,
                "Solicito homologación de una materia cursada previamente.",
                CanalOrigen.SAC,
                estudiante.getId()
        );

        when(usuarioRepository.findById(estudiante.getId())).thenReturn(Optional.of(estudiante));
        when(solicitudRepository.save(any(SolicitudEntity.class))).thenAnswer(invocation -> {
            SolicitudEntity entity = invocation.getArgument(0);
            entity.setId(100L);
            return entity;
        });
        when(historialSolicitudRepository.save(any(HistorialSolicitudEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(100L)).thenReturn(List.of());

        SolicitudResponse response = solicitudService.registrarSolicitud(request);

        assertNotNull(response);
        assertEquals(TipoSolicitud.HOMOLOGACION, response.getTipo());
        assertEquals(EstadoSolicitud.REGISTRADA, response.getEstado());
        verify(solicitudRepository, times(1)).save(any(SolicitudEntity.class));
        verify(historialSolicitudRepository, atLeastOnce()).save(any(HistorialSolicitudEntity.class));
    }

    @Test
    @DisplayName("Debería clasificar una solicitud correctamente")
    void deberiaClasificarSolicitudCorrectamente() {
        ClasificarSolicitudRequest request = new ClasificarSolicitudRequest(TipoSolicitud.CANCELACION_ASIGNATURAS);

        solicitudEntity.setEstado(EstadoSolicitud.REGISTRADA);
        solicitudEntity.setTipo(TipoSolicitud.CONSULTA_ACADEMICA);

        when(usuarioRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(solicitudRepository.findById(solicitudEntity.getId())).thenReturn(Optional.of(solicitudEntity));
        when(solicitudRepository.save(any(SolicitudEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(historialSolicitudRepository.save(any(HistorialSolicitudEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudEntity.getId())).thenReturn(List.of());

        SolicitudResponse response = solicitudService.clasificarSolicitud(solicitudEntity.getId(), admin.getId(), request);

        assertNotNull(response);
        assertEquals(TipoSolicitud.CANCELACION_ASIGNATURAS, response.getTipo());
        assertEquals(EstadoSolicitud.CLASIFICADA, response.getEstado());
    }

    @Test
    @DisplayName("Debería asignar prioridad correctamente")
    void deberiaAsignarPrioridadCorrectamente() {
        AsignarPrioridadRequest request = new AsignarPrioridadRequest();
        request.setImpactoAcademico(ImpactoAcademico.ALTO);
        request.setFechaLimite(LocalDate.now().plusDays(1));

        solicitudEntity.setEstado(EstadoSolicitud.CLASIFICADA);
        solicitudEntity.setTipo(TipoSolicitud.SOLICITUD_CUPOS);

        when(usuarioRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(solicitudRepository.findById(solicitudEntity.getId())).thenReturn(Optional.of(solicitudEntity));
        when(solicitudRepository.save(any(SolicitudEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(historialSolicitudRepository.save(any(HistorialSolicitudEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudEntity.getId())).thenReturn(List.of());

        SolicitudResponse response = solicitudService.asignarPrioridad(solicitudEntity.getId(), request, admin.getId());

        assertNotNull(response);
        assertNotNull(response.getPrioridad());
        assertNotNull(response.getJustificacionPrioridad());
    }

    @Test
    @DisplayName("Debería asignar responsable activo correctamente")
    void deberiaAsignarResponsableActivoCorrectamente() {
        AsignarResponsableRequest request = new AsignarResponsableRequest();
        request.setResponsableId(responsable.getId());

        solicitudEntity.setEstado(EstadoSolicitud.CLASIFICADA);

        when(usuarioRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(usuarioRepository.findById(responsable.getId())).thenReturn(Optional.of(responsable));
        when(solicitudRepository.findById(solicitudEntity.getId())).thenReturn(Optional.of(solicitudEntity));
        when(solicitudRepository.save(any(SolicitudEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(historialSolicitudRepository.save(any(HistorialSolicitudEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudEntity.getId())).thenReturn(List.of());

        SolicitudResponse response = solicitudService.asignarResponsable(solicitudEntity.getId(), request, admin.getId());

        assertNotNull(response);
        assertNotNull(response.getResponsableAsignadoId());
        assertEquals(responsable.getId(), response.getResponsableAsignadoId());
    }

    @Test
    @DisplayName("Debería registrar en historial el usuario real al cambiar estado")
    void deberiaRegistrarHistorialConUsuarioRealAlCambiarEstado() {
        CambiarEstadoRequest request = new CambiarEstadoRequest();
        request.setNuevoEstado(EstadoSolicitud.EN_ATENCION);
        request.setObservacion("Se inicia atención de la solicitud.");
        request.setUsuarioId(admin.getId());

        solicitudEntity.setEstado(EstadoSolicitud.CLASIFICADA);

        when(usuarioRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(solicitudRepository.findById(solicitudEntity.getId())).thenReturn(Optional.of(solicitudEntity));
        when(solicitudRepository.save(any(SolicitudEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(historialSolicitudRepository.save(any(HistorialSolicitudEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudEntity.getId())).thenReturn(List.of());

        SolicitudResponse response = solicitudService.cambiarEstado(solicitudEntity.getId(), request);

        assertNotNull(response);
        assertEquals(EstadoSolicitud.EN_ATENCION, response.getEstado());

        ArgumentCaptor<HistorialSolicitudEntity> captor = ArgumentCaptor.forClass(HistorialSolicitudEntity.class);
        verify(historialSolicitudRepository).save(captor.capture());

        HistorialSolicitudEntity historialGuardado = captor.getValue();
        assertNotNull(historialGuardado.getUsuarioResponsable());
        assertEquals(admin.getId(), historialGuardado.getUsuarioResponsable().getId());
        assertEquals("CAMBIO_ESTADO", historialGuardado.getAccion());
    }

    @Test
    @DisplayName("Debería cerrar una solicitud correctamente")
    void deberiaCerrarSolicitudCorrectamente() {
        CerrarSolicitudRequest request = new CerrarSolicitudRequest();
        request.setObservacionCierre("La solicitud fue resuelta y cerrada correctamente.");

        solicitudEntity.setEstado(EstadoSolicitud.ATENDIDA);

        when(usuarioRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(solicitudRepository.findById(solicitudEntity.getId())).thenReturn(Optional.of(solicitudEntity));
        when(solicitudRepository.save(any(SolicitudEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(historialSolicitudRepository.save(any(HistorialSolicitudEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudEntity.getId())).thenReturn(List.of());

        SolicitudResponse response = solicitudService.cerrarSolicitud(solicitudEntity.getId(), request, admin.getId());

        assertNotNull(response);
        assertEquals(EstadoSolicitud.CERRADA, response.getEstado());
        assertEquals("La solicitud fue resuelta y cerrada correctamente.", response.getObservacionCierre());
    }

    @Test
    @DisplayName("Debería obtener historial correctamente")
    void deberiaObtenerHistorialCorrectamente() {
        HistorialSolicitudEntity h1 = new HistorialSolicitudEntity();
        h1.setId(1L);
        h1.setAccion("REGISTRO");
        h1.setObservaciones("Solicitud registrada");
        h1.setFechaHora(LocalDateTime.now());
        h1.setSolicitud(solicitudEntity);
        h1.setUsuarioResponsable(estudiante);

        HistorialSolicitudEntity h2 = new HistorialSolicitudEntity();
        h2.setId(2L);
        h2.setAccion("CLASIFICACION");
        h2.setObservaciones("Solicitud clasificada");
        h2.setFechaHora(LocalDateTime.now());
        h2.setSolicitud(solicitudEntity);
        h2.setUsuarioResponsable(admin);

        when(solicitudRepository.findById(solicitudEntity.getId())).thenReturn(Optional.of(solicitudEntity));
        when(historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudEntity.getId()))
                .thenReturn(List.of(h1, h2));

        List<HistorialSolicitudResponse> historial = solicitudService.obtenerHistorial(solicitudEntity.getId());

        assertNotNull(historial);
        assertEquals(2, historial.size());
    }

    private UsuarioEntity crearUsuarioEntity(Long id, String nombre, String email, boolean activo, RolUsuario rol) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setActivo(activo);
        usuario.setRol(rol);
        return usuario;
    }

    private SolicitudEntity crearSolicitudEntityBase() {
        SolicitudEntity solicitud = new SolicitudEntity();
        solicitud.setId(50L);
        solicitud.setTipo(TipoSolicitud.HOMOLOGACION);
        solicitud.setDescripcion("Solicitud base para pruebas del servicio.");
        solicitud.setCanalOrigen(CanalOrigen.SAC);
        solicitud.setFechaRegistro(LocalDateTime.now());
        solicitud.setEstado(EstadoSolicitud.REGISTRADA);
        solicitud.setSolicitante(estudiante);
        return solicitud;
    }
}