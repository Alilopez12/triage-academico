package co.edu.uniquindio.triage.mapper;

import co.edu.uniquindio.triage.domain.entity.UsuarioEntity;
import co.edu.uniquindio.triage.domain.model.Usuario;

public class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Usuario(
                entity.getId(),
                entity.getNombre(),
                entity.getEmail(),
                entity.isActivo(),
                entity.getRol()
        );
    }

    public static UsuarioEntity toEntity(Usuario domain) {
        if (domain == null) {
            return null;
        }

        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setEmail(domain.getEmail());
        entity.setActivo(domain.isActivo());
        entity.setRol(domain.getRol());

        return entity;
    }
}