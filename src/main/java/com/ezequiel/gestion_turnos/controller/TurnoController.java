package com.ezequiel.gestion_turnos.controller;

import com.ezequiel.gestion_turnos.model.Turno;
import com.ezequiel.gestion_turnos.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @GetMapping
    public List<Turno> listar() {
        return turnoService.listarTurnos();
    }

    @PostMapping
    public ResponseEntity<Turno> crear(@Valid @RequestBody Turno turno) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(turnoService.crearTurno(turno));
    }

    @GetMapping("/{id}")
    public Turno obtener(@PathVariable Long id) {
        return turnoService.obtenerTurno(id);
    }

    @PutMapping("/{id}")
    public Turno actualizar(@PathVariable Long id, @Valid @RequestBody Turno turno) {
        return turnoService.actualizarTurno(id, turno);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        turnoService.eliminarTurno(id);
        return ResponseEntity.noContent().build();
    }
}
