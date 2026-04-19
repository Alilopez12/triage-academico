package co.edu.uniquindio.triage.config;

import co.edu.uniquindio.triage.domain.entity.UsuarioEntity;
import co.edu.uniquindio.triage.domain.enums.RolUsuario;
import co.edu.uniquindio.triage.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsuarios(UsuarioRepository usuarioRepository) {
        return args -> {

            if (usuarioRepository.count() == 0) {

                UsuarioEntity estudiante = new UsuarioEntity();
                estudiante.setNombre("Juan Estudiante");
                estudiante.setRol(RolUsuario.ESTUDIANTE);
                estudiante.setActivo(true);

                UsuarioEntity admin = new UsuarioEntity();
                admin.setNombre("Admin Sistema");
                admin.setRol(RolUsuario.ADMIN);
                admin.setActivo(true);

                UsuarioEntity responsable = new UsuarioEntity();
                responsable.setNombre("Responsable Academico");
                responsable.setRol(RolUsuario.RESPONSABLE);
                responsable.setActivo(true);

                usuarioRepository.save(estudiante);
                usuarioRepository.save(admin);
                usuarioRepository.save(responsable);

                System.out.println("Usuarios iniciales creados ✔");
            }
        };
    }
}