package co.edu.uniquindio.triage.service.impl;

import co.edu.uniquindio.triage.domain.entity.HistorialSolicitudEntity;
import co.edu.uniquindio.triage.domain.entity.SolicitudEntity;
import co.edu.uniquindio.triage.domain.entity.UsuarioEntity;
import co.edu.uniquindio.triage.domain.model.HistorialSolicitud;
import co.edu.uniquindio.triage.domain.model.Solicitud;
import co.edu.uniquindio.triage.domain.model.Usuario;
import co.edu.uniquindio.triage.dto.request.SolicitudCreateRequest;
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
}