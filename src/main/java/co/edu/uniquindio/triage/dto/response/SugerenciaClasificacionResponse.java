package co.edu.uniquindio.triage.dto.response;

import co.edu.uniquindio.triage.domain.enums.Prioridad;
import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;

public class SugerenciaClasificacionResponse {

    private TipoSolicitud tipoSugerido;
    private Prioridad prioridadSugerida;
    private String justificacion;

    public SugerenciaClasificacionResponse() {
    }

    public SugerenciaClasificacionResponse(TipoSolicitud tipoSugerido,
                                           Prioridad prioridadSugerida,
                                           String justificacion) {
        this.tipoSugerido = tipoSugerido;
        this.prioridadSugerida = prioridadSugerida;
        this.justificacion = justificacion;
    }

    public TipoSolicitud getTipoSugerido() {
        return tipoSugerido;
    }

    public void setTipoSugerido(TipoSolicitud tipoSugerido) {
        this.tipoSugerido = tipoSugerido;
    }

    public Prioridad getPrioridadSugerida() {
        return prioridadSugerida;
    }

    public void setPrioridadSugerida(Prioridad prioridadSugerida) {
        this.prioridadSugerida = prioridadSugerida;
    }

    public String getJustificacion() {
        return justificacion;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }
}