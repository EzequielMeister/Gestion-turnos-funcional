package com.ezequiel.gestion_turnos.repository;

import com.ezequiel.gestion_turnos.model.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {
    
    Optional<Especialidad> findByNombre(String nombre);
    
    List<Especialidad> findByActivaTrue();
    
    boolean existsByNombre(String nombre);
}
