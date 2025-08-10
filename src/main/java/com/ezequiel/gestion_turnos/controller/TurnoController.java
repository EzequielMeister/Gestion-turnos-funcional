package com.ezequiel.gestion_turnos.controller;

import com.ezequiel.gestion_turnos.model.Turno;
import com.ezequiel.gestion_turnos.repository.TurnoRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<?> crearTurno(@Valid @RequestBody Turno turno) {
        Turno nuevoTurno = turnoRepository.save(turno);
        URI location = URI.create(String.format("/api/turnos/%s", nuevoTurno.getId()));
        return ResponseEntity.created(location).body(nuevoTurno);
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
    public ResponseEntity<?> actualizarTurno(@PathVariable Long id, @Valid @RequestBody Turno turnoDetalles) {
        return turnoRepository.findById(id)
                .map(turno -> {
                    turno.setPaciente(turnoDetalles.getPaciente());
                    turno.setMedico(turnoDetalles.getMedico());
                    turno.setEspecialidad(turnoDetalles.getEspecialidad());
                    turno.setFechaHora(turnoDetalles.getFechaHora());
                    turno.setConfirmado(turnoDetalles.isConfirmado());
                    Turno actualizado = turnoRepository.save(turno);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
