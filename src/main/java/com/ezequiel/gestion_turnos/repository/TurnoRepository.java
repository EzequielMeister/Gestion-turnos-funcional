package com.ezequiel.gestion_turnos.repository;

import com.ezequiel.gestion_turnos.model.Turno;
import com.ezequiel.gestion_turnos.model.enums.EstadoTurno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    Page<Turno> findByMedicoId(Long medicoId, Pageable pageable);

    Page<Turno> findByPacienteId(Long pacienteId, Pageable pageable);

    Page<Turno> findByEstado(EstadoTurno estado, Pageable pageable);

    @Query("SELECT t FROM Turno t WHERE t.medico.id = :medicoId AND t.fechaHora = :fechaHora AND t.estado <> 'CANCELADO'")
    Optional<Turno> findConflictingTurno(@Param("medicoId") Long medicoId, @Param("fechaHora") LocalDateTime fechaHora);

    @Query("SELECT COUNT(t) FROM Turno t WHERE t.medico.id = :medicoId AND t.fechaHora >= :inicio AND t.fechaHora < :fin AND t.estado <> 'CANCELADO'")
    long countTurnosMedicoEnHorario(@Param("medicoId") Long medicoId, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT t FROM Turno t WHERE t.medico.id = :medicoId AND t.fechaHora BETWEEN :desde AND :hasta AND t.estado <> 'CANCELADO' ORDER BY t.fechaHora")
    List<Turno> findTurnosDelMedicoEnRango(@Param("medicoId") Long medicoId, @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT t FROM Turno t JOIN FETCH t.paciente JOIN FETCH t.medico JOIN FETCH t.especialidad WHERE t.id = :id")
    Optional<Turno> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT t FROM Turno t JOIN FETCH t.paciente JOIN FETCH t.medico JOIN FETCH t.especialidad")
    Page<Turno> findAllWithDetails(Pageable pageable);

    boolean existsByMedicoIdAndFechaHoraAndEstadoNot(Long medicoId, LocalDateTime fechaHora, EstadoTurno estadoExcluido);
}
