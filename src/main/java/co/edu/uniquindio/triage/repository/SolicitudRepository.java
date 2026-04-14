package co.edu.uniquindio.triage.repository;

import co.edu.uniquindio.triage.domain.entity.SolicitudEntity;
import co.edu.uniquindio.triage.domain.enums.EstadoSolicitud;
import co.edu.uniquindio.triage.domain.enums.Prioridad;
import co.edu.uniquindio.triage.domain.enums.TipoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudRepository extends JpaRepository<SolicitudEntity, Long> {

    List<SolicitudEntity> findByEstado(EstadoSolicitud estado);

    List<SolicitudEntity> findByTipo(TipoSolicitud tipo);

    List<SolicitudEntity> findByPrioridad(Prioridad prioridad);

    List<SolicitudEntity> findByResponsableAsignadoId(Long responsableId);

    List<SolicitudEntity> findBySolicitanteId(Long solicitanteId);

    List<SolicitudEntity> findByEstadoAndTipo(EstadoSolicitud estado, TipoSolicitud tipo);
}