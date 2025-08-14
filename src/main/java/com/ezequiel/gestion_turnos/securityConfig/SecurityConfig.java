package com.ezequiel.gestion_turnos.securityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig {

    // Encoder de contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Usuarios en memoria
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        PasswordEncoder encoder = passwordEncoder();

        UserDetails paciente = User.builder()
                .username("paciente")
                .password(encoder.encode("pacientepass"))
                .roles("PACIENTE")
                .build();

        UserDetails medico = User.builder()
                .username("medico")
                .password(encoder.encode("medicopass"))
                .roles("MEDICO")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("adminpass"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(paciente, medico, admin);
    }

    // Configuración de seguridad
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()                // Permite CORS
                .csrf().disable()            // Desactiva CSRF para fetch
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/sacar-turnos.html", "/lista-turnos.html")
                        .authenticated()   // Solo usuarios logueados pueden acceder a estas páginas
                        .requestMatchers("/api/turnos/**", "/api/me").authenticated() // fetch requiere sesión
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login.html")          // Página de login personalizada
                        .loginProcessingUrl("/login")      // URL de procesamiento del login
                        .defaultSuccessUrl("/sacar-turnos.html", true) // Redirección post-login
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login.html")
                        .permitAll()
                );

        return http.build();
    }

    // Configuración de CORS (para fetch desde el mismo origen o distinto)
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowCredentials(true) // Permite enviar cookies
                        .allowedOrigins("http://localhost:8080") // tu front si es otro puerto
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
            }
        };
    }
}
