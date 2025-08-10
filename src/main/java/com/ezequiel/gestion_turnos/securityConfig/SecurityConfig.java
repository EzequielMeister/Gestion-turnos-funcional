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

        PasswordEncoder passwordEncoder = passwordEncoder();

        UserDetails paciente = User.builder()
                .username("paciente")
                .password(passwordEncoder.encode("pacientepass"))
                .roles("PACIENTE")
                .build();

        UserDetails medico = User.builder()
                .username("medico")
                .password(passwordEncoder.encode("medicopass"))
                .roles("MEDICO")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("adminpass"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(paciente, medico, admin);

    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        //Requiere login para sacar turnos
                        .requestMatchers("/sacar-turnos.html").authenticated()

                        // Permito la API sin login
                        .requestMatchers("/api/turnos/**").permitAll()

                        // Permito recursos estáticos (css, js, imágenes)
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // Para todo lo demás permito acceso libre
                        .anyRequest().permitAll()
                )
                .httpBasic();
        return http.build();
    }

}
