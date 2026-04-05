package com.ezequiel.gestion_turnos.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ResponseWrapper<T>(
    boolean success,
    String message,
    T data,
    LocalDateTime timestamp
) {
    public static <T> ResponseWrapper<T> success(T data) {
        return new ResponseWrapper<>(true, "Operación exitosa", data, LocalDateTime.now());
    }
    
    public static <T> ResponseWrapper<T> success(String message, T data) {
        return new ResponseWrapper<>(true, message, data, LocalDateTime.now());
    }
    
    public static <T> ResponseWrapper<T> error(String message) {
        return new ResponseWrapper<>(false, message, null, LocalDateTime.now());
    }
    
    public static ResponseWrapper<Map<String, String>> validationError(Map<String, String> errors) {
        return new ResponseWrapper<>(false, "Error de validación", errors, LocalDateTime.now());
    }
}
