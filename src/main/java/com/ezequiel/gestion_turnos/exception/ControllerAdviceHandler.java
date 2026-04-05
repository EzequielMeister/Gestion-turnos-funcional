package com.ezequiel.gestion_turnos.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerAdviceHandler {

    // Maneja errores de validación de @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> manageValidations(MethodArgumentNotValidException exc) {
        String errores = exc.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }

    // Maneja cualquier otra excepción no controlada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> manejarErrorGenerico(Exception exc) {
        return new ResponseEntity<>("Error interno del servidor: " + exc.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
