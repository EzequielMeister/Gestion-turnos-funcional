package com.ezequiel.gestion_turnos.service;

import com.ezequiel.gestion_turnos.dto.EspecialidadRequest;
import com.ezequiel.gestion_turnos.dto.EspecialidadResponse;
import com.ezequiel.gestion_turnos.model.Especialidad;
import com.ezequiel.gestion_turnos.repository.EspecialidadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EspecialidadService {

    private final EspecialidadRepository especialidadRepository;

    public EspecialidadService(EspecialidadRepository especialidadRepository) {
        this.especialidadRepository = especialidadRepository;
    }

    @Transactional(readOnly = true)
    public List<EspecialidadResponse> listarActivas() {
        return especialidadRepository.findByActivaTrue()
                .stream()
                .map(EspecialidadResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EspecialidadResponse> listarTodas() {
        return especialidadRepository.findAll()
                .stream()
                .map(EspecialidadResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EspecialidadResponse obtenerPorId(Long id) {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Especialidad no encontrada con ID: " + id));
        return EspecialidadResponse.fromEntity(especialidad);
    }

    public EspecialidadResponse crear(EspecialidadRequest request) {
        if (especialidadRepository.existsByNombre(request.nombre())) {
            throw new DatosDuplicadosException("Ya existe una especialidad con el nombre: " + request.nombre());
        }

        Especialidad especialidad = new Especialidad();
        especialidad.setNombre(request.nombre());
        especialidad.setDescripcion(request.descripcion());
        especialidad.setDuracionMinutos(request.duracionMinutos() != null ? request.duracionMinutos() : 30);
        especialidad.setPrecio(request.precio());
        especialidad.setActiva(true);

        return EspecialidadResponse.fromEntity(especialidadRepository.save(especialidad));
    }

    public EspecialidadResponse actualizar(Long id, EspecialidadRequest request) {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Especialidad no encontrada con ID: " + id));

        if (!especialidad.getNombre().equals(request.nombre()) && especialidadRepository.existsByNombre(request.nombre())) {
            throw new DatosDuplicadosException("Ya existe una especialidad con el nombre: " + request.nombre());
        }

        especialidad.setNombre(request.nombre());
        especialidad.setDescripcion(request.descripcion());
        if (request.duracionMinutos() != null) {
            especialidad.setDuracionMinutos(request.duracionMinutos());
        }
        if (request.precio() != null) {
            especialidad.setPrecio(request.precio());
        }

        return EspecialidadResponse.fromEntity(especialidadRepository.save(especialidad));
    }

    public void eliminar(Long id) {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Especialidad no encontrada con ID: " + id));
        especialidad.setActiva(false);
        especialidadRepository.save(especialidad);
    }

    @Transactional(readOnly = true)
    public Especialidad obtenerEntidadPorId(Long id) {
        return especialidadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Especialidad no encontrada con ID: " + id));
    }
}
