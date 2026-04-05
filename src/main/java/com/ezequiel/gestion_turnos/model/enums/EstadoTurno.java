package com.ezequiel.gestion_turnos.model.enums;

public enum EstadoTurno {
    PENDIENTE("Pendiente de confirmación"),
    CONFIRMADO("Turno confirmado"),
    COMPLETADO("Turno completado"),
    CANCELADO("Turno cancelado");

    private final String descripcion;

    EstadoTurno(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
