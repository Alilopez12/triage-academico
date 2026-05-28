package co.edu.uniquindio.triage.config;

import co.edu.uniquindio.triage.domain.entity.UsuarioEntity;
import co.edu.uniquindio.triage.domain.enums.RolUsuario;
import co.edu.uniquindio.triage.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsuarios(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            if (usuarioRepository.count() == 0) {

                UsuarioEntity estudiante = new UsuarioEntity();
                estudiante.setNombre("Juan Estudiante");
                estudiante.setEmail("juan.estudiante@uqvirtual.edu.co");
                estudiante.setPassword(passwordEncoder.encode("estudiante123"));
                estudiante.setRol(RolUsuario.ESTUDIANTE);
                estudiante.setActivo(true);

                UsuarioEntity admin = new UsuarioEntity();
                admin.setNombre("Admin Sistema");
                admin.setEmail("admin.sistema@uqvirtual.edu.co");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRol(RolUsuario.ADMIN);
                admin.setActivo(true);

                UsuarioEntity responsable = new UsuarioEntity();
                responsable.setNombre("Responsable Academico");
                responsable.setEmail("responsable.academico@uqvirtual.edu.co");
                responsable.setPassword(passwordEncoder.encode("responsable123"));
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