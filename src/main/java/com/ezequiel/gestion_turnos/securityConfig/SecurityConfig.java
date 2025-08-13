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

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        // Solo usuarios autenticados pueden acceder a sacar-turnos.html
                        .requestMatchers("/sacar-turnos.html").authenticated()

                        // Rutas de la API que pueden ser públicas
                        .requestMatchers("/api/turnos/**").permitAll()

                        // Recursos estáticos
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login.html")          // Página de login personalizada
                        .loginProcessingUrl("/login")      // URL a la que envía el formulario POST
                        .defaultSuccessUrl("/sacar-turnos.html", true) // Redirección al login exitoso
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login.html")
                        .permitAll()
                );

        return http.build();
    }
}
