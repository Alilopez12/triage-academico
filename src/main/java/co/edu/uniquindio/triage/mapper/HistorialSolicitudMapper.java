package co.edu.uniquindio.triage.mapper;

import co.edu.uniquindio.triage.domain.entity.HistorialSolicitudEntity;
import co.edu.uniquindio.triage.domain.model.HistorialSolicitud;
import co.edu.uniquindio.triage.dto.response.HistorialSolicitudResponse;

public class HistorialSolicitudMapper {

    private HistorialSolicitudMapper() {
    }

    public static HistorialSolicitud toDomain(HistorialSolicitudEntity entity) {
        if (entity == null) {
            return null;
        }

        HistorialSolicitud historial = new HistorialSolicitud();
        historial.setId(entity.getId());
        historial.setFechaHora(entity.getFechaHora());
        historial.setAccion(entity.getAccion());
        historial.setObservaciones(entity.getObservaciones());
        historial.setUsuarioResponsable(UsuarioMapper.toDomain(entity.getUsuarioResponsable()));
        historial.setSolicitud(null);

        return historial;
    }

    public static HistorialSolicitudEntity toEntity(HistorialSolicitud domain) {
        if (domain == null) {
            return null;
        }

        HistorialSolicitudEntity entity = new HistorialSolicitudEntity();
        entity.setId(domain.getId());
        entity.setFechaHora(domain.getFechaHora());
        entity.setAccion(domain.getAccion());
        entity.setObservaciones(domain.getObservaciones());
        entity.setUsuarioResponsable(UsuarioMapper.toEntity(domain.getUsuarioResponsable()));

        return entity;
    }

    public static HistorialSolicitudResponse toResponse(HistorialSolicitud domain) {
        if (domain == null) {
            return null;
        }

        HistorialSolicitudResponse response = new HistorialSolicitudResponse();
        response.setId(domain.getId());
        response.setFechaHora(domain.getFechaHora());
        response.setAccion(domain.getAccion());
        response.setObservaciones(domain.getObservaciones());

        if (domain.getUsuarioResponsable() != null) {
            response.setUsuarioResponsableId(domain.getUsuarioResponsable().getId());
            response.setNombreUsuarioResponsable(domain.getUsuarioResponsable().getNombre());
        }

        return response;
    }
}