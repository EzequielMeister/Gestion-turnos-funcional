package com.ezequiel.gestion_turnos.repository;

import com.ezequiel.gestion_turnos.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

// Es una interfaz de Spring Data JPA que te da acceso a operaciones CRUD básicas sin tener que escribirlas.
public interface TurnoRepository extends JpaRepository<Turno, Long> {
}
