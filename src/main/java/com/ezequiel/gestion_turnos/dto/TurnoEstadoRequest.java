package com.ezequiel.gestion_turnos.dto;

import com.ezequiel.gestion_turnos.model.enums.EstadoTurno;
import jakarta.validation.constraints.NotNull;

public record TurnoEstadoRequest(
    @NotNull(message = "El estado es obligatorio")
    EstadoTurno estado,
    
    String notasMedico
) {}
