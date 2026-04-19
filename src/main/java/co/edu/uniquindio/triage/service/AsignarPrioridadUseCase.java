package co.edu.uniquindio.triage.service;

import co.edu.uniquindio.triage.dto.request.AsignarPrioridadRequest;
import co.edu.uniquindio.triage.dto.response.SolicitudResponse;

public interface AsignarPrioridadUseCase {

    SolicitudResponse ejecutar(Long solicitudId, AsignarPrioridadRequest request, Long usuarioId);
}