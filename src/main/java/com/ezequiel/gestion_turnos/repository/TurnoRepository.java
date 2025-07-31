package com.ezequiel.gestion_turnos.repository;

import com.ezequiel.gestion_turnos.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurnoRepository extends JpaRepository<Turno, Long> {
}
