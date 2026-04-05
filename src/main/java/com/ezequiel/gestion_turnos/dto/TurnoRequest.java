package com.ezequiel.gestion_turnos.dto;

import com.ezequiel.gestion_turnos.model.enums.EstadoTurno;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record TurnoRequest(
    @NotNull(message = "El ID de especialidad es obligatorio")
    Long especialidadId,
    
    @NotNull(message = "El ID del médico es obligatorio")
    Long medicoId,
    
    @NotNull(message = "La fecha y hora es obligatoria")
    @Future(message = "La fecha debe ser futura")
    LocalDateTime fechaHora,
    
    String notasPaciente
) {}
