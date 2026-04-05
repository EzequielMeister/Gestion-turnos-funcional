package com.ezequiel.gestion_turnos.controller;

import com.ezequiel.gestion_turnos.dto.ResponseWrapper;
import com.ezequiel.gestion_turnos.dto.TurnoEstadoRequest;
import com.ezequiel.gestion_turnos.dto.TurnoRequest;
import com.ezequiel.gestion_turnos.dto.TurnoResponse;
import com.ezequiel.gestion_turnos.service.TurnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/turnos")
@Tag(name = "Turnos", description = "API para gestión de turnos médicos")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @GetMapping
    @Operation(summary = "Listar turnos", description = "Lista turnos con paginación. El filtrado depende del rol del usuario.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de turnos obtenida correctamente")
    })
    public ResponseEntity<ResponseWrapper<Page<TurnoResponse>>> listar(
            @Parameter(description = "Número de página (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "fechaHora") String sortBy,
            @Parameter(description = "Dirección del ordenamiento") @RequestParam(defaultValue = "ASC") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TurnoResponse> turnos = turnoService.listarTurnos(pageable);
        return ResponseEntity.ok(ResponseWrapper.success(turnos));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener turno por ID", description = "Devuelve los detalles de un turno específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Turno encontrado"),
        @ApiResponse(responseCode = "404", description = "Turno no encontrado")
    })
    public ResponseEntity<ResponseWrapper<TurnoResponse>> obtener(@PathVariable Long id) {
        TurnoResponse turno = turnoService.obtenerTurno(id);
        return ResponseEntity.ok(ResponseWrapper.success(turno));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo turno", description = "Permite a un paciente solicitar un nuevo turno")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Turno creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio violada")
    })
    public ResponseEntity<ResponseWrapper<TurnoResponse>> crear(@Valid @RequestBody TurnoRequest request) {
        TurnoResponse turno = turnoService.crearTurno(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.success("Turno creado exitosamente", turno));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado del turno", description = "Actualiza el estado de un turno (confirmar, completar, cancelar)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos para esta acción")
    })
    public ResponseEntity<ResponseWrapper<TurnoResponse>> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody TurnoEstadoRequest request) {
        TurnoResponse turno = turnoService.cambiarEstado(id, request);
        return ResponseEntity.ok(ResponseWrapper.success("Estado actualizado", turno));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar turno", description = "Cancela un turno existente")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Turno cancelado"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos para cancelar")
    })
    public ResponseEntity<ResponseWrapper<Void>> cancelar(@PathVariable Long id) {
        turnoService.cancelarTurno(id);
        return ResponseEntity.ok(ResponseWrapper.success("Turno cancelado", null));
    }

    @GetMapping("/disponibilidad")
    @Operation(summary = "Verificar disponibilidad", description = "Verifica si un médico tiene disponibilidad en una fecha/hora específica")
    public ResponseEntity<ResponseWrapper<Boolean>> verificarDisponibilidad(
            @RequestParam Long medicoId,
            @RequestParam LocalDateTime fechaHora) {
        boolean disponible = turnoService.tieneDisponibilidad(medicoId, fechaHora);
        return ResponseEntity.ok(ResponseWrapper.success(disponible));
    }
}
