package co.edu.uniquindio.triage.service;

import co.edu.uniquindio.triage.dto.request.CerrarSolicitudRequest;
import co.edu.uniquindio.triage.dto.response.SolicitudResponse;

public interface CerrarSolicitudUseCase {

    SolicitudResponse ejecutar(Long solicitudId, CerrarSolicitudRequest request, Long usuarioId);
}