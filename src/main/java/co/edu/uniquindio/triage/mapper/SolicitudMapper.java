package co.edu.uniquindio.triage.mapper;

import co.edu.uniquindio.triage.domain.entity.HistorialSolicitudEntity;
import co.edu.uniquindio.triage.domain.entity.SolicitudEntity;
import co.edu.uniquindio.triage.domain.model.HistorialSolicitud;
import co.edu.uniquindio.triage.domain.model.Solicitud;
import co.edu.uniquindio.triage.dto.response.HistorialSolicitudResponse;
import co.edu.uniquindio.triage.dto.response.SolicitudResponse;

import java.util.ArrayList;
import java.util.List;

public class SolicitudMapper {

    private SolicitudMapper() {
    }

    public static Solicitud toDomain(SolicitudEntity entity) {
        if (entity == null) {
            return null;
        }

        Solicitud solicitud = new Solicitud();
        solicitud.setId(entity.getId());
        solicitud.setTipo(entity.getTipo());
        solicitud.setDescripcion(entity.getDescripcion());
        solicitud.setCanalOrigen(entity.getCanalOrigen());
        solicitud.setFechaRegistro(entity.getFechaRegistro());
        solicitud.setPrioridad(entity.getPrioridad());
        solicitud.setJustificacionPrioridad(entity.getJustificacionPrioridad());
        solicitud.setEstado(entity.getEstado());
        solicitud.setImpactoAcademico(entity.getImpactoAcademico());
        solicitud.setObservacionCierre(entity.getObservacionCierre());
        solicitud.setSolicitante(UsuarioMapper.toDomain(entity.getSolicitante()));
        solicitud.setResponsableAsignado(UsuarioMapper.toDomain(entity.getResponsableAsignado()));

        return solicitud;
    }

    public static SolicitudEntity toEntity(Solicitud domain) {
        if (domain == null) {
            return null;
        }

        SolicitudEntity entity = new SolicitudEntity();
        entity.setId(domain.getId());
        entity.setTipo(domain.getTipo());
        entity.setDescripcion(domain.getDescripcion());
        entity.setCanalOrigen(domain.getCanalOrigen());
        entity.setFechaRegistro(domain.getFechaRegistro());
        entity.setPrioridad(domain.getPrioridad());
        entity.setJustificacionPrioridad(domain.getJustificacionPrioridad());
        entity.setEstado(domain.getEstado());
        entity.setImpactoAcademico(domain.getImpactoAcademico());
        entity.setObservacionCierre(domain.getObservacionCierre());
        entity.setSolicitante(UsuarioMapper.toEntity(domain.getSolicitante()));
        entity.setResponsableAsignado(UsuarioMapper.toEntity(domain.getResponsableAsignado()));

        return entity;
    }

    public static SolicitudResponse toResponse(Solicitud domain, List<HistorialSolicitud> historialDomain) {
        if (domain == null) {
            return null;
        }

        SolicitudResponse response = new SolicitudResponse();
        response.setId(domain.getId());
        response.setTipo(domain.getTipo());
        response.setDescripcion(domain.getDescripcion());
        response.setCanalOrigen(domain.getCanalOrigen());
        response.setFechaRegistro(domain.getFechaRegistro());
        response.setPrioridad(domain.getPrioridad());
        response.setJustificacionPrioridad(domain.getJustificacionPrioridad());
        response.setEstado(domain.getEstado());
        response.setImpactoAcademico(domain.getImpactoAcademico());
        response.setObservacionCierre(domain.getObservacionCierre());

        if (domain.getSolicitante() != null) {
            response.setSolicitanteId(domain.getSolicitante().getId());
            response.setNombreSolicitante(domain.getSolicitante().getNombre());
        }

        if (domain.getResponsableAsignado() != null) {
            response.setResponsableAsignadoId(domain.getResponsableAsignado().getId());
            response.setNombreResponsableAsignado(domain.getResponsableAsignado().getNombre());
        }

        List<HistorialSolicitudResponse> historialResponse = new ArrayList<>();
        if (historialDomain != null) {
            for (HistorialSolicitud historial : historialDomain) {
                historialResponse.add(HistorialSolicitudMapper.toResponse(historial));
            }
        }

        response.setHistorial(historialResponse);

        return response;
    }

    public static List<HistorialSolicitud> toHistorialDomainList(List<HistorialSolicitudEntity> historialEntities) {
        List<HistorialSolicitud> historial = new ArrayList<>();

        if (historialEntities == null) {
            return historial;
        }

        for (HistorialSolicitudEntity entity : historialEntities) {
            historial.add(HistorialSolicitudMapper.toDomain(entity));
        }

        return historial;
    }
}