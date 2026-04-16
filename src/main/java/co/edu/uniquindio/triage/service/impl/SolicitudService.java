package co.edu.uniquindio.triage.service.impl;

import co.edu.uniquindio.triage.domain.entity.HistorialSolicitudEntity;
import co.edu.uniquindio.triage.domain.entity.SolicitudEntity;
import co.edu.uniquindio.triage.domain.entity.UsuarioEntity;
import co.edu.uniquindio.triage.domain.enums.EstadoSolicitud;
import co.edu.uniquindio.triage.domain.enums.Prioridad;
import co.edu.uniquindio.triage.domain.enums.RolUsuario;
import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.triage.domain.model.HistorialSolicitud;
import co.edu.uniquindio.triage.domain.model.Solicitud;
import co.edu.uniquindio.triage.domain.model.Usuario;
import co.edu.uniquindio.triage.dto.request.AsignarPrioridadRequest;
import co.edu.uniquindio.triage.dto.request.AsignarResponsableRequest;
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


    private UsuarioEntity obtenerUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un usuario con id " + usuarioId
                ));
    }

    private SolicitudEntity obtenerSolicitudEntity(Long solicitudId) {
        return solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una solicitud con id " + solicitudId
                ));
    }

    private void validarAdmin(Long usuarioId, String accion) {
        UsuarioEntity usuario = obtenerUsuario(usuarioId);
        if (usuario.getRol() != RolUsuario.ADMIN) {
            throw new IllegalStateException("No autorizado para " + accion);
        }
    }

    private List<HistorialSolicitudEntity> obtenerHistorialEntities(Long solicitudId) {
        return historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudId);
    }



    public SolicitudResponse registrarSolicitud(SolicitudCreateRequest request) {

        UsuarioEntity solicitanteEntity = obtenerUsuario(request.getSolicitanteId());

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

        return SolicitudMapper.toResponse(
                solicitudGuardadaDomain,
                SolicitudMapper.toHistorialDomainList(obtenerHistorialEntities(solicitudGuardada.getId()))
        );
    }



    public SolicitudResponse clasificarSolicitud(Long solicitudId, Long usuarioId) {

        validarAdmin(usuarioId, "clasificar solicitudes");

        UsuarioEntity usuario = obtenerUsuario(usuarioId);
        SolicitudEntity solicitudEntity = obtenerSolicitudEntity(solicitudId);

        Solicitud solicitudDomain = SolicitudMapper.toDomain(solicitudEntity);

        solicitudDomain.clasificar(solicitudDomain.getTipo());

        solicitudEntity.setTipo(solicitudDomain.getTipo());
        solicitudEntity.setEstado(solicitudDomain.getEstado());

        solicitudRepository.save(solicitudEntity);

        HistorialSolicitud historialDomain = HistorialSolicitud.crear(
                "CLASIFICACION",
                "Solicitud clasificada como " + solicitudDomain.getTipo(),
                UsuarioMapper.toDomain(usuario),
                solicitudDomain
        );

        HistorialSolicitudEntity historialEntity = HistorialSolicitudMapper.toEntity(historialDomain);
        historialEntity.setSolicitud(solicitudEntity);
        historialEntity.setUsuarioResponsable(usuario);

        historialSolicitudRepository.save(historialEntity);

        return SolicitudMapper.toResponse(
                solicitudDomain,
                SolicitudMapper.toHistorialDomainList(obtenerHistorialEntities(solicitudId))
        );
    }


    public SolicitudResponse asignarPrioridad(Long solicitudId, AsignarPrioridadRequest request, Long usuarioId) {

        validarAdmin(usuarioId, "asignar prioridad");

        UsuarioEntity usuario = obtenerUsuario(usuarioId);
        SolicitudEntity solicitudEntity = obtenerSolicitudEntity(solicitudId);

        Solicitud solicitudDomain = SolicitudMapper.toDomain(solicitudEntity);

        solicitudDomain.asignarImpactoAcademico(request.getImpactoAcademico());
        solicitudDomain.asignarFechaLimite(request.getFechaLimite());
        solicitudDomain.calcularYAsignarPrioridad();

        solicitudEntity.setImpactoAcademico(solicitudDomain.getImpactoAcademico());
        solicitudEntity.setFechaLimite(solicitudDomain.getFechaLimite());
        solicitudEntity.setPrioridad(solicitudDomain.getPrioridad());
        solicitudEntity.setJustificacionPrioridad(solicitudDomain.getJustificacionPrioridad());

        solicitudRepository.save(solicitudEntity);

        HistorialSolicitud historialDomain = HistorialSolicitud.crear(
                "ASIGNACION_PRIORIDAD",
                "Se asignó prioridad automáticamente: " + solicitudDomain.getPrioridad(),
                UsuarioMapper.toDomain(usuario),
                solicitudDomain
        );

        HistorialSolicitudEntity historialEntity = HistorialSolicitudMapper.toEntity(historialDomain);
        historialEntity.setSolicitud(solicitudEntity);
        historialEntity.setUsuarioResponsable(usuario);

        historialSolicitudRepository.save(historialEntity);

        return SolicitudMapper.toResponse(
                solicitudDomain,
                SolicitudMapper.toHistorialDomainList(obtenerHistorialEntities(solicitudId))
        );
    }


    public SolicitudResponse asignarResponsable(Long solicitudId,
                                                AsignarResponsableRequest request,
                                                Long usuarioId) {

        validarAdmin(usuarioId, "asignar responsables");

        UsuarioEntity usuario = obtenerUsuario(usuarioId);
        SolicitudEntity solicitudEntity = obtenerSolicitudEntity(solicitudId);

        UsuarioEntity responsableEntity = obtenerUsuario(request.getResponsableId());

        if (!responsableEntity.isActivo()) {
            throw new IllegalStateException("El responsable no está activo");
        }

        if (responsableEntity.getRol() != RolUsuario.RESPONSABLE
                && responsableEntity.getRol() != RolUsuario.ADMIN) {
            throw new IllegalStateException("El usuario asignado no tiene rol autorizado como responsable");
        }

        Solicitud solicitudDomain = SolicitudMapper.toDomain(solicitudEntity);
        Usuario responsableDomain = UsuarioMapper.toDomain(responsableEntity);

        solicitudDomain.asignarResponsable(responsableDomain);

        solicitudEntity.setResponsableAsignado(responsableEntity);

        solicitudRepository.save(solicitudEntity);

        HistorialSolicitud historialDomain = HistorialSolicitud.crear(
                "ASIGNACION_RESPONSABLE",
                "Se asignó el responsable: " + responsableDomain.getNombre(),
                UsuarioMapper.toDomain(usuario),
                solicitudDomain
        );

        HistorialSolicitudEntity historialEntity = HistorialSolicitudMapper.toEntity(historialDomain);
        historialEntity.setSolicitud(solicitudEntity);
        historialEntity.setUsuarioResponsable(usuario);

        historialSolicitudRepository.save(historialEntity);

        return SolicitudMapper.toResponse(
                solicitudDomain,
                SolicitudMapper.toHistorialDomainList(obtenerHistorialEntities(solicitudId))
        );
    }



    public List<HistorialSolicitudResponse> obtenerHistorial(Long solicitudId) {

        if (!solicitudRepository.existsById(solicitudId)) {
            throw new RecursoNoEncontradoException(
                    "No existe una solicitud con id " + solicitudId
            );
        }

        List<HistorialSolicitudEntity> historialEntities = obtenerHistorialEntities(solicitudId);
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



    public SolicitudResponse cambiarEstado(Long solicitudId, CambiarEstadoRequest request) {

        SolicitudEntity solicitudEntity = obtenerSolicitudEntity(solicitudId);

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
        historialEntity.setUsuarioResponsable(solicitudEntity.getSolicitante());

        historialSolicitudRepository.save(historialEntity);

        return SolicitudMapper.toResponse(
                solicitudDomain,
                SolicitudMapper.toHistorialDomainList(obtenerHistorialEntities(solicitudId))
        );
    }



    public SolicitudResponse cerrarSolicitud(Long solicitudId,
                                             CerrarSolicitudRequest request,
                                             Long usuarioId) {

        validarAdmin(usuarioId, "cerrar solicitudes");

        UsuarioEntity usuario = obtenerUsuario(usuarioId);
        SolicitudEntity solicitudEntity = obtenerSolicitudEntity(solicitudId);

        Solicitud solicitudDomain = SolicitudMapper.toDomain(solicitudEntity);

        solicitudDomain.cerrar(request.getObservacionCierre());

        solicitudEntity.setEstado(solicitudDomain.getEstado());
        solicitudEntity.setObservacionCierre(solicitudDomain.getObservacionCierre());

        solicitudRepository.save(solicitudEntity);

        HistorialSolicitud historialDomain = HistorialSolicitud.crear(
                "CIERRE_SOLICITUD",
                request.getObservacionCierre(),
                UsuarioMapper.toDomain(usuario),
                solicitudDomain
        );

        HistorialSolicitudEntity historialEntity = HistorialSolicitudMapper.toEntity(historialDomain);
        historialEntity.setSolicitud(solicitudEntity);
        historialEntity.setUsuarioResponsable(usuario);

        historialSolicitudRepository.save(historialEntity);

        return SolicitudMapper.toResponse(
                solicitudDomain,
                SolicitudMapper.toHistorialDomainList(obtenerHistorialEntities(solicitudId))
        );
    }



    public String generarResumenSolicitud(Long solicitudId) {

        SolicitudEntity solicitud = obtenerSolicitudEntity(solicitudId);

        List<HistorialSolicitudEntity> historial = obtenerHistorialEntities(solicitudId);

        StringBuilder contenido = new StringBuilder();
        contenido.append("Solicitud: ").append(solicitud.getDescripcion()).append("\n");
        contenido.append("Estado: ").append(solicitud.getEstado()).append("\n");

        for (HistorialSolicitudEntity h : historial) {
            contenido.append("- ").append(h.getAccion());
            if (h.getObservaciones() != null) {
                contenido.append(" - ").append(h.getObservaciones());
            }
            contenido.append("\n");
        }

        return iaService.generarResumen(contenido.toString());
    }


    public List<SolicitudResponse> listarSolicitudes(
            EstadoSolicitud estado,
            TipoSolicitud tipo,
            Prioridad prioridad,
            Long responsableId) {

        List<SolicitudEntity> solicitudes;

        if (estado == null && tipo == null && prioridad == null && responsableId == null) {
            solicitudes = solicitudRepository.findAll();
        } else if (estado != null && tipo != null) {
            solicitudes = solicitudRepository.findByEstadoAndTipo(estado, tipo);
        } else if (estado != null) {
            solicitudes = solicitudRepository.findByEstado(estado);
        } else if (tipo != null) {
            solicitudes = solicitudRepository.findByTipo(tipo);
        } else if (prioridad != null) {
            solicitudes = solicitudRepository.findByPrioridad(prioridad);
        } else if (responsableId != null) {
            solicitudes = solicitudRepository.findByResponsableAsignadoId(responsableId);
        } else {
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
        SolicitudEntity solicitudEntity = obtenerSolicitudEntity(id);

        List<HistorialSolicitudEntity> historialEntities = obtenerHistorialEntities(id);
        Solicitud solicitudDomain = SolicitudMapper.toDomain(solicitudEntity);

        return SolicitudMapper.toResponse(
                solicitudDomain,
                SolicitudMapper.toHistorialDomainList(historialEntities)
        );
    }
}