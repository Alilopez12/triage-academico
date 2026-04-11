package co.edu.uniquindio.triage.repository;

import co.edu.uniquindio.triage.domain.entity.UsuarioEntity;
import co.edu.uniquindio.triage.domain.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmail(String email);

    List<UsuarioEntity> findByRol(RolUsuario rol);

    List<UsuarioEntity> findByActivoTrue();

    List<UsuarioEntity> findByRolAndActivoTrue(RolUsuario rol);
}