package com.ezequiel.gestion_turnos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EspecialidadRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    String nombre,
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    String descripcion,
    
    Integer duracionMinutos,
    
    Double precio
) {}
