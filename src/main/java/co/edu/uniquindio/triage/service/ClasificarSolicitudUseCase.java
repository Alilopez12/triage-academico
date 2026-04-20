package co.edu.uniquindio.triage.service;

import co.edu.uniquindio.triage.dto.request.ClasificarSolicitudRequest;
import co.edu.uniquindio.triage.dto.response.SolicitudResponse;

public interface ClasificarSolicitudUseCase {

    SolicitudResponse ejecutar(Long solicitudId, Long usuarioId, ClasificarSolicitudRequest request);
}