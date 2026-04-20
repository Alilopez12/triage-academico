package co.edu.uniquindio.triage.service;

import co.edu.uniquindio.triage.dto.request.SolicitudCreateRequest;
import co.edu.uniquindio.triage.dto.response.SolicitudResponse;

public interface RegistrarSolicitudUseCase {

    SolicitudResponse ejecutar(SolicitudCreateRequest request);
}