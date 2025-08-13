package com.ezequiel.gestion_turnos.controller;

import com.ezequiel.gestion_turnos.model.Turno;
import com.ezequiel.gestion_turnos.repository.TurnoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    @Autowired
    private TurnoRepository turnoRepository;

    // Listar turnos según rol
    @GetMapping
    public List<Turno> listarTurnos() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
            return turnoRepository.findAll();
        } else if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_MEDICO"))) {
            return turnoRepository.findByMedicoUsername(username);
        } else {
            return turnoRepository.findByPacienteUsername(username);
        }
    }

    // Crear turno (solo PACIENTE o ADMIN)
    @PostMapping
    public ResponseEntity<?> crearTurno(@Valid @RequestBody Turno turno, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errores = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            // Para devolver un JSON con un campo "errores"
            return ResponseEntity.badRequest().body(Map.of("errores", errores));
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Asignar automáticamente el username del paciente si es PACIENTE
        if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_PACIENTE"))) {
            turno.setPacienteUsername(username);
        }

        Turno nuevoTurno = turnoRepository.save(turno);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTurno);
    }

    // Obtener turno por ID
    @GetMapping("/{id}")
    public ResponseEntity<Turno> obtenerTurno(@PathVariable Long id) {
        return turnoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Actualizar turnos (ADMIN o paciente dueño del turno)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTurno(@PathVariable Long id, @Valid @RequestBody Turno turno, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errores = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errores);
        }

        return turnoRepository.findById(id).map(turnoExistente -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String usernameLogueado = auth.getName();

            // Solo paciente dueño del turno o admin puede actualizar
            boolean esAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!usernameLogueado.equals(turnoExistente.getPacienteUsername()) && !esAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tenés permisos para actualizar este turno");
            }

            // Actualizamos campos permitidos
            turnoExistente.setPaciente(turno.getPaciente());
            turnoExistente.setMedico(turno.getMedico());
            turnoExistente.setPacienteUsername(turno.getPacienteUsername());
            turnoExistente.setMedicoUsername(turno.getMedicoUsername());
            turnoExistente.setEspecialidad(turno.getEspecialidad());
            turnoExistente.setFechaHora(turno.getFechaHora());
            turnoExistente.setConfirmado(turno.isConfirmado());

            turnoRepository.save(turnoExistente);
            return ResponseEntity.ok(turnoExistente);

        }).orElseGet(() -> ResponseEntity.notFound().build());
    }


    //Eliminar turno (solo Admin o dueño del turno)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTurno(@PathVariable Long id) {
        return turnoRepository.findById(id).map(turno -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String usernameLogueado = auth.getName();

            // Solo paciente dueño del turno o admin puede eliminar
            if (usernameLogueado.equals(turno.getPacienteUsername()) ||
                    auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {

                turnoRepository.delete(turno);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tenés permisos para eliminar este turno");
            }
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
