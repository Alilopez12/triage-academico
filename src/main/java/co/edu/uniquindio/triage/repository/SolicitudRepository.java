package co.edu.uniquindio.triage.repository;

import co.edu.uniquindio.triage.domain.entity.SolicitudEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudRepository extends JpaRepository<SolicitudEntity, Long>, JpaSpecificationExecutor<SolicitudEntity> {
}