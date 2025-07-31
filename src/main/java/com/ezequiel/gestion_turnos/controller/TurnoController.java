package com.ezequiel.gestion_turnos.controller;

import com.ezequiel.gestion_turnos.model.Turno;
import com.ezequiel.gestion_turnos.repository.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turnos") // <-- Define la ruta base
public class TurnoController {

    @Autowired
    private TurnoRepository turnoRepository;

    @GetMapping
    public List<Turno> listarTurnos() {
        return turnoRepository.findAll();
    }

    @PostMapping
    public Turno crearTurno(@RequestBody Turno turno) {
        return turnoRepository.save(turno);
    }

    @GetMapping("/{id}")
    public Turno obtenerTurno(@PathVariable Long id) {
        return turnoRepository.findById(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void eliminarTurno(@PathVariable Long id) {
        turnoRepository.deleteById(id);
    }
}
