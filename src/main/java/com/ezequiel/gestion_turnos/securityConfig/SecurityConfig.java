package com.ezequiel.gestion_turnos.securityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Desactiva la seguridad por defecto de Spring Security.
// Permite acceder a cualquier endpoint sin login, sin token, sin usuario.
// Es útil para pruebas locales, Postman, y cuando no tenés frontend todavía.
// Si no lo hacías, Spring mostraría una pantalla de login al entrar por navegador.
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .httpBasic().disable();
        return http.build();
    }


}
