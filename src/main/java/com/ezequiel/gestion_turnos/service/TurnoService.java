package com.ezequiel.gestion_turnos.service;

import com.ezequiel.gestion_turnos.model.Turno;

import java.util.List;

public interface TurnoService {

    List<Turno> listarTurnos();

    Turno crearTurno(Turno turno);

    Turno obtenerTurno(Long id);

    Turno actualizarTurno(Long id, Turno turno);

    void eliminarTurno(Long id);
}
