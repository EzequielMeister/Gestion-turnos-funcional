package com.ezequiel.gestion_turnos.controller;

import com.ezequiel.gestion_turnos.dto.ResponseWrapper;
import com.ezequiel.gestion_turnos.dto.EspecialidadRequest;
import com.ezequiel.gestion_turnos.dto.EspecialidadResponse;
import com.ezequiel.gestion_turnos.service.EspecialidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@Tag(name = "Especialidades", description = "API para gestión de especialidades médicas")
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    public EspecialidadController(EspecialidadService especialidadService) {
        this.especialidadService = especialidadService;
    }

    @GetMapping
    @Operation(summary = "Listar especialidades activas", description = "Devuelve lista de especialidades disponibles para solicitar turnos")
    public ResponseEntity<ResponseWrapper<List<EspecialidadResponse>>> listarActivas() {
        List<EspecialidadResponse> especialidades = especialidadService.listarActivas();
        return ResponseEntity.ok(ResponseWrapper.success(especialidades));
    }

    @GetMapping("/todas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todas las especialidades", description = "Incluye también las inactivas (solo admin)")
    public ResponseEntity<ResponseWrapper<List<EspecialidadResponse>>> listarTodas() {
        List<EspecialidadResponse> especialidades = especialidadService.listarTodas();
        return ResponseEntity.ok(ResponseWrapper.success(especialidades));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener especialidad por ID")
    public ResponseEntity<ResponseWrapper<EspecialidadResponse>> obtener(@PathVariable Long id) {
        EspecialidadResponse especialidad = especialidadService.obtenerPorId(id);
        return ResponseEntity.ok(ResponseWrapper.success(especialidad));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear especialidad", description = "Solo administradores pueden crear especialidades")
    public ResponseEntity<ResponseWrapper<EspecialidadResponse>> crear(@Valid @RequestBody EspecialidadRequest request) {
        EspecialidadResponse especialidad = especialidadService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.success("Especialidad creada exitosamente", especialidad));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar especialidad", description = "Solo administradores pueden actualizar")
    public ResponseEntity<ResponseWrapper<EspecialidadResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EspecialidadRequest request) {
        EspecialidadResponse especialidad = especialidadService.actualizar(id, request);
        return ResponseEntity.ok(ResponseWrapper.success("Especialidad actualizada", especialidad));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar especialidad", description = "Desactiva la especialidad (soft delete)")
    public ResponseEntity<ResponseWrapper<Void>> eliminar(@PathVariable Long id) {
        especialidadService.eliminar(id);
        return ResponseEntity.ok(ResponseWrapper.success("Especialidad eliminada", null));
    }
}
