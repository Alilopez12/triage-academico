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

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialSolicitudRepository historialSolicitudRepository;

    public SolicitudService(SolicitudRepository solicitudRepository,
                            UsuarioRepository usuarioRepository,
                            HistorialSolicitudRepository historialSolicitudRepository) {
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.historialSolicitudRepository = historialSolicitudRepository;
    }

    public SolicitudResponse registrarSolicitud(SolicitudCreateRequest request) {

        UsuarioEntity solicitanteEntity = usuarioRepository.findById(request.getSolicitanteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un usuario con id " + request.getSolicitanteId()
                ));

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
    public SolicitudResponse asignarPrioridad(Long solicitudId, AsignarPrioridadRequest request) {

        // 1. Buscar solicitud
        SolicitudEntity solicitudEntity = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una solicitud con id " + solicitudId
                ));

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

    public SolicitudResponse cambiarEstado(Long solicitudId, CambiarEstadoRequest request) {

        SolicitudEntity solicitudEntity = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una solicitud con id " + solicitudId
                ));

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

        List<HistorialSolicitudEntity> historialEntities =
                historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudId);

        return SolicitudMapper.toResponse(
                solicitudDomain,
                SolicitudMapper.toHistorialDomainList(historialEntities)
        );
    }

    public SolicitudResponse cerrarSolicitud(Long solicitudId, CerrarSolicitudRequest request) {

        SolicitudEntity solicitudEntity = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una solicitud con id " + solicitudId
                ));

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
        historialEntity.setUsuarioResponsable(solicitudEntity.getSolicitante());

        historialSolicitudRepository.save(historialEntity);

        List<HistorialSolicitudEntity> historialEntities =
                historialSolicitudRepository.findBySolicitudIdOrderByFechaHoraAsc(solicitudId);

        return SolicitudMapper.toResponse(
                solicitudDomain,
                SolicitudMapper.toHistorialDomainList(historialEntities)
        );
    }


}