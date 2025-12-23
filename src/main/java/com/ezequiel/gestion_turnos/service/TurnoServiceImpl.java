package com.ezequiel.gestion_turnos.service;

import com.ezequiel.gestion_turnos.model.Turno;
import com.ezequiel.gestion_turnos.repository.TurnoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurnoServiceImpl implements TurnoService {

    private final TurnoRepository turnoRepository;

    public TurnoServiceImpl(TurnoRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    @Override
    public List<Turno> listarTurnos() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        if (tieneRol("ROLE_ADMIN")) {
            return turnoRepository.findAll();
        } else if (tieneRol("ROLE_MEDICO")) {
            return turnoRepository.findByMedicoUsername(username);
        } else {
            return turnoRepository.findByPacienteUsername(username);
        }
    }

    @Override
    public Turno crearTurno(Turno turno) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (tieneRol("ROLE_PACIENTE")) {
            turno.setPacienteUsername(auth.getName());
        }

        return turnoRepository.save(turno);
    }

    @Override
    public Turno obtenerTurno(Long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
    }

    @Override
    public Turno actualizarTurno(Long id, Turno turno) {
        Turno existente = obtenerTurno(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean esAdmin = tieneRol("ROLE_ADMIN");

        if (!auth.getName().equals(existente.getPacienteUsername()) && !esAdmin) {
            throw new RuntimeException("No tenés permisos para actualizar este turno");
        }

        existente.setEspecialidad(turno.getEspecialidad());
        existente.setFechaHora(turno.getFechaHora());
        existente.setConfirmado(turno.isConfirmado());
        existente.setMedicoUsername(turno.getMedicoUsername());

        return turnoRepository.save(existente);
    }

    @Override
    public void eliminarTurno(Long id) {
        Turno turno = obtenerTurno(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!auth.getName().equals(turno.getPacienteUsername()) && !tieneRol("ROLE_ADMIN")) {
            throw new RuntimeException("No tenés permisos para eliminar este turno");
        }

        turnoRepository.delete(turno);
    }

    private boolean tieneRol(String rol) {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(rol));
    }
}
