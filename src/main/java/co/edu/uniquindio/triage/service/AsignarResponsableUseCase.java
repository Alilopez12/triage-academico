package co.edu.uniquindio.triage.service;

import co.edu.uniquindio.triage.dto.request.AsignarResponsableRequest;
import co.edu.uniquindio.triage.dto.response.SolicitudResponse;

public interface AsignarResponsableUseCase {

    SolicitudResponse ejecutar(Long solicitudId, AsignarResponsableRequest request, Long usuarioId);
}