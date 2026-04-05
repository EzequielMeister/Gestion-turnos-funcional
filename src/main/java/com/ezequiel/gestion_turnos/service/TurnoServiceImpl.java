package com.ezequiel.gestion_turnos.service;

import com.ezequiel.gestion_turnos.dto.TurnoEstadoRequest;
import com.ezequiel.gestion_turnos.dto.TurnoRequest;
import com.ezequiel.gestion_turnos.dto.TurnoResponse;
import com.ezequiel.gestion_turnos.model.Especialidad;
import com.ezequiel.gestion_turnos.model.Turno;
import com.ezequiel.gestion_turnos.model.User;
import com.ezequiel.gestion_turnos.model.enums.EstadoTurno;
import com.ezequiel.gestion_turnos.repository.TurnoRepository;
import com.ezequiel.gestion_turnos.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class TurnoServiceImpl implements TurnoService {

    private static final LocalTime HORA_INICIO = LocalTime.of(8, 0);
    private static final LocalTime HORA_FIN = LocalTime.of(18, 0);
    private static final int HORAS_ANTELACION_CANCELACION = 24;

    private final TurnoRepository turnoRepository;
    private final UserRepository userRepository;
    private final EspecialidadService especialidadService;

    public TurnoServiceImpl(TurnoRepository turnoRepository, UserRepository userRepository, EspecialidadService especialidadService) {
        this.turnoRepository = turnoRepository;
        this.userRepository = userRepository;
        this.especialidadService = especialidadService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TurnoResponse> listarTurnos(Pageable pageable) {
        User usuario = obtenerUsuarioActual();
        
        if (tieneRol("ROLE_ADMIN")) {
            return turnoRepository.findAllWithDetails(pageable)
                    .map(TurnoResponse::fromEntity);
        } else if (tieneRol("ROLE_MEDICO")) {
            return turnoRepository.findByMedicoId(usuario.getId(), pageable)
                    .map(TurnoResponse::fromEntity);
        } else {
            return turnoRepository.findByPacienteId(usuario.getId(), pageable)
                    .map(TurnoResponse::fromEntity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TurnoResponse obtenerTurno(Long id) {
        Turno turno = turnoRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Turno no encontrado con ID: " + id));
        return TurnoResponse.fromEntity(turno);
    }

    @Override
    public TurnoResponse crearTurno(TurnoRequest request) {
        validarHorarioAtencion(request.fechaHora());
        validarNoEsPasado(request.fechaHora());
        
        User paciente = obtenerUsuarioActual();
        User medico = userRepository.findById(request.medicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Médico no encontrado con ID: " + request.medicoId()));
        
        validarMedico(medico);
        
        Especialidad especialidad = especialidadService.obtenerEntidadPorId(request.especialidadId());
        
        Turno turno = new Turno();
        turno.setPaciente(paciente);
        turno.setMedico(medico);
        turno.setEspecialidad(especialidad);
        turno.setFechaHora(request.fechaHora());
        turno.setEstado(EstadoTurno.PENDIENTE);
        turno.setNotasPaciente(request.notasPaciente());
        
        return TurnoResponse.fromEntity(turnoRepository.save(turno));
    }

    @Override
    public TurnoResponse cambiarEstado(Long id, TurnoEstadoRequest request) {
        Turno turno = turnoRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Turno no encontrado con ID: " + id));
        
        User usuario = obtenerUsuarioActual();
        validarCambioEstado(turno, request, usuario);
        
        turno.setEstado(request.estado());
        if (request.notasMedico() != null && tieneRol("ROLE_MEDICO")) {
            turno.setNotasMedico(request.notasMedico());
        }
        
        return TurnoResponse.fromEntity(turnoRepository.save(turno));
    }

    @Override
    public void cancelarTurno(Long id) {
        Turno turno = turnoRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Turno no encontrado con ID: " + id));
        
        User usuario = obtenerUsuarioActual();
        
        if (!usuario.getId().equals(turno.getPaciente().getId()) && !tieneRol("ROLE_ADMIN")) {
            throw new PermisoDenegadoException("Solo el paciente o un administrador pueden cancelar el turno");
        }
        
        if (turno.getEstado() == EstadoTurno.CANCELADO || turno.getEstado() == EstadoTurno.COMPLETADO) {
            throw new ReglaDeNegocioException("No se puede cancelar un turno que ya está " + turno.getEstado());
        }
        
        turno.setEstado(EstadoTurno.CANCELADO);
        turnoRepository.save(turno);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoResponse> obtenerTurnosDelMedico(Long medicoId, LocalDateTime desde, LocalDateTime hasta) {
        return turnoRepository.findTurnosDelMedicoEnRango(medicoId, desde, hasta)
                .stream()
                .map(TurnoResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean tieneDisponibilidad(Long medicoId, LocalDateTime fechaHora) {
        if (fechaHora.toLocalTime().isBefore(HORA_INICIO) || fechaHora.toLocalTime().isAfter(HORA_FIN)) {
            return false;
        }
        return !turnoRepository.existsByMedicoIdAndFechaHoraAndEstadoNot(
                medicoId, fechaHora, EstadoTurno.CANCELADO);
    }

    private void validarHorarioAtencion(LocalDateTime fechaHora) {
        LocalTime hora = fechaHora.toLocalTime();
        if (hora.isBefore(HORA_INICIO) || hora.isAfter(HORA_FIN)) {
            throw new ReglaDeNegocioException(
                    "BR-02: Los turnos deben programarse entre las " + HORA_INICIO + " y las " + HORA_FIN);
        }
    }

    private void validarNoEsPasado(LocalDateTime fechaHora) {
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new ReglaDeNegocioException("No se pueden solicitar turnos en fechas pasadas");
        }
    }

    private void validarMedico(User medico) {
        boolean esMedico = medico.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_MEDICO"));
        if (!esMedico) {
            throw new ReglaDeNegocioException("El usuario seleccionado no es un médico válido");
        }
    }

    private void validarCambioEstado(Turno turno, TurnoEstadoRequest request, User usuario) {
        EstadoTurno nuevoEstado = request.estado();
        EstadoTurno estadoActual = turno.getEstado();
        
        if (nuevoEstado == EstadoTurno.CANCELADO) {
            if (!usuario.getId().equals(turno.getPaciente().getId()) && !tieneRol("ROLE_ADMIN")) {
                throw new PermisoDenegadoException("Solo el paciente o un administrador pueden cancelar");
            }
        }
        
        if (nuevoEstado == EstadoTurno.COMPLETADO) {
            if (!tieneRol("ROLE_MEDICO") && !tieneRol("ROLE_ADMIN")) {
                throw new PermisoDenegadoException("Solo un médico o administrador pueden marcar como completado");
            }
        }
    }

    private User obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    private boolean tieneRol(String rol) {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(rol));
    }
}
