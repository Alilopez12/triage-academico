package co.edu.uniquindio.triage;

import co.edu.uniquindio.triage.domain.enums.CanalOrigen;
import co.edu.uniquindio.triage.domain.enums.EstadoSolicitud;
import co.edu.uniquindio.triage.domain.enums.ImpactoAcademico;
import co.edu.uniquindio.triage.domain.enums.Prioridad;
import co.edu.uniquindio.triage.domain.enums.RolUsuario;
import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.triage.exception.ReglaNegocioException;
import co.edu.uniquindio.triage.exception.TransicionInvalidaException;
import co.edu.uniquindio.triage.domain.model.Solicitud;
import co.edu.uniquindio.triage.domain.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SolicitudTest {

    @Test
    @DisplayName("Debería crear una solicitud en estado REGISTRADA")
    void deberiaCrearSolicitudEnEstadoRegistrada() {
        Usuario solicitante = crearUsuarioActivo(RolUsuario.ESTUDIANTE);

        Solicitud solicitud = Solicitud.crear(
                TipoSolicitud.HOMOLOGACION,
                "Solicito homologación de una asignatura cursada anteriormente.",
                CanalOrigen.SAC,
                solicitante
        );

        assertNotNull(solicitud);
        assertEquals(TipoSolicitud.HOMOLOGACION, solicitud.getTipo());
        assertEquals("Solicito homologación de una asignatura cursada anteriormente.", solicitud.getDescripcion());
        assertEquals(CanalOrigen.SAC, solicitud.getCanalOrigen());
        assertEquals(EstadoSolicitud.REGISTRADA, solicitud.getEstado());
        assertNotNull(solicitud.getFechaRegistro());

        assertNull(solicitud.getPrioridad());
        assertNull(solicitud.getImpactoAcademico());
        assertNull(solicitud.getFechaLimite());
        assertNull(solicitud.getResponsableAsignado());
        assertNull(solicitud.getObservacionCierre());
    }

    @Test
    @DisplayName("No debería crear una solicitud con descripción vacía")
    void noDeberiaCrearSolicitudConDescripcionVacia() {
        Usuario solicitante = crearUsuarioActivo(RolUsuario.ESTUDIANTE);

        assertThrows(ReglaNegocioException.class, () ->
                Solicitud.crear(
                        TipoSolicitud.HOMOLOGACION,
                        "",
                        CanalOrigen.SAC,
                        solicitante
                )
        );
    }

    @Test
    @DisplayName("Debería clasificar una solicitud desde REGISTRADA")
    void deberiaClasificarSolicitudDesdeRegistrada() {
        Usuario solicitante = crearUsuarioActivo(RolUsuario.ESTUDIANTE);

        Solicitud solicitud = Solicitud.crear(
                TipoSolicitud.CONSULTA_ACADEMICA,
                "Necesito orientación sobre el proceso de homologación.",
                CanalOrigen.CORREO,
                solicitante
        );

        solicitud.clasificar(TipoSolicitud.HOMOLOGACION);

        assertEquals(TipoSolicitud.HOMOLOGACION, solicitud.getTipo());
        assertEquals(EstadoSolicitud.CLASIFICADA, solicitud.getEstado());
    }

    @Test
    @DisplayName("No debería permitir clasificar una solicitud que no esté en REGISTRADA")
    void noDeberiaPermitirClasificarSiNoEstaRegistrada() {
        Usuario solicitante = crearUsuarioActivo(RolUsuario.ESTUDIANTE);

        Solicitud solicitud = Solicitud.crear(
                TipoSolicitud.SOLICITUD_CUPOS,
                "Necesito un cupo adicional en la asignatura.",
                CanalOrigen.PRESENCIAL,
                solicitante
        );

        solicitud.clasificar(TipoSolicitud.SOLICITUD_CUPOS);

        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.clasificar(TipoSolicitud.HOMOLOGACION)
        );
    }

    @Test
    @DisplayName("Debería permitir transición REGISTRADA a CLASIFICADA")
    void deberiaPermitirTransicionRegistradaAClasificada() {
        Usuario solicitante = crearUsuarioActivo(RolUsuario.ESTUDIANTE);

        Solicitud solicitud = Solicitud.crear(
                TipoSolicitud.REGISTRO_ASIGNATURAS,
                "Solicito registro de asignatura para el siguiente semestre.",
                CanalOrigen.SAC,
                solicitante
        );

        solicitud.cambiarEstado(EstadoSolicitud.CLASIFICADA);

        assertEquals(EstadoSolicitud.CLASIFICADA, solicitud.getEstado());
    }

    @Test
    @DisplayName("No debería permitir transición inválida de REGISTRADA a CERRADA")
    void noDeberiaPermitirTransicionInvalidaDeRegistradaACerrada() {
        Usuario solicitante = crearUsuarioActivo(RolUsuario.ESTUDIANTE);

        Solicitud solicitud = Solicitud.crear(
                TipoSolicitud.REGISTRO_ASIGNATURAS,
                "Solicito registro de asignatura para el siguiente semestre.",
                CanalOrigen.SAC,
                solicitante
        );

        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.cambiarEstado(EstadoSolicitud.CERRADA)
        );
    }

    @Test
    @DisplayName("Debería recorrer correctamente el flujo REGISTRADA -> CLASIFICADA -> EN_ATENCION -> ATENDIDA")
    void deberiaRecorrerFlujoCorrecto() {
        Usuario solicitante = crearUsuarioActivo(RolUsuario.ESTUDIANTE);

        Solicitud solicitud = Solicitud.crear(
                TipoSolicitud.CANCELACION_ASIGNATURAS,
                "Solicito cancelación por cruce de horario.",
                CanalOrigen.CSU,
                solicitante
        );

        solicitud.cambiarEstado(EstadoSolicitud.CLASIFICADA);
        assertEquals(EstadoSolicitud.CLASIFICADA, solicitud.getEstado());

        solicitud.cambiarEstado(EstadoSolicitud.EN_ATENCION);
        assertEquals(EstadoSolicitud.EN_ATENCION, solicitud.getEstado());

        solicitud.cambiarEstado(EstadoSolicitud.ATENDIDA);
        assertEquals(EstadoSolicitud.ATENDIDA, solicitud.getEstado());
    }

    @Test
    @DisplayName("No debería cerrar una solicitud si no está ATENDIDA")
    void noDeberiaCerrarSiNoEstaAtendida() {
        Usuario solicitante = crearUsuarioActivo(RolUsuario.ESTUDIANTE);

        Solicitud solicitud = Solicitud.crear(
                TipoSolicitud.HOMOLOGACION,
                "Solicito homologación de créditos.",
                CanalOrigen.CORREO,
                solicitante
        );

        assertThrows(TransicionInvalidaException.class, () ->
                solicitud.cerrar("Se cierra la solicitud con observación suficiente.")
        );
    }

    @Test
    @DisplayName("Debería cerrar una solicitud atendida con observación válida")
    void deberiaCerrarSolicitudAtendidaConObservacionValida() {
        Usuario solicitante = crearUsuarioActivo(RolUsuario.ESTUDIANTE);

        Solicitud solicitud = Solicitud.crear(
                TipoSolicitud.HOMOLOGACION,
                "Solicito homologación de créditos.",
                CanalOrigen.CORREO,
                solicitante
        );

        solicitud.cambiarEstado(EstadoSolicitud.CLASIFICADA);
        solicitud.cambiarEstado(EstadoSolicitud.EN_ATENCION);
        solicitud.cambiarEstado(EstadoSolicitud.ATENDIDA);

        solicitud.cerrar("La solicitud fue resuelta y documentada correctamente.");

        assertEquals(EstadoSolicitud.CERRADA, solicitud.getEstado());
        assertEquals("La solicitud fue resuelta y documentada correctamente.", solicitud.getObservacionCierre());
    }

    @Test
    @DisplayName("No debería cerrar una solicitud con observación vacía")
    void noDeberiaCerrarSolicitudConObservacionVacia() {
        Usuario solicitante = crearUsuarioActivo(RolUsuario.ESTUDIANTE);

        Solicitud solicitud = Solicitud.crear(
                TipoSolicitud.HOMOLOGACION,
                "Solicito homologación de créditos.",
                CanalOrigen.CORREO,
                solicitante
        );

        solicitud.cambiarEstado(EstadoSolicitud.CLASIFICADA);
        solicitud.cambiarEstado(EstadoSolicitud.EN_ATENCION);
        solicitud.cambiarEstado(EstadoSolicitud.ATENDIDA);

        assertThrows(ReglaNegocioException.class, () ->
                solicitud.cerrar(" ")
        );
    }

    @Test
    @DisplayName("No debería permitir modificar una solicitud cerrada")
    void noDeberiaPermitirModificarSolicitudCerrada() {
        Usuario solicitante = crearUsuarioActivo(RolUsuario.ESTUDIANTE);

        Solicitud solicitud = Solicitud.crear(
                TipoSolicitud.SOLICITUD_CUPOS,
                "Necesito cupo adicional por situación académica.",
                CanalOrigen.SAC,
                solicitante
        );

        solicitud.cambiarEstado(EstadoSolicitud.CLASIFICADA);
        solicitud.cambiarEstado(EstadoSolicitud.EN_ATENCION);
        solicitud.cambiarEstado(EstadoSolicitud.ATENDIDA);
        solicitud.cerrar("La situación fue atendida y finalizada correctamente.");

        assertThrows(ReglaNegocioException.class, () ->
                solicitud.asignarImpactoAcademico(ImpactoAcademico.ALTO)
        );

        assertThrows(ReglaNegocioException.class, () ->
                solicitud.asignarFechaLimite(LocalDate.now().plusDays(2))
        );
    }

    @Test
    @DisplayName("Debería asignar impacto académico")
    void deberiaAsignarImpactoAcademico() {
        Usuario solicitante = crearUsuarioActivo(RolUsuario.ESTUDIANTE);

        Solicitud solicitud = Solicitud.crear(
                TipoSolicitud.REGISTRO_ASIGNATURAS,
                "Requiero registro extraordinario de asignatura.",
                CanalOrigen.CSU,
                solicitante
        );

        solicitud.asignarImpactoAcademico(ImpactoAcademico.ALTO);

        assertEquals(ImpactoAcademico.ALTO, solicitud.getImpactoAcademico());
    }

    @Test
    @DisplayName("Debería asignar fecha límite")
    void deberiaAsignarFechaLimite() {
        Usuario solicitante = crearUsuarioActivo(RolUsuario.ESTUDIANTE);

        Solicitud solicitud = Solicitud.crear(
                TipoSolicitud.REGISTRO_ASIGNATURAS,
                "Requiero registro extraordinario de asignatura.",
                CanalOrigen.CSU,
                solicitante
        );

        LocalDate fechaLimite = LocalDate.now().plusDays(2);
        solicitud.asignarFechaLimite(fechaLimite);

        assertEquals(fechaLimite, solicitud.getFechaLimite());
    }

    @Test
    @DisplayName("Debería calcular prioridad CRITICA con impacto alto y fecha muy próxima")
    void deberiaCalcularPrioridadCritica() {
        Usuario solicitante = crearUsuarioActivo(RolUsuario.ESTUDIANTE);

        Solicitud solicitud = Solicitud.crear(
                TipoSolicitud.SOLICITUD_CUPOS,
                "Necesito cupo urgente para no afectar el semestre.",
                CanalOrigen.SAC,
                solicitante
        );

        solicitud.asignarImpactoAcademico(ImpactoAcademico.ALTO);
        solicitud.asignarFechaLimite(LocalDate.now().plusDays(1));

        Prioridad prioridad = solicitud.calcularPrioridad();

        assertEquals(Prioridad.CRITICA, prioridad);
    }

    @Test
    @DisplayName("Debería calcular y asignar prioridad automáticamente")
    void deberiaCalcularYAsignarPrioridad() {
        Usuario solicitante = crearUsuarioActivo(RolUsuario.ESTUDIANTE);

        Solicitud solicitud = Solicitud.crear(
                TipoSolicitud.HOMOLOGACION,
                "Solicito homologación de materias cursadas previamente.",
                CanalOrigen.CORREO,
                solicitante
        );

        solicitud.asignarImpactoAcademico(ImpactoAcademico.MEDIO);
        solicitud.asignarFechaLimite(LocalDate.now().plusDays(5));

        solicitud.calcularYAsignarPrioridad();

        assertNotNull(solicitud.getPrioridad());
        assertNotNull(solicitud.getJustificacionPrioridad());
    }

    @Test
    @DisplayName("No debería calcular prioridad sin impacto académico y fecha límite")
    void noDeberiaCalcularPrioridadSinDatosMinimos() {
        Usuario solicitante = crearUsuarioActivo(RolUsuario.ESTUDIANTE);

        Solicitud solicitud = Solicitud.crear(
                TipoSolicitud.CONSULTA_ACADEMICA,
                "Quiero información sobre el proceso académico.",
                CanalOrigen.SAC,
                solicitante
        );

        assertThrows(ReglaNegocioException.class, solicitud::calcularPrioridad);
    }

    @Test
    @DisplayName("Debería asignar responsable activo")
    void deberiaAsignarResponsableActivo() {
        Usuario solicitante = crearUsuarioActivo(RolUsuario.ESTUDIANTE);
        Usuario responsable = crearUsuarioActivo(RolUsuario.RESPONSABLE);

        Solicitud solicitud = Solicitud.crear(
                TipoSolicitud.CANCELACION_ASIGNATURAS,
                "Solicito cancelación por motivos personales.",
                CanalOrigen.CORREO,
                solicitante
        );

        solicitud.asignarResponsable(responsable);

        assertNotNull(solicitud.getResponsableAsignado());
        assertEquals(responsable.getId(), solicitud.getResponsableAsignado().getId());
    }

    @Test
    @DisplayName("No debería asignar responsable inactivo")
    void noDeberiaAsignarResponsableInactivo() {
        Usuario solicitante = crearUsuarioActivo(RolUsuario.ESTUDIANTE);
        Usuario responsableInactivo = crearUsuarioInactivo(RolUsuario.RESPONSABLE);

        Solicitud solicitud = Solicitud.crear(
                TipoSolicitud.CANCELACION_ASIGNATURAS,
                "Solicito cancelación por motivos personales.",
                CanalOrigen.CORREO,
                solicitante
        );

        assertThrows(ReglaNegocioException.class, () ->
                solicitud.asignarResponsable(responsableInactivo)
        );
    }

    private Usuario crearUsuarioActivo(RolUsuario rol) {
        return new Usuario(1L, "Usuario Prueba", "usuario@prueba.com", true, rol);
    }

    private Usuario crearUsuarioInactivo(RolUsuario rol) {
        return new Usuario(2L, "Usuario Inactivo", "inactivo@prueba.com", false, rol);
    }
}
