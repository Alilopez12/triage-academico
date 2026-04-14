package co.edu.uniquindio.triage.service.impl;

import co.edu.uniquindio.triage.domain.entity.HistorialSolicitudEntity;
import co.edu.uniquindio.triage.domain.entity.SolicitudEntity;
import co.edu.uniquindio.triage.domain.entity.UsuarioEntity;
import co.edu.uniquindio.triage.domain.model.HistorialSolicitud;
import co.edu.uniquindio.triage.domain.model.Solicitud;
import co.edu.uniquindio.triage.domain.model.Usuario;
import co.edu.uniquindio.triage.dto.request.CambiarEstadoRequest;
import co.edu.uniquindio.triage.dto.request.CerrarSolicitudRequest;
import co.edu.uniquindio.triage.dto.request.SolicitudCreateRequest;
import co.edu.uniquindio.triage.dto.response.HistorialSolicitudResponse;
import co.edu.uniquindio.triage.dto.response.SolicitudResponse;
import co.edu.uniquindio.triage.exception.RecursoNoEncontradoException;
import co.edu.uniquindio.triage.mapper.HistorialSolicitudMapper;
import co.edu.uniquindio.triage.mapper.SolicitudMapper;
import co.edu.uniquindio.triage.mapper.UsuarioMapper;
import co.edu.uniquindio.triage.repository.HistorialSolicitudRepository;
import co.edu.uniquindio.triage.repository.SolicitudRepository;
import co.edu.uniquindio.triage.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.uniquindio.triage.dto.request.AsignarPrioridadRequest;
import co.edu.uniquindio.triage.domain.enums.EstadoSolicitud;
import co.edu.uniquindio.triage.domain.enums.RolUsuario;

