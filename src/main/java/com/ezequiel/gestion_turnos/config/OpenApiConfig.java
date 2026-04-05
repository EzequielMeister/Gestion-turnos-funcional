package com.ezequiel.gestion_turnos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Sistema de Gestión de Turnos Médicos")
                        .version("1.0.0")
                        .description("""
                                API REST para el sistema de gestión de turnos médicos del Centro Médico Salud.
                                
                                ## Funcionalidades principales:
                                - **Pacientes**: Solicitar, confirmar y cancelar turnos
                                - **Médicos**: Ver su agenda y gestionar estados de turnos
                                - **Administradores**: Gestión completa del sistema
                                
                                ## Autenticación
                                Utilice el endpoint `/login` con credenciales válidas.
                                
                                ## Roles
                                - `ROLE_ADMIN`: Acceso completo
                                - `ROLE_MEDICO`: Agenda y gestión de turnos
                                - `ROLE_PACIENTE`: Solicitud de turnos
                                """)
                        .contact(new Contact()
                                .name("Ezequiel Meister")
                                .email("ezee7771@gmail.com")
                                .url("https://github.com/EzequielMeister"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor de desarrollo"),
                        new Server()
                                .url("https://api.centromedico.com")
                                .description("Servidor de producción")
                ));
    }
}
