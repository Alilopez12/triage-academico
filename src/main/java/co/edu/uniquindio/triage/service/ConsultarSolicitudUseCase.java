package co.edu.uniquindio.triage.service;

import co.edu.uniquindio.triage.domain.enums.EstadoSolicitud;
import co.edu.uniquindio.triage.domain.enums.Prioridad;
import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;
import co.edu.uniquindio.triage.dto.response.HistorialSolicitudResponse;
import co.edu.uniquindio.triage.dto.response.PageResponse;
import co.edu.uniquindio.triage.dto.response.SolicitudResponse;

import java.util.List;

public interface ConsultarSolicitudUseCase {

    PageResponse<SolicitudResponse> listar(
            EstadoSolicitud estado,
            TipoSolicitud tipo,
            Prioridad prioridad,
            Long responsableId,
            int page,
            int size,
            String sortBy,
            String direction
    );

    SolicitudResponse obtenerPorId(Long id);

    List<HistorialSolicitudResponse> obtenerHistorial(Long id);
}