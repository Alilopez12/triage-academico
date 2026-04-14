package co.edu.uniquindio.triage.repository;

import co.edu.uniquindio.triage.domain.entity.HistorialSolicitudEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialSolicitudRepository extends JpaRepository<HistorialSolicitudEntity, Long> {

    List<HistorialSolicitudEntity> findBySolicitudIdOrderByFechaHoraAsc(Long solicitudId);
}