package com.ezequiel.gestion_turnos.controller;

import com.ezequiel.gestion_turnos.model.Turno;
import com.ezequiel.gestion_turnos.repository.TurnoRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    @Autowired
    private TurnoRepository turnoRepository;

    @GetMapping
    public List<Turno> listarTurnos() {
        return turnoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> crearTurno(@Valid @RequestBody Turno turno, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Para enviar un mensaje con todos los errores de la solicitud de crear el turno.
            String errores = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errores);
        }
        Turno nuevoTurno = turnoRepository.save(turno);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTurno);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Turno> obtenerTurno(@PathVariable Long id) {
        return turnoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTurno(@PathVariable Long id) {
        if (!turnoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        turnoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTurno(@PathVariable Long id, @Valid @RequestBody Turno turno, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errores = bindingResult.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(errores);
        }

        return turnoRepository.findById(id).map(turnoExistente -> {
            turnoExistente.setPaciente(turno.getPaciente());
            turnoExistente.setMedico(turno.getMedico());
            turnoExistente.setEspecialidad(turno.getEspecialidad());
            turnoExistente.setFechaHora(turno.getFechaHora());
            turnoExistente.setConfirmado(turno.isConfirmado());

            turnoRepository.save(turnoExistente);
            return ResponseEntity.ok(turnoExistente);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }


}
