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
import co.edu.uniquindio.triage.dto.request.ClasificarSolicitudRequest;
import co.edu.uniquindio.triage.dto.request.SolicitudCreateRequest;
import co.edu.uniquindio.triage.dto.response.HistorialSolicitudResponse;
import co.edu.uniquindio.triage.dto.response.PageResponse;
import co.edu.uniquindio.triage.dto.response.SolicitudResponse;
import co.edu.uniquindio.triage.exception.AutorizacionException;
import co.edu.uniquindio.triage.exception.RecursoNoEncontradoException;
import co.edu.uniquindio.triage.exception.ReglaNegocioException;
import co.edu.uniquindio.triage.mapper.HistorialSolicitudMapper;
import co.edu.uniquindio.triage.mapper.SolicitudMapper;
import co.edu.uniquindio.triage.mapper.UsuarioMapper;
import co.edu.uniquindio.triage.repository.HistorialSolicitudRepository;
import co.edu.uniquindio.triage.repository.SolicitudRepository;
import co.edu.uniquindio.triage.repository.UsuarioRepository;
import co.edu.uniquindio.triage.service.AsignarPrioridadUseCase;
import co.edu.uniquindio.triage.service.AsignarResponsableUseCase;
import co.edu.uniquindio.triage.service.CambiarEstadoSolicitudUseCase;
import co.edu.uniquindio.triage.service.CerrarSolicitudUseCase;
import co.edu.uniquindio.triage.service.ClasificarSolicitudUseCase;
import co.edu.uniquindio.triage.service.ConsultarSolicitudUseCase;
import co.edu.uniquindio.triage.service.RegistrarSolicitudUseCase;
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

@Service
@Transactional
public class SolicitudService implements
        ConsultarSolicitudUseCase,
        RegistrarSolicitudUseCase,
        ClasificarSolicitudUseCase,
        AsignarPrioridadUseCase,
        AsignarResponsableUseCase,
        CambiarEstadoSolicitudUseCase,
        CerrarSolicitudUseCase {

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

    // ===================== IMPLEMENTACIÓN DE INTERFACES =====================

    @Override
    public SolicitudResponse ejecutar(SolicitudCreateRequest request) {
        return registrarSolicitud(request);
    }

    @Override
    public SolicitudResponse ejecutar(Long solicitudId, Long usuarioId, ClasificarSolicitudRequest request) {
        return clasificarSolicitud(solicitudId, usuarioId, request);
    }

    @Override
    public SolicitudResponse ejecutar(Long solicitudId, AsignarPrioridadRequest request, Long usuarioId) {
        return asignarPrioridad(solicitudId, request, usuarioId);
    }

    @Override
    public SolicitudResponse ejecutar(Long solicitudId, AsignarResponsableRequest request, Long usuarioId) {
        return asignarResponsable(solicitudId, request, usuarioId);
    }

    @Override
    public SolicitudResponse ejecutar(Long solicitudId, CambiarEstadoRequest request) {
        return cambiarEstado(solicitudId, request);
    }

    @Override
    public SolicitudResponse ejecutar(Long solicitudId, CerrarSolicitudRequest request, Long usuarioId) {
        return cerrarSolicitud(solicitudId, request, usuarioId);
    }

    @Override
    public PageResponse<SolicitudResponse> listar(
            EstadoSolicitud estado,
            TipoSolicitud tipo,
            Prioridad prioridad,
            Long responsableId,
            int page,
            int size,
            String sortBy,
            String direction) {
        return listarSolicitudes(estado, tipo, prioridad, responsableId, page, size, sortBy, direction);
    }

    @Override
    public SolicitudResponse obtenerPorId(Long id) {
        return obtenerSolicitudPorId(id);
    }

    @Override
    public List<HistorialSolicitudResponse> obtenerHistorial(Long id) {
        return obtenerHistorialInterno(id);
    }

    // ===================== MÉTODOS INTERNOS AUXILIARES =====================

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

    // ===================== LÓGICA DE NEGOCIO =====================

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

    public SolicitudResponse clasificarSolicitud(Long solicitudId,
                                                 Long usuarioId,
                                                 ClasificarSolicitudRequest request) {

        validarAdmin(usuarioId, "clasificar solicitudes");

        UsuarioEntity usuario = obtenerUsuario(usuarioId);
        SolicitudEntity solicitudEntity = obtenerSolicitudEntity(solicitudId);

        Solicitud solicitudDomain = SolicitudMapper.toDomain(solicitudEntity);

        solicitudDomain.clasificar(request.getTipo());

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

    public List<HistorialSolicitudResponse> obtenerHistorialInterno(Long solicitudId) {

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

        if (usuario.getRol() == RolUsuario.RESPONSABLE) {
            UsuarioEntity responsableAsignado = solicitudEntity.getResponsableAsignado();

            if (responsableAsignado == null || !responsableAsignado.getId().equals(usuario.getId())) {
                throw new AutorizacionException("Solo el responsable asignado o un administrador pueden cambiar el estado de esta solicitud");
            }
        }

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

        contenido.append("Tipo: ").append(solicitud.getTipo()).append("\n");
        contenido.append("Estado: ").append(solicitud.getEstado()).append("\n");

        if (solicitud.getPrioridad() != null) {
            contenido.append("Prioridad: ").append(solicitud.getPrioridad()).append("\n");
        }

        if (solicitud.getCanalOrigen() != null) {
            contenido.append("Canal: ").append(solicitud.getCanalOrigen()).append("\n");
        }

        if (solicitud.getFechaRegistro() != null) {
            contenido.append("Fecha de registro: ").append(solicitud.getFechaRegistro()).append("\n");
        }

        if (solicitud.getResponsableAsignado() != null) {
            contenido.append("Responsable: ").append(solicitud.getResponsableAsignado().getNombre()).append("\n");
        }

        contenido.append("Descripción: ").append(solicitud.getDescripcion()).append("\n");

        if (solicitud.getObservacionCierre() != null && !solicitud.getObservacionCierre().isBlank()) {
            contenido.append("Observación de cierre: ").append(solicitud.getObservacionCierre()).append("\n");
        }

        contenido.append("\nHistorial:\n");

        for (HistorialSolicitudEntity h : historial) {
            contenido.append("- ").append(h.getAccion());

            if (h.getObservaciones() != null && !h.getObservaciones().isBlank()) {
                contenido.append(" - ").append(h.getObservaciones());
            }

            if (h.getFechaHora() != null) {
                contenido.append(" (").append(h.getFechaHora()).append(")");
            }

            contenido.append("\n");
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

        Specification<SolicitudEntity> specification = construirEspecificacion(
                estado,
                tipo,
                prioridad,
                responsableId
        );

        Page<SolicitudEntity> solicitudesPage = solicitudRepository.findAll(specification, pageable);

        List<SolicitudResponse> content = new ArrayList<>();

        for (SolicitudEntity entity : solicitudesPage.getContent()) {
            List<HistorialSolicitudEntity> historialEntities =
                    historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(entity.getId());

            Solicitud solicitudDomain = SolicitudMapper.toDomain(entity);

            content.add(
                    SolicitudMapper.toResponse(
                            solicitudDomain,
                            SolicitudMapper.toHistorialDomainList(historialEntities)
                    )
            );
        }

        return new PageResponse<>(
                content,
                solicitudesPage.getNumber(),
                solicitudesPage.getSize(),
                solicitudesPage.getTotalElements(),
                solicitudesPage.getTotalPages(),
                solicitudesPage.isFirst(),
                solicitudesPage.isLast(),
                sortBy,
                direction
        );
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