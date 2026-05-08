package co.edu.uniquindio.triage.service;

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
import co.edu.uniquindio.triage.dto.request.ClasificarSolicitudRequest;
import co.edu.uniquindio.triage.dto.request.SolicitudCreateRequest;
import co.edu.uniquindio.triage.dto.response.HistorialSolicitudResponse;
import co.edu.uniquindio.triage.dto.response.PageResponse;
import co.edu.uniquindio.triage.dto.response.SolicitudResponse;
import co.edu.uniquindio.triage.exception.AutorizacionException;
import co.edu.uniquindio.triage.exception.ConcurrenciaException;
import co.edu.uniquindio.triage.exception.RecursoNoEncontradoException;
import co.edu.uniquindio.triage.exception.ReglaNegocioException;
import co.edu.uniquindio.triage.mapper.HistorialSolicitudMapper;
import co.edu.uniquindio.triage.mapper.SolicitudMapper;
import co.edu.uniquindio.triage.mapper.UsuarioMapper;
import co.edu.uniquindio.triage.repository.HistorialSolicitudRepository;
import co.edu.uniquindio.triage.repository.SolicitudRepository;
import co.edu.uniquindio.triage.repository.UsuarioRepository;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class SolicitudService{

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

    private void validarVersion(Long versionEnviada, SolicitudEntity solicitudEntity) {
        if (versionEnviada == null) {
            throw new ReglaNegocioException("La versión es obligatoria para modificar la solicitud.");
        }

        if (solicitudEntity.getVersion() == null) {
            throw new ConcurrenciaException("La solicitud no tiene una versión válida de concurrencia.");
        }

        if (!solicitudEntity.getVersion().equals(versionEnviada)) {
            throw new ConcurrenciaException(
                    "Conflicto de concurrencia: la solicitud fue modificada por otro usuario. " +
                            "Versión actual=" + solicitudEntity.getVersion() +
                            ", versión enviada=" + versionEnviada
            );
        }
    }

    private void validarAdmin(Long usuarioId, String accion) {
        UsuarioEntity usuario = obtenerUsuario(usuarioId);
        if (usuario.getRol() != RolUsuario.ADMIN) {
            throw new AutorizacionException("No autorizado para " + accion);
        }
    }

    private void validarAdminOResponsable(Long usuarioId, String accion) {
        UsuarioEntity usuario = obtenerUsuario(usuarioId);

        if (usuario.getRol() != RolUsuario.ADMIN && usuario.getRol() != RolUsuario.RESPONSABLE) {
            throw new AutorizacionException("No autorizado para " + accion);
        }

        if (!usuario.isActivo()) {
            throw new AutorizacionException("El usuario no se encuentra activo para " + accion);
        }
    }

    private List<HistorialSolicitudEntity> obtenerHistorialEntities(Long solicitudId) {
        return historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudId);
    }

    private Specification<SolicitudEntity> construirEspecificacion(
            EstadoSolicitud estado,
            TipoSolicitud tipo,
            Prioridad prioridad,
            Long responsableId) {

        Specification<SolicitudEntity> specification = Specification.where(null);

        if (estado != null) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get("estado"), estado));
        }

        if (tipo != null) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get("tipo"), tipo));
        }

        if (prioridad != null) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get("prioridad"), prioridad));
        }

        if (responsableId != null) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.join("responsableAsignado", JoinType.LEFT).get("id"), responsableId));
        }

        return specification;
    }

    private void validarPaginacion(int page, int size) {
        if (page < 0) {
            throw new ReglaNegocioException("El parámetro page no puede ser negativo.");
        }

        if (size <= 0) {
            throw new ReglaNegocioException("El parámetro size debe ser mayor que cero.");
        }

        if (size > 100) {
            throw new ReglaNegocioException("El parámetro size no puede ser mayor a 100.");
        }
    }

    private Sort construirSort(String sortBy, String direction) {
        List<String> camposPermitidos = List.of("id", "fechaRegistro", "estado", "tipo", "prioridad");

        if (!camposPermitidos.contains(sortBy)) {
            throw new ReglaNegocioException("El campo sortBy no es válido.");
        }

        if (!"asc".equalsIgnoreCase(direction) && !"desc".equalsIgnoreCase(direction)) {
            throw new ReglaNegocioException("La dirección de ordenamiento debe ser 'asc' o 'desc'.");
        }

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        return Sort.by(sortDirection, sortBy);
    }

    public SolicitudResponse registrarSolicitud(SolicitudCreateRequest request) {
        UsuarioEntity solicitanteEntity = obtenerUsuario(request.getSolicitanteId());

        if (solicitanteEntity.getRol() != RolUsuario.ESTUDIANTE) {
            throw new AutorizacionException("Solo los estudiantes pueden registrar solicitudes");
        }

        if (!solicitanteEntity.isActivo()) {
            throw new AutorizacionException("El solicitante no se encuentra activo");
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

        SolicitudEntity solicitudGuardada = solicitudRepository.saveAndFlush(solicitudEntity);
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

    public SolicitudResponse clasificarSolicitud(Long solicitudId,
                                                 Long usuarioId,
                                                 ClasificarSolicitudRequest request) {

        validarAdmin(usuarioId, "clasificar solicitudes");

        UsuarioEntity usuario = obtenerUsuario(usuarioId);
        SolicitudEntity solicitudEntity = obtenerSolicitudEntity(solicitudId);

        validarVersion(request.getVersion(), solicitudEntity);

        Solicitud solicitudDomain = SolicitudMapper.toDomain(solicitudEntity);
        solicitudDomain.clasificar(request.getTipo());

        solicitudEntity.setTipo(solicitudDomain.getTipo());
        solicitudEntity.setEstado(solicitudDomain.getEstado());

        SolicitudEntity solicitudActualizada = solicitudRepository.saveAndFlush(solicitudEntity);
        Solicitud solicitudActualizadaDomain = SolicitudMapper.toDomain(solicitudActualizada);

        HistorialSolicitud historialDomain = HistorialSolicitud.crear(
                "CLASIFICACION",
                "Solicitud clasificada como " + solicitudActualizadaDomain.getTipo(),
                UsuarioMapper.toDomain(usuario),
                solicitudActualizadaDomain
        );

        HistorialSolicitudEntity historialEntity = HistorialSolicitudMapper.toEntity(historialDomain);
        historialEntity.setSolicitud(solicitudActualizada);
        historialEntity.setUsuarioResponsable(usuario);

        historialSolicitudRepository.save(historialEntity);

        return SolicitudMapper.toResponse(
                solicitudActualizadaDomain,
                SolicitudMapper.toHistorialDomainList(obtenerHistorialEntities(solicitudId))
        );
    }

    public SolicitudResponse asignarPrioridad(Long solicitudId, AsignarPrioridadRequest request, Long usuarioId) {
        validarAdmin(usuarioId, "asignar prioridad");

        UsuarioEntity usuario = obtenerUsuario(usuarioId);
        SolicitudEntity solicitudEntity = obtenerSolicitudEntity(solicitudId);

        validarVersion(request.getVersion(), solicitudEntity);

        Solicitud solicitudDomain = SolicitudMapper.toDomain(solicitudEntity);
        solicitudDomain.asignarImpactoAcademico(request.getImpactoAcademico());
        solicitudDomain.asignarFechaLimite(request.getFechaLimite());
        solicitudDomain.calcularYAsignarPrioridad();

        solicitudEntity.setImpactoAcademico(solicitudDomain.getImpactoAcademico());
        solicitudEntity.setFechaLimite(solicitudDomain.getFechaLimite());
        solicitudEntity.setPrioridad(solicitudDomain.getPrioridad());
        solicitudEntity.setJustificacionPrioridad(solicitudDomain.getJustificacionPrioridad());

        SolicitudEntity solicitudActualizada = solicitudRepository.saveAndFlush(solicitudEntity);
        Solicitud solicitudActualizadaDomain = SolicitudMapper.toDomain(solicitudActualizada);

        HistorialSolicitud historialDomain = HistorialSolicitud.crear(
                "ASIGNACION_PRIORIDAD",
                "Se asignó prioridad automáticamente: " + solicitudActualizadaDomain.getPrioridad(),
                UsuarioMapper.toDomain(usuario),
                solicitudActualizadaDomain
        );

        HistorialSolicitudEntity historialEntity = HistorialSolicitudMapper.toEntity(historialDomain);
        historialEntity.setSolicitud(solicitudActualizada);
        historialEntity.setUsuarioResponsable(usuario);

        historialSolicitudRepository.save(historialEntity);

        return SolicitudMapper.toResponse(
                solicitudActualizadaDomain,
                SolicitudMapper.toHistorialDomainList(obtenerHistorialEntities(solicitudId))
        );
    }

    public SolicitudResponse asignarResponsable(Long solicitudId,
                                                AsignarResponsableRequest request,
                                                Long usuarioId) {

        validarAdmin(usuarioId, "asignar responsables");

        UsuarioEntity usuario = obtenerUsuario(usuarioId);
        SolicitudEntity solicitudEntity = obtenerSolicitudEntity(solicitudId);

        validarVersion(request.getVersion(), solicitudEntity);

        UsuarioEntity responsableEntity = obtenerUsuario(request.getResponsableId());

        if (!responsableEntity.isActivo()) {
            throw new ReglaNegocioException("El responsable no está activo");
        }

        if (responsableEntity.getRol() != RolUsuario.RESPONSABLE
                && responsableEntity.getRol() != RolUsuario.ADMIN) {
            throw new ReglaNegocioException("El usuario asignado no tiene rol autorizado como responsable");
        }

        Solicitud solicitudDomain = SolicitudMapper.toDomain(solicitudEntity);
        Usuario responsableDomain = UsuarioMapper.toDomain(responsableEntity);

        solicitudDomain.asignarResponsable(responsableDomain);
        solicitudEntity.setResponsableAsignado(responsableEntity);

        SolicitudEntity solicitudActualizada = solicitudRepository.saveAndFlush(solicitudEntity);
        Solicitud solicitudActualizadaDomain = SolicitudMapper.toDomain(solicitudActualizada);

        HistorialSolicitud historialDomain = HistorialSolicitud.crear(
                "ASIGNACION_RESPONSABLE",
                "Se asignó el responsable: " + responsableDomain.getNombre(),
                UsuarioMapper.toDomain(usuario),
                solicitudActualizadaDomain
        );

        HistorialSolicitudEntity historialEntity = HistorialSolicitudMapper.toEntity(historialDomain);
        historialEntity.setSolicitud(solicitudActualizada);
        historialEntity.setUsuarioResponsable(usuario);

        historialSolicitudRepository.save(historialEntity);

        return SolicitudMapper.toResponse(
                solicitudActualizadaDomain,
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
        validarAdminOResponsable(request.getUsuarioId(), "cambiar el estado de solicitudes");

        SolicitudEntity solicitudEntity = obtenerSolicitudEntity(solicitudId);
        UsuarioEntity usuario = obtenerUsuario(request.getUsuarioId());

        validarVersion(request.getVersion(), solicitudEntity);

        if (usuario.getRol() == RolUsuario.RESPONSABLE) {
            UsuarioEntity responsableAsignado = solicitudEntity.getResponsableAsignado();

            if (responsableAsignado == null || !responsableAsignado.getId().equals(usuario.getId())) {
                throw new AutorizacionException("Solo el responsable asignado o un administrador pueden cambiar el estado de esta solicitud");
            }
        }

        Solicitud solicitudDomain = SolicitudMapper.toDomain(solicitudEntity);
        solicitudDomain.cambiarEstado(request.getNuevoEstado());

        solicitudEntity.setEstado(solicitudDomain.getEstado());

        SolicitudEntity solicitudActualizada = solicitudRepository.saveAndFlush(solicitudEntity);
        Solicitud solicitudActualizadaDomain = SolicitudMapper.toDomain(solicitudActualizada);

        String observacionHistorial = request.getObservacion();
        if (observacionHistorial == null || observacionHistorial.isBlank()) {
            observacionHistorial = "Cambio de estado a " + solicitudActualizadaDomain.getEstado();
        }

        HistorialSolicitud historialDomain = HistorialSolicitud.crear(
                "CAMBIO_ESTADO",
                observacionHistorial,
                UsuarioMapper.toDomain(usuario),
                solicitudActualizadaDomain
        );

        HistorialSolicitudEntity historialEntity = HistorialSolicitudMapper.toEntity(historialDomain);
        historialEntity.setSolicitud(solicitudActualizada);
        historialEntity.setUsuarioResponsable(usuario);

        historialSolicitudRepository.save(historialEntity);

        return SolicitudMapper.toResponse(
                solicitudActualizadaDomain,
                SolicitudMapper.toHistorialDomainList(obtenerHistorialEntities(solicitudId))
        );
    }

    public SolicitudResponse cerrarSolicitud(Long solicitudId,
                                             CerrarSolicitudRequest request,
                                             Long usuarioId) {

        validarAdmin(usuarioId, "cerrar solicitudes");

        UsuarioEntity usuario = obtenerUsuario(usuarioId);
        SolicitudEntity solicitudEntity = obtenerSolicitudEntity(solicitudId);

        validarVersion(request.getVersion(), solicitudEntity);

        Solicitud solicitudDomain = SolicitudMapper.toDomain(solicitudEntity);
        solicitudDomain.cerrar(request.getObservacionCierre());

        solicitudEntity.setEstado(solicitudDomain.getEstado());
        solicitudEntity.setObservacionCierre(solicitudDomain.getObservacionCierre());

        SolicitudEntity solicitudActualizada = solicitudRepository.saveAndFlush(solicitudEntity);
        Solicitud solicitudActualizadaDomain = SolicitudMapper.toDomain(solicitudActualizada);

        HistorialSolicitud historialDomain = HistorialSolicitud.crear(
                "CIERRE_SOLICITUD",
                request.getObservacionCierre(),
                UsuarioMapper.toDomain(usuario),
                solicitudActualizadaDomain
        );

        HistorialSolicitudEntity historialEntity = HistorialSolicitudMapper.toEntity(historialDomain);
        historialEntity.setSolicitud(solicitudActualizada);
        historialEntity.setUsuarioResponsable(usuario);

        historialSolicitudRepository.save(historialEntity);

        return SolicitudMapper.toResponse(
                solicitudActualizadaDomain,
                SolicitudMapper.toHistorialDomainList(obtenerHistorialEntities(solicitudId))
        );
    }

    public String generarResumenSolicitud(Long solicitudId) {
        SolicitudEntity solicitud = obtenerSolicitudEntity(solicitudId);
        List<HistorialSolicitudEntity> historial = obtenerHistorialEntities(solicitudId);

        StringBuilder contenido = new StringBuilder();

        contenido.append("Tipo: ").append(solicitud.getTipo()).append("\n");
        contenido.append("Estado: ").append(solicitud.getEstado()).append("\n");

        if (solicitud.getPrioridad() != null) {
            contenido.append("Prioridad: ").append(solicitud.getPrioridad()).append("\n");
        }

        if (solicitud.getJustificacionPrioridad() != null) {
            contenido.append("Justificación prioridad: ").append(solicitud.getJustificacionPrioridad()).append("\n");
        }

        contenido.append("Descripción: ").append(solicitud.getDescripcion()).append("\n");
        contenido.append("Solicitante: ").append(solicitud.getSolicitante().getNombre()).append("\n");

        if (solicitud.getResponsableAsignado() != null) {
            contenido.append("Responsable: ").append(solicitud.getResponsableAsignado().getNombre()).append("\n");
        }

        contenido.append("\nHistorial:\n");
        for (HistorialSolicitudEntity h : historial) {
            contenido.append("- ")
                    .append(h.getFechaHora())
                    .append(" | ")
                    .append(h.getAccion())
                    .append(" | ")
                    .append(h.getObservaciones())
                    .append("\n");
        }

        return iaService.generarResumen(contenido.toString());
    }

    public PageResponse<SolicitudResponse> listarSolicitudes(
            EstadoSolicitud estado,
            TipoSolicitud tipo,
            Prioridad prioridad,
            Long responsableId,
            int page,
            int size,
            String sortBy,
            String direction) {

        validarPaginacion(page, size);
        Sort sort = construirSort(sortBy, direction);

        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<SolicitudEntity> specification = construirEspecificacion(estado, tipo, prioridad, responsableId);

        Page<SolicitudEntity> resultPage = solicitudRepository.findAll(specification, pageable);

        List<SolicitudResponse> content = resultPage.getContent().stream()
                .map(entity -> SolicitudMapper.toResponse(
                        SolicitudMapper.toDomain(entity),
                        SolicitudMapper.toHistorialDomainList(obtenerHistorialEntities(entity.getId()))
                ))
                .toList();

        PageResponse<SolicitudResponse> response = new PageResponse<>();
        response.setContent(content);
        response.setPage(resultPage.getNumber());
        response.setSize(resultPage.getSize());
        response.setTotalElements(resultPage.getTotalElements());
        response.setTotalPages(resultPage.getTotalPages());
        response.setFirst(resultPage.isFirst());
        response.setLast(resultPage.isLast());
        response.setSortBy(sortBy);
        response.setDirection(direction);

        return response;
    }

    public SolicitudResponse obtenerSolicitudPorId(Long id) {
        SolicitudEntity solicitudEntity = obtenerSolicitudEntity(id);

        return SolicitudMapper.toResponse(
                SolicitudMapper.toDomain(solicitudEntity),
                SolicitudMapper.toHistorialDomainList(obtenerHistorialEntities(id))
        );
    }
}