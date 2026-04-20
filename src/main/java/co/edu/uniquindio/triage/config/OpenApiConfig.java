package co.edu.uniquindio.triage.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI triageOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Sistema de Triage y Gestión de Solicitudes Académicas")
                        .description("API REST para registrar, clasificar, priorizar, asignar, consultar y cerrar solicitudes académicas.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo de desarrollo")
                                .email("allison@example.com"))
                        .license(new License()
                                .name("Uso académico")
                                .url("https://uniquindio.edu.co")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentación del proyecto")
                        .url("https://uniquindio.edu.co"));
    }
}