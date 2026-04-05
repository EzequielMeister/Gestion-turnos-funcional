package com.ezequiel.gestion_turnos.service;

import com.ezequiel.gestion_turnos.dto.TurnoEstadoRequest;
import com.ezequiel.gestion_turnos.dto.TurnoRequest;
import com.ezequiel.gestion_turnos.dto.TurnoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface TurnoService {
    
    Page<TurnoResponse> listarTurnos(Pageable pageable);
    
    TurnoResponse obtenerTurno(Long id);
    
    TurnoResponse crearTurno(TurnoRequest request);
    
    TurnoResponse cambiarEstado(Long id, TurnoEstadoRequest request);
    
    void cancelarTurno(Long id);
    
    List<TurnoResponse> obtenerTurnosDelMedico(Long medicoId, LocalDateTime desde, LocalDateTime hasta);
    
    boolean tieneDisponibilidad(Long medicoId, LocalDateTime fechaHora);
}
