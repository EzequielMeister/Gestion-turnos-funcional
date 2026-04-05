package com.ezequiel.gestion_turnos.dto;

import com.ezequiel.gestion_turnos.model.Especialidad;

public record EspecialidadResponse(
    Long id,
    String nombre,
    String descripcion,
    Integer duracionMinutos,
    Double precio,
    boolean activa
) {
    public static EspecialidadResponse fromEntity(Especialidad especialidad) {
        return new EspecialidadResponse(
            especialidad.getId(),
            especialidad.getNombre(),
            especialidad.getDescripcion(),
            especialidad.getDuracionMinutos(),
            especialidad.getPrecio(),
            especialidad.isActiva()
        );
    }
}
