package com.ezequiel.gestion_turnos.exception;

import com.ezequiel.gestion_turnos.dto.ResponseWrapper;
import com.ezequiel.gestion_turnos.service.DatosDuplicadosException;
import com.ezequiel.gestion_turnos.service.PermisoDenegadoException;
import com.ezequiel.gestion_turnos.service.RecursoNoEncontradoException;
import com.ezequiel.gestion_turnos.service.ReglaDeNegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        logger.warn("Recurso no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseWrapper.error(ex.getMessage()));
    }

    @ExceptionHandler(DatosDuplicadosException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleDatosDuplicados(DatosDuplicadosException ex) {
        logger.warn("Datos duplicados: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseWrapper.error(ex.getMessage()));
    }

    @ExceptionHandler(ReglaDeNegocioException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleReglaDeNegocio(ReglaDeNegocioException ex) {
        logger.warn("Regla de negocio violada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error(ex.getMessage()));
    }

    @ExceptionHandler(PermisoDenegadoException.class)
    public ResponseEntity<ResponseWrapper<Void>> handlePermisoDenegado(PermisoDenegadoException ex) {
        logger.warn("Permiso denegado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseWrapper.error(ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleAccessDenied(AccessDeniedException ex) {
        logger.warn("Acceso denegado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseWrapper.error("No tiene permisos para realizar esta acción"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleBadCredentials(BadCredentialsException ex) {
        logger.warn("Credenciales inválidas: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseWrapper.error("Usuario o contraseña incorrectos"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<Map<String, String>>> handleValidations(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);
        });
        logger.warn("Errores de validación: {}", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.validationError(errores));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<Void>> handleGenericException(Exception ex) {
        logger.error("Error interno del servidor", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseWrapper.error("Error interno del servidor. Por favor, intente más tarde."));
    }
}
