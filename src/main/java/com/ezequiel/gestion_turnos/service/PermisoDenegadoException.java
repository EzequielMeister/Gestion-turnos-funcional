package com.ezequiel.gestion_turnos.service;

public class PermisoDenegadoException extends RuntimeException {
    public PermisoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
