package co.edu.uniquindio.triage.service;

import co.edu.uniquindio.triage.dto.request.CambiarEstadoRequest;
import co.edu.uniquindio.triage.dto.response.SolicitudResponse;

public interface CambiarEstadoSolicitudUseCase {

    SolicitudResponse ejecutar(Long solicitudId, CambiarEstadoRequest request);
}