import co.edu.uniquindio.triage.domain.enums.Prioridad;
import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.triage.dto.request.AsignarResponsableRequest;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialSolicitudRepository historialSolicitudRepository;
    private final IAService iaService;

    public SolicitudService(SolicitudRepository solicitudRepository,
                            UsuarioRepository usuarioRepository,
                            HistorialSolicitudRepository historialSolicitudRepository,
                            IAService iaService) {

        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.historialSolicitudRepository = historialSolicitudRepository;
        this.iaService = iaService;
    }

    public SolicitudResponse registrarSolicitud(SolicitudCreateRequest request) {

        UsuarioEntity solicitanteEntity = usuarioRepository.findById(request.getSolicitanteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un usuario con id " + request.getSolicitanteId()
                ));

        if (solicitanteEntity.getRol() != RolUsuario.ESTUDIANTE) {
            throw new IllegalStateException("Solo los estudiantes pueden registrar solicitudes");
        }

        Usuario solicitanteDomain = UsuarioMapper.toDomain(solicitanteEntity);

        Solicitud solicitudDomain = Solicitud.crear(
                request.getTipo(),
                request.getDescripcion(),
                request.getCanalOrigen(),
                solicitanteDomain
        );

        SolicitudEntity solicitudEntity = SolicitudMapper.toEntity(solicitudDomain);
        solicitudEntity.setSolicitante(solicitanteEntity);

        SolicitudEntity solicitudGuardada = solicitudRepository.save(solicitudEntity);

        Solicitud solicitudGuardadaDomain = SolicitudMapper.toDomain(solicitudGuardada);

        HistorialSolicitud historialInicialDomain = HistorialSolicitud.crearRegistroInicial(
                solicitudGuardadaDomain,
                solicitanteDomain
        );

        HistorialSolicitudEntity historialEntity = HistorialSolicitudMapper.toEntity(historialInicialDomain);
        historialEntity.setSolicitud(solicitudGuardada);
        historialEntity.setUsuarioResponsable(solicitanteEntity);

        historialSolicitudRepository.save(historialEntity);

        List<HistorialSolicitudEntity> historialGuardadoEntities =
                historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudGuardada.getId());

        return SolicitudMapper.toResponse(
                solicitudGuardadaDomain,
                SolicitudMapper.toHistorialDomainList(historialGuardadoEntities)
        );
    }
    public SolicitudResponse asignarPrioridad(Long solicitudId, AsignarPrioridadRequest request, Long usuarioId) {

        // 1. Buscar solicitud
        SolicitudEntity solicitudEntity = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una solicitud con id " + solicitudId
                ));

        // 2. Buscar usuario que ejecuta la acción
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        // 3. VALIDACIÓN DE ROL
        if (usuario.getRol() != RolUsuario.ADMIN) {

            throw new IllegalStateException("No autorizado para asignar prioridad");
        }

        // 4. Convertir a dominio
        Solicitud solicitudDomain = SolicitudMapper.toDomain(solicitudEntity);

        // 5. Asignar impacto académico
        solicitudDomain.asignarImpactoAcademico(request.getImpactoAcademico());

        // 6. Asignar fecha límite
        solicitudDomain.asignarFechaLimite(request.getFechaLimite());

        // 7. Calcular prioridad automáticamente
        solicitudDomain.calcularYAsignarPrioridad();

        // 8. Actualizar entity
        // 2. Convertir a dominio
        Solicitud solicitudDomain = SolicitudMapper.toDomain(solicitudEntity);

        // 3. Asignar impacto académico
        solicitudDomain.asignarImpactoAcademico(request.getImpactoAcademico());

        // 4. Asignar fecha límite
        solicitudDomain.asignarFechaLimite(request.getFechaLimite());

        // 5. Calcular prioridad automáticamente
        solicitudDomain.calcularYAsignarPrioridad();

        // 6. ACTUALIZAR ENTITY EXISTENTE
        solicitudEntity.setImpactoAcademico(solicitudDomain.getImpactoAcademico());
        solicitudEntity.setFechaLimite(solicitudDomain.getFechaLimite());
        solicitudEntity.setPrioridad(solicitudDomain.getPrioridad());
        solicitudEntity.setJustificacionPrioridad(solicitudDomain.getJustificacionPrioridad());

        solicitudRepository.save(solicitudEntity);

        // 9. Crear historial
        HistorialSolicitud historialDomain = HistorialSolicitud.crear(
                "ASIGNACION_PRIORIDAD",
                "Se asignó prioridad automáticamente: " + solicitudDomain.getPrioridad(),
                UsuarioMapper.toDomain(usuario),
        // 7. Guardar cambios
        solicitudRepository.save(solicitudEntity);

        // 8. Crear historial
        HistorialSolicitud historialDomain = HistorialSolicitud.crear(
                "ASIGNACION_PRIORIDAD",
                "Se asignó prioridad automáticamente: " + solicitudDomain.getPrioridad(),
                UsuarioMapper.toDomain(solicitudEntity.getSolicitante()),
                solicitudDomain
        );

        HistorialSolicitudEntity historialEntity = HistorialSolicitudMapper.toEntity(historialDomain);
        historialEntity.setSolicitud(solicitudEntity);
        historialEntity.setUsuarioResponsable(solicitudEntity.getSolicitante());

        historialSolicitudRepository.save(historialEntity);

        // 9. Obtener historial actualizado
        List<HistorialSolicitudEntity> historialEntities =
                historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudId);

        // 10. Retornar response
        return SolicitudMapper.toResponse(
                solicitudDomain,
                SolicitudMapper.toHistorialDomainList(historialEntities)
        );
    }
    public SolicitudResponse asignarResponsable(Long solicitudId, AsignarResponsableRequest request) {

        SolicitudEntity solicitudEntity = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una solicitud con id " + solicitudId
                ));

        UsuarioEntity responsableEntity = usuarioRepository.findById(request.getResponsableId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un usuario responsable con id " + request.getResponsableId()
                ));

        Solicitud solicitudDomain = SolicitudMapper.toDomain(solicitudEntity);
        Usuario responsableDomain = UsuarioMapper.toDomain(responsableEntity);

        solicitudDomain.asignarResponsable(responsableDomain);

        solicitudEntity.setResponsableAsignado(responsableEntity);

        solicitudRepository.save(solicitudEntity);

        HistorialSolicitud historialDomain = HistorialSolicitud.crear(
                "ASIGNACION_RESPONSABLE",
                "Se asignó el responsable: " + responsableDomain.getNombre(),
                responsableDomain,
                solicitudDomain
        );

        HistorialSolicitudEntity historialEntity = HistorialSolicitudMapper.toEntity(historialDomain);
        historialEntity.setSolicitud(solicitudEntity);
        historialEntity.setUsuarioResponsable(usuario);

        historialSolicitudRepository.save(historialEntity);

        // 10. Obtener historial actualizado
        List<HistorialSolicitudEntity> historialEntities =
                historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudId);

        // 11. Retornar response
        historialEntity.setUsuarioResponsable(responsableEntity);

        historialSolicitudRepository.save(historialEntity);

        List<HistorialSolicitudEntity> historialEntities =
                historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudId);

        return SolicitudMapper.toResponse(
                solicitudDomain,
                SolicitudMapper.toHistorialDomainList(historialEntities)
        );
    }

    public List<HistorialSolicitudResponse> obtenerHistorial(Long solicitudId) {

        if (!solicitudRepository.existsById(solicitudId)) {
            throw new RecursoNoEncontradoException(
                    "No existe una solicitud con id " + solicitudId
            );
        }

        List<HistorialSolicitudEntity> historialEntities =
                historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudId);

        List<HistorialSolicitudResponse> response = new ArrayList<>();

        for (HistorialSolicitudEntity entity : historialEntities) {
            response.add(
                    HistorialSolicitudMapper.toResponse(
                            HistorialSolicitudMapper.toDomain(entity)
                    )
            );
        }

        return response;
    }

    public SolicitudResponse clasificarSolicitud(Long solicitudId, Long usuarioId) {

        //Buscar usuario que ejecuta la acción
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con id " + usuarioId
                ));

        // 1. Validar Rol
        if (usuario.getRol() != RolUsuario.ADMIN) {
            throw new IllegalStateException("No autorizado para clasificar solicitudes");
        }

        // 2. Buscar solicitud
    public SolicitudResponse cambiarEstado(Long solicitudId, CambiarEstadoRequest request) {

        SolicitudEntity solicitudEntity = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una solicitud con id " + solicitudId
                ));

        // 3. Convertir a dominio
        Solicitud solicitudDomain = SolicitudMapper.toDomain(solicitudEntity);

        // 4. Validaciones existentes
        if (solicitudDomain.getEstado() != EstadoSolicitud.REGISTRADA) {
            throw new IllegalStateException("Solo se puede clasificar solicitudes en estado REGISTRADA");
        }

        if (solicitudDomain.getTipo() == null) {
            throw new IllegalStateException("La solicitud no tiene tipo definido");
        }

        // 5. Clasificar
        solicitudDomain.clasificar();

        // 6. Actualizar entity
        solicitudEntity.setEstado(solicitudDomain.getEstado());
        solicitudRepository.save(solicitudEntity);

        // 7. Historial
        HistorialSolicitud historialDomain = HistorialSolicitud.crear(
                "CLASIFICACION",
                "Solicitud clasificada como " + solicitudDomain.getTipo(),
                UsuarioMapper.toDomain(usuario), //
        Solicitud solicitudDomain = SolicitudMapper.toDomain(solicitudEntity);

        solicitudDomain.cambiarEstado(request.getNuevoEstado());

        solicitudEntity.setEstado(solicitudDomain.getEstado());

        solicitudRepository.save(solicitudEntity);

        String observacionHistorial = request.getObservacion();
        if (observacionHistorial == null || observacionHistorial.isBlank()) {
            observacionHistorial = "Cambio de estado a " + solicitudDomain.getEstado();
        }

        HistorialSolicitud historialDomain = HistorialSolicitud.crear(
                "CAMBIO_ESTADO",
                observacionHistorial,
                UsuarioMapper.toDomain(solicitudEntity.getSolicitante()),
                solicitudDomain
        );

        HistorialSolicitudEntity historialEntity = HistorialSolicitudMapper.toEntity(historialDomain);
        historialEntity.setSolicitud(solicitudEntity);
        historialEntity.setUsuarioResponsable(usuario);

        historialSolicitudRepository.save(historialEntity);

        // 8. Retornar
        historialEntity.setUsuarioResponsable(solicitudEntity.getSolicitante());

        historialSolicitudRepository.save(historialEntity);

        List<HistorialSolicitudEntity> historialEntities =
                historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudId);

        return SolicitudMapper.toResponse(
                solicitudDomain,
                SolicitudMapper.toHistorialDomainList(historialEntities)
        );
    }

    public SolicitudResponse asignarResponsable(Long solicitudId, Long responsableId, Long usuarioId) {

        // 1. Buscar solicitud
    public SolicitudResponse cerrarSolicitud(Long solicitudId, CerrarSolicitudRequest request) {

        SolicitudEntity solicitudEntity = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una solicitud con id " + solicitudId
                ));

        // 2. Buscar usuario que ejecuta la acción
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        // 3. VALIDACIÓN DE AUTORIZACIÓN
        if (usuario.getRol() != RolUsuario.ADMIN) {

            throw new IllegalStateException("No autorizado para asignar responsables");
        }

        // 4. Buscar responsable
        UsuarioEntity responsableEntity = usuarioRepository.findById(responsableId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un usuario con id " + responsableId
                ));

        // 5. Validar que esté activo
        if (!responsableEntity.isActivo()) {
            throw new IllegalStateException("El responsable no está activo");
        }

        // 6. Convertir a dominio
        Solicitud solicitudDomain = SolicitudMapper.toDomain(solicitudEntity);

        // 7. Validar estado
        if (solicitudDomain.getEstado() == EstadoSolicitud.CERRADA) {
            throw new IllegalStateException("No se puede asignar una solicitud cerrada");
        }

        // 8. Asignar responsable (dominio)
        solicitudDomain.asignarResponsable(
                UsuarioMapper.toDomain(responsableEntity)
        );

        // 9. Actualizar entity
        solicitudEntity.setResponsableAsignado(responsableEntity);
        solicitudRepository.save(solicitudEntity);

        // 10. Crear historial
        HistorialSolicitud historialDomain = HistorialSolicitud.crear(
                "ASIGNACION_RESPONSABLE",
                "Solicitud asignada al responsable con id: " + responsableId,
                UsuarioMapper.toDomain(usuario),
        Solicitud solicitudDomain = SolicitudMapper.toDomain(solicitudEntity);

        solicitudDomain.cerrar(request.getObservacionCierre());

        solicitudEntity.setEstado(solicitudDomain.getEstado());
        solicitudEntity.setObservacionCierre(solicitudDomain.getObservacionCierre());

        solicitudRepository.save(solicitudEntity);

        HistorialSolicitud historialDomain = HistorialSolicitud.crear(
                "CIERRE_SOLICITUD",
                request.getObservacionCierre(),
                UsuarioMapper.toDomain(solicitudEntity.getSolicitante()),
                solicitudDomain
        );

        HistorialSolicitudEntity historialEntity = HistorialSolicitudMapper.toEntity(historialDomain);
        historialEntity.setSolicitud(solicitudEntity);
        historialEntity.setUsuarioResponsable(usuario);

        historialSolicitudRepository.save(historialEntity);

        // 11. Obtener historial actualizado
        List<HistorialSolicitudEntity> historialEntities =
                historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudId);

        // 12. Retornar response
        historialEntity.setUsuarioResponsable(solicitudEntity.getSolicitante());

        historialSolicitudRepository.save(historialEntity);

        List<HistorialSolicitudEntity> historialEntities =
                historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudId);

        return SolicitudMapper.toResponse(
                solicitudDomain,
                SolicitudMapper.toHistorialDomainList(historialEntities)
        );
    }

    public String generarResumen(Long solicitudId) {

        SolicitudEntity solicitudEntity = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una solicitud con id " + solicitudId
                ));

        List<HistorialSolicitudEntity> historial =
                historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudId);

        StringBuilder resumen = new StringBuilder();

        resumen.append("Solicitud ID: ").append(solicitudEntity.getId()).append("\n");
        resumen.append("Tipo: ").append(solicitudEntity.getTipo()).append("\n");
        resumen.append("Estado: ").append(solicitudEntity.getEstado()).append("\n");
        resumen.append("Prioridad: ").append(solicitudEntity.getPrioridad()).append("\n\n");

        resumen.append("Historial:\n");

        for (HistorialSolicitudEntity h : historial) {
            resumen.append("- ")
                    .append(h.getFechaHora())
                    .append(": ")
                    .append(h.getAccion());

            if (h.getObservaciones() != null) {
                resumen.append(" - ").append(h.getObservaciones());
            }

            resumen.append("\n");
        }
        return iaService.generarResumen(resumen.toString());
    }

    public String generarResumenSolicitud(Long solicitudId) {

        SolicitudEntity solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada"));

        List<HistorialSolicitudEntity> historial =
                historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudId);

        // Construir texto
        StringBuilder contenido = new StringBuilder();
        contenido.append("Solicitud: ").append(solicitud.getDescripcion()).append("\n");
        contenido.append("Estado: ").append(solicitud.getEstado()).append("\n");

        for (HistorialSolicitudEntity h : historial) {
            contenido.append("- ")
                    .append(h.getAccion());

            if (h.getObservaciones() != null) {
                contenido.append(" - ").append(h.getObservaciones());
            }

            contenido.append("\n");
        }

        // Enviar a IA
        return iaService.generarResumen(contenido.toString());
    public List<SolicitudResponse> listarSolicitudes(
            EstadoSolicitud estado,
            TipoSolicitud tipo,
            Prioridad prioridad,
            Long responsableId) {

        List<SolicitudEntity> solicitudes;

        // Caso 1: sin filtros
        if (estado == null && tipo == null && prioridad == null && responsableId == null) {
            solicitudes = solicitudRepository.findAll();
        }
        // Caso 2: estado y tipo
        else if (estado != null && tipo != null) {
            solicitudes = solicitudRepository.findByEstadoAndTipo(estado, tipo);
        }
        // Caso 3: solo estado
        else if (estado != null) {
            solicitudes = solicitudRepository.findByEstado(estado);
        }
        // Caso 4: solo tipo
        else if (tipo != null) {
            solicitudes = solicitudRepository.findByTipo(tipo);
        }
        // Caso 5: solo prioridad
        else if (prioridad != null) {
            solicitudes = solicitudRepository.findByPrioridad(prioridad);
        }
        // Caso 6: solo responsable
        else if (responsableId != null) {
            solicitudes = solicitudRepository.findByResponsableAsignadoId(responsableId);
        }
        else {
            solicitudes = solicitudRepository.findAll();
        }

        List<SolicitudResponse> response = new ArrayList<>();

        for (SolicitudEntity entity : solicitudes) {
            List<HistorialSolicitudEntity> historialEntities =
                    historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(entity.getId());

            Solicitud solicitudDomain = SolicitudMapper.toDomain(entity);

            response.add(
                    SolicitudMapper.toResponse(
                            solicitudDomain,
                            SolicitudMapper.toHistorialDomainList(historialEntities)
                    )
            );
        }

        return response;
    }

    public SolicitudResponse obtenerSolicitudPorId(Long id) {
        SolicitudEntity solicitudEntity = solicitudRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una solicitud con id " + id
                ));

        List<HistorialSolicitudEntity> historialEntities =
                historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(id);

        Solicitud solicitudDomain = SolicitudMapper.toDomain(solicitudEntity);

        return SolicitudMapper.toResponse(
                solicitudDomain,
                SolicitudMapper.toHistorialDomainList(historialEntities)
        );
    }
}