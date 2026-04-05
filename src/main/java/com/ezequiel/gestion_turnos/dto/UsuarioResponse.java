package com.ezequiel.gestion_turnos.dto;

public record UsuarioResponse(
    String username,
    String rol,
    Long id
) {}
