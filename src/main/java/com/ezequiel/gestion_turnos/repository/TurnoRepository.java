package com.ezequiel.gestion_turnos.repository;

import com.ezequiel.gestion_turnos.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Es una interfaz de Spring Data JPA que te da acceso a operaciones CRUD básicas sin tener que escribirlas.
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    List<Turno> findByMedicoUsername(String medicoUsername);

    List<Turno> findByPacienteUsername(String pacienteUsername);

}
