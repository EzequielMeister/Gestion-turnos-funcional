package com.ezequiel.gestion_turnos.dto;

import com.ezequiel.gestion_turnos.model.Turno;
import com.ezequiel.gestion_turnos.model.enums.EstadoTurno;
import java.time.LocalDateTime;

public record TurnoResponse(
    Long id,
    String pacienteNombre,
    String pacienteUsername,
    String medicoNombre,
    String medicoUsername,
    Long especialidadId,
    String especialidadNombre,
    LocalDateTime fechaHora,
    EstadoTurno estado,
    String notasPaciente,
    String notasMedico,
    LocalDateTime fechaCreacion
) {
    public static TurnoResponse fromEntity(Turno turno) {
        return new TurnoResponse(
            turno.getId(),
            turno.getPaciente().getUsername(),
            turno.getPaciente().getUsername(),
            turno.getMedico().getUsername(),
            turno.getMedico().getUsername(),
            turno.getEspecialidad().getId(),
            turno.getEspecialidad().getNombre(),
            turno.getFechaHora(),
            turno.getEstado(),
            turno.getNotasPaciente(),
            turno.getNotasMedico(),
            turno.getFechaCreacion()
        );
    }
}